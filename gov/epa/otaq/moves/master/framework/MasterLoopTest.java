/**************************************************************************************************
 * @(#)MasterLoopTest.java
 *
 *
 *
 *************************************************************************************************/
package gov.epa.otaq.moves.master.framework;

//import gov.epa.otaq.moves.master.implementation.general.FuelGenerator;
import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import junit.framework.*;

/**
 * Test Case for the MasterLoop class
 *
 * @author		Cimulus
 * @version		2006-01-16
**/
public class MasterLoopTest extends TestCase implements MasterLoopable {
	/**
	 * Standard TestCase constructor
	 * @param name Name of the test case.
	**/
	public MasterLoopTest(String name) {
		super(name);
	}

	/**
	 * Implements the test case(s).
	 * @throws InterruptedException This is thrown if the thread is interrupted.
	 * @throws SQLException If a database error occurs.
	**/
	public void testLoop() throws InterruptedException, SQLException {
		ExecutionRunSpecTest.setupExecutionRunSpec();
		System.out.println("ExecutionRunSpec.theExecutionRunSpec.targetProcesses.size() = " +
				ExecutionRunSpec.theExecutionRunSpec.targetProcesses.size());

		MOVESEngine.theInstance = new MOVESEngine();
		MasterLoop targetLoop = new MasterLoop();
		MOVESEngine.theInstance.loop = targetLoop;

		subscribeToMe(targetLoop);

		// There is substantial overlap between code that would start up the master loop
		// and the MOVESAPITest, so we just stop at ensuring subscription services don't
		// blowup and leave actual execution to another test.
	}

	/**
	 * Requests that this object subscribe to the given loop at desired looping points.
	 * Objects can assume that all necessary MasterLoopable objects have been instantiated.
	 *
	 * @param targetLoop The loop to subscribe to.
	**/
	public void subscribeToMe(MasterLoop targetLoop) {
		assertNotNull(targetLoop);
		// Subscribe to all active processes.
		for (Iterator i = ExecutionRunSpec.theExecutionRunSpec.targetProcesses.iterator();
				i.hasNext();) {
			EmissionProcess iterProcess = (EmissionProcess) i.next();
			assertNotNull(iterProcess);

			targetLoop.subscribe(this, iterProcess,
				MasterLoopGranularity.DAY, MasterLoopPriority.GENERATOR);
			targetLoop.subscribe(this, iterProcess,
				MasterLoopGranularity.HOUR, MasterLoopPriority.EMISSION_CALCULATOR);
		}
	}

	/**
	 * Called during each relevant iteration of the MasterLoop.
	 *
	 * @param context The current context of the loop.
	**/
	public void executeLoop(MasterLoopContext context) {
		if(context.executionGranularity == MasterLoopGranularity.DAY) {
			//numberOfDayExecutions++;
		} else if(context.executionGranularity == MasterLoopGranularity.HOUR) {
			//numberOfHourExecutions++;
		} else {
			fail("Invoked at invalid loop granularity.");
		}
	}

	/**
	 * This method is to clean up the data generated by the generator.
	 * @param context The MasterLoopContext that applies to this execution.
	**/
	public void cleanDataLoop(MasterLoopContext context) {
	}


}
