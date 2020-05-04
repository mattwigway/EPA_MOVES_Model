/**************************************************************************************************
 * @(#)WellToPumpProcessor.java
 *
 *
 *
 *************************************************************************************************/
package gov.epa.otaq.moves.master.implementation.ghg;

import gov.epa.otaq.moves.common.*;
import gov.epa.otaq.moves.master.framework.*;

import java.util.*;
import java.sql.*;

/**
 * Perform step ECCP-6 calculating Well-To-Pump emissions if called for in the
 * ExecutionRunSpec.  This is done as a chained calculator, so that well-to-pump
 * can be added to anything that produces total energy, petroleum energy, or
 * fossil energy.
 * @author		Wesley Faler
 * @author      EPA - Mitch C. 
 * @version		2012-03-11
**/
public class WellToPumpProcessor extends EmissionCalculator {
	/**
	 * Constructor, including registration of potential processes and pollutants
	 * handled by this calculator.  Such registration facilitates calculator
	 * chaining.
	**/
	public WellToPumpProcessor() {
		Pollutant totalEnergy = Pollutant.findByID(91);
		Pollutant petroleumEnergy = Pollutant.findByID(92);
		Pollutant fossilEnergy = Pollutant.findByID(93);

		EmissionProcess wellToPump = EmissionProcess.findByID(99);

		EmissionCalculatorRegistration.register(totalEnergy,wellToPump,this);
		EmissionCalculatorRegistration.register(petroleumEnergy,wellToPump,this);
		EmissionCalculatorRegistration.register(fossilEnergy,wellToPump,this);
	}

	/**
	 * MasterLoopable override that performs loop registration.
	 * @param targetLoop The loop to subscribe to.
	**/
	public void subscribeToMe(MasterLoop targetLoop) {
		// This is a chained calculator, so don't subscribe to the MasterLoop.  Instead,
		// find calculators that produce data this one needs.
		Pollutant totalEnergy = Pollutant.findByID(91);
		TreeSet<EmissionCalculator> calculators = new TreeSet<EmissionCalculator>();
		LinkedList<EmissionCalculator> t = 
				EmissionCalculatorRegistration.findPollutant(totalEnergy);
		calculators.addAll(t);
		// earlier version also chained to any calculators producing
		// fossil or petroleum energy consumption results, but this is not correct

		for(Iterator<EmissionCalculator> i=calculators.iterator();i.hasNext();) {
			EmissionCalculator c = (EmissionCalculator)i.next();
			if(c != this) {
				c.chainCalculator(this);
			}
		}
	}

	/**
	 * Builds SQL statements for a distributed worker to execute. This is called by
	 * EnergyConsumptionCalculator.executeLoop. Implementations of this method
	 * should contain uncertainty logic when UncertaintyParameters specifies that
	 * this mode is enabled.
	 * @param context The MasterLoopContext that applies to this execution.
	 * @return The resulting sql lists as an SQLForWorker object.
	**/
	public SQLForWorker doExecute(MasterLoopContext context) {
		// Determine which pollutant(s) should be calculated
		boolean shouldCalcPetroleumEnergy = false;
		boolean shouldCalcFossilFuelEnergy = false;
		boolean shouldCalcTotalEnergy = false;
		
		EmissionProcess wellToPump = EmissionProcess.findByID(99);

		boolean foundPollutant = false;
		if(ExecutionRunSpec.theExecutionRunSpec.doesHavePollutantAndProcess(
				"Petroleum Energy Consumption",wellToPump.processName)) {
			shouldCalcPetroleumEnergy = true;
			foundPollutant = true;
		}
		if(ExecutionRunSpec.theExecutionRunSpec.doesHavePollutantAndProcess(
				"Fossil Fuel Energy Consumption",wellToPump.processName)) {
			shouldCalcFossilFuelEnergy = true;
			foundPollutant = true;
		}
		if(ExecutionRunSpec.theExecutionRunSpec.doesHavePollutantAndProcess(
				"Total Energy Consumption",wellToPump.processName)) {
			shouldCalcTotalEnergy = true;
			foundPollutant = true;
		}

		if(!foundPollutant) {
			return null;
		}

		SQLForWorker sqlForWorker = new SQLForWorker();

		TreeMapIgnoreCase replacements = new TreeMapIgnoreCase();
		TreeSetIgnoreCase enabledSectionNames = new TreeSetIgnoreCase();
		String pollutantIDs = "";

		if(shouldCalcTotalEnergy) {
			if(pollutantIDs.length() > 0) {
				pollutantIDs += ",";
			}
			pollutantIDs += "91";
		}
		if(shouldCalcPetroleumEnergy) {
			if(pollutantIDs.length() > 0) {
				pollutantIDs += ",";
			}
			pollutantIDs += "92";
		}
		if(shouldCalcFossilFuelEnergy) {
			if(pollutantIDs.length() > 0) {
				pollutantIDs += ",";
			}
			pollutantIDs += "93";
		}

		replacements.put("##pollutantIDs##",pollutantIDs);

		boolean isOK = readAndHandleScriptedCalculations(context,replacements,
				"database/WellToPumpCalculator.sql",enabledSectionNames,
				sqlForWorker);

		if(isOK) {
			return sqlForWorker;
		} else {
			return null;
		}
	}
}
