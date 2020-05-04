/**************************************************************************************************
 * @(#)TimeSpans.java
 *
 *
 *
 *************************************************************************************************/
package gov.epa.otaq.moves.master.gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import gov.epa.otaq.moves.master.runspec.*;
import gov.epa.otaq.moves.master.framework.EmissionProcess;
import gov.epa.otaq.moves.master.framework.PollutantProcessAssociation;
import gov.epa.otaq.moves.master.framework.DatabaseConnectionManager;
import gov.epa.otaq.moves.common.*;

/**
 * Class for MOVES TimeSpans panel. Constructs a TimeSpans panel, creates and
 * sets the layouts of the controls. There are several panels on the Time Spans
 * panel. The Years panel contains a Year combo box, an Add, and a Remove button
 * to add, remove to or from the Year list box. The Months Panel lets the user
 * to select one or more of the month check boxes. The Select All and the Clear
 * All buttons let the user to select or to clear all of the twelve months. the
 * Days panel lets the user to select one or more of the days check boxes. The
 * Select All and the Clear All buttons let the user to select or to clear all
 * the of the seven days selections. The Hours panel lets the user to select one
 * of the twenty four hours from the Start Hour drop down list for the start hour,
 * and one of the twenty four hours from the End Hour drop down list. The Select
 * All button allows the selection of twenty four hours, and the Clear All button
 * resets the Start Hour and the End Hour selections. This class load/Saves
 * information to/from the runSpec.  TimeSpans was modified to allow calendar years
 * up to 2050.
 *
 * @author		Wesley Faler
 * @author		Sarah Luo
 * @author		Don Smith
 * @author		Tim Hull
 * @author		Bob G (Task 18 Item 99)
 * @author		EPA-elg (Update calendar year list)
 * @version     2015-05-21
**/
public class TimeSpans extends JPanel implements  ListSelectionListener,
		ActionListener, RunSpecEditor {
	/** Constant used for a lack of a year selection **/
	static String EMPTY_YEAR_TEXT = "          ";

	static class IntegerWrapper {
		public Integer v;

		public IntegerWrapper() {
			v = new Integer(0);
		}

		public IntegerWrapper(Integer i) {
			v = new Integer(i.intValue());
		}

		public String toString() {
			return v.toString();
		}
	}

	/** Panel contains the Time Aggregation controls. **/
	JPanel aggregatePanel;
	/** Time Aggregation Radio button group. **/
	ButtonGroup aggregateButtons;
	/** Aggregate by Year radio button. **/
	JRadioButton aggregateYear;
	/** Aggregate by Month radio button. **/
	JRadioButton aggregateMonth;
	/** Aggregate by Day radio button. **/
	JRadioButton aggregateDay;
	/** Aggregate by Hour radio button. **/
	JRadioButton aggregateHour;
	/** Add button control. **/
	JButton addYearButton;
	/** Panel contains the Year UI controls. **/
	JPanel yearPanel;
	/** Panel contains the Month UI controls. **/
	JPanel monthPanel;
	/** Select all months button. **/
	JButton selectAllMonthsButton;
	/** Clear all months button. **/
	JButton clearAllMonthsButton;
	/** Panel contains the Day UI controls. **/
	JPanel dayPanel;
	/** Select all days button. **/
	JButton selectAllDaysButton;
	/** Clear all days button. **/
	JButton clearAllDaysButton;
	/** Panel contains the Hour UI controls. **/
	JPanel hourPanel;
	/** Select all hours button. **/
	JButton selectAllHoursButton;
	/** Clear all hours button. **/
	JButton clearAllHoursButton;
	/** Label for the Year combo control. **/
	JLabel yearLabel;
	/** Year combo control. **/
	ExtendedComboBox<String> yearCombo;
	/** Label for Year Selection default list model. **/
	JLabel yearSelectionLabel;
	/** Year Selection default list model. **/
	DefaultListModel<IntegerWrapper> yearListModel;
	/** Year list. **/
	JList<IntegerWrapper> yearList;
	/** Scroll pane for Year UI controls. **/
	JScrollPane yearScrollPane;
	/** Year Remove button. **/
	JButton yearRemove;

	private static class MonthCheckBox {
		public TimeSpan.Month month;
		public JCheckBox checkBox;

		public MonthCheckBox() {
		}

		public MonthCheckBox(TimeSpan.Month monthToUse, JCheckBox checkBoxToUse) {
			month = monthToUse;
			checkBox = checkBoxToUse;
		}
	}

	/** List of MonthCheckBox objects **/
	ArrayList<MonthCheckBox> monthCheckBoxes = new ArrayList<MonthCheckBox>();

	private static class DayCheckBox {
		public TimeSpan.Day day;
		public JCheckBox checkBox;

		public DayCheckBox() {
		}

		public DayCheckBox(TimeSpan.Day dayToUse, JCheckBox checkBoxToUse) {
			day = dayToUse;
			checkBox = checkBoxToUse;
		}
	}

	/** List of DayCheckBox objects **/
	ArrayList<DayCheckBox> dayCheckBoxes = new ArrayList<DayCheckBox>();

	/** Label for the starting hour dropdown. **/
	JLabel beginHourLabel;
	/** Start hour combo control. **/
	ExtendedComboBox<TimeSpan.Hour> beginHourCombo;
	/** Label for the ending hour dropdown. **/
	JLabel endHourLabel;
	/** End hour combo control. **/
	ExtendedComboBox<TimeSpan.Hour> endHourCombo;
	/** Last calculated time span section status **/
	int timeSpanSectionStatus;
	/** Domain of the most recently loaded RunSpec **/
	ModelDomain loadedDomain = null;
	/** Hour object shown when there is no hour selection **/
	TimeSpan.Hour emptyHour = new TimeSpan.Hour(0,"");

	/**
	 * Constructs a TimeSpans panel, also creates and sets the layouts of the controls.
	**/
	public TimeSpans() {
		if(!TimeSpan.isLoaded()) {
			TimeSpan.loadTimeObjects();
		}
		createControls();
		arrangeControls();
		populateControls();
	}

	/**
	 * Gets text to be included in the RunSpec print out.
	 *
	 * @param runspec The runspec to acquire data from
	 * @param destination The StringBuffer to fill.
	**/
	public void getPrintableDescription(RunSpec runspec, StringBuffer destination) {
		destination.append("Time Spans:\r\n");

		destination.append("\tAggregate By: ");
		if(runspec.timeSpan.aggregateBy == OutputTimeStep.YEAR) {
			destination.append("Year\r\n");
		} else if (runspec.timeSpan.aggregateBy == OutputTimeStep.MONTH) {
			destination.append("Month\r\n");
		} else if (runspec.timeSpan.aggregateBy == OutputTimeStep.CLASSICAL_DAY) {
			destination.append("Day\r\n");
		} else {
			destination.append("Hour\r\n");
		}

		destination.append("\tYears:\r\n");
		for(Iterator<Integer> yearListIter=runspec.timeSpan.years.iterator();yearListIter.hasNext();
				destination.append("\t\t"+yearListIter.next()+"\r\n"));

		destination.append("\r\n\tMonths:\r\n");
		for(Iterator<TimeSpan.Month> i=runspec.timeSpan.months.iterator();i.hasNext();) {
			TimeSpan.Month m = (TimeSpan.Month)i.next();
			destination.append("\t\t" + m.name + "\r\n");
		}

		destination.append("\tDays:\r\n");
		for(Iterator<TimeSpan.Day> i=runspec.timeSpan.days.iterator();i.hasNext();) {
			TimeSpan.Day d = (TimeSpan.Day)i.next();
			destination.append("\t\t" + d.name + "\r\n");
		}

		destination.append("\tHours:\r\n");
		TimeSpan.Hour beginHour = TimeSpan.getHourByID(runspec.timeSpan.beginHourID);
		TimeSpan.Hour endHour = TimeSpan.getHourByID(runspec.timeSpan.endHourID);
		destination.append("\t\tBegin Hour: "+(beginHour != null? beginHour.name : "")+"\r\n");
		destination.append("\t\tEnd Hour: "+(endHour != null? endHour.name : "")+"\r\n");
		destination.append("\r\n");
	}

	/** Creates and initializes all controls on this panel. **/
	public void createControls() {
		Dimension aggregatePanelSize = new Dimension(560,60);
		aggregatePanel = new JPanel();
		aggregatePanel.setName("aggregatePanel");
		aggregatePanel = new JPanel();
		aggregatePanel.setBorder(BorderFactory.createTitledBorder(
				"Time Aggregation Level"));
		aggregatePanel.setPreferredSize(aggregatePanelSize);

		Dimension yearPanelSize = new Dimension(360,250);
		yearPanel = new JPanel();
		yearPanel.setName("yearPanel");
		yearPanel = new JPanel();
		yearPanel.setBorder(BorderFactory.createTitledBorder(
				"Years"));
		yearPanel.setPreferredSize(yearPanelSize);

		Dimension monthPanelSize = new Dimension(200,250);
		monthPanel = new JPanel();
		monthPanel.setName("monthPanel");
		monthPanel.setBorder(BorderFactory.createTitledBorder(
				"Months"));
		monthPanel.setPreferredSize(monthPanelSize);

		Dimension dayPanelSize = new Dimension(360,120);
		dayPanel = new JPanel();
		dayPanel.setName("dayPanel");
		dayPanel.setBorder(BorderFactory.createTitledBorder(
				"Days"));
		dayPanel.setPreferredSize(dayPanelSize);

		Dimension hourPanelSize = new Dimension(200,120);
		hourPanel = new JPanel();
		hourPanel.setName("hourPanel");
		hourPanel.setBorder(BorderFactory.createTitledBorder(
				"Hours"));
		hourPanel.setPreferredSize(hourPanelSize);

		aggregateButtons = new ButtonGroup();

		aggregateYear = new JRadioButton("Year");
		aggregateYear.setName("aggregateYear");
		aggregateYear.addActionListener(this);
		ToolTipHelper.add(aggregateYear,"Aggregate input data by year");

		aggregateMonth = new JRadioButton("Month");
		aggregateMonth.setName("aggregateMonth");
		aggregateMonth.addActionListener(this);
		ToolTipHelper.add(aggregateMonth,"Aggregate input data by month");

		aggregateDay = new JRadioButton("Day");
		aggregateDay.setName("aggregateDay");
		aggregateDay.addActionListener(this);
		ToolTipHelper.add(aggregateDay,"Aggregate input data by day");

		aggregateHour = new JRadioButton("Hour");
		aggregateHour.setName("aggregateHour");
		aggregateHour.addActionListener(this);
		ToolTipHelper.add(aggregateHour,"Aggregate input data by hour");

		yearLabel = new JLabel("Select Year:");
		yearLabel.setName("yearLabel");
		yearCombo = new ExtendedComboBox<String>();
		yearCombo.setName("yearCombo");
		yearCombo.setSelectedIndex(-1);
		yearCombo.setEditable(false);
		ToolTipHelper.add(addYearButton,"Select a year to add to the RunSpec");

		addYearButton = new JButton("Add");
		addYearButton.setName("addYearButton");
		ToolTipHelper.add(addYearButton,"Add the selected year to the RunSpec");
		yearSelectionLabel = new JLabel("Years:");
		yearSelectionLabel.setName("yearSelectionLabel");
		yearListModel = new DefaultListModel<IntegerWrapper>();
		yearList = new JListWithToolTips<IntegerWrapper>(yearListModel);
		yearList.setName("yearList");
		yearList.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		yearList.setSelectedIndex(-1);
		yearList.addListSelectionListener(this);
		yearList.setVisibleRowCount(6);
		yearList.setPrototypeCellValue(new IntegerWrapper() { public String toString() { return "CharacterCountToDisplayXXXXXXXXXXXXXX"; }});
		yearScrollPane = new JScrollPane(yearList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		yearScrollPane.setName("yearScrollPane");

		yearRemove = new JButton("Remove");
		yearRemove.setName("yearRemove");
		yearRemove.setEnabled(false); // not enabled until item selected on list
		yearRemove.setName("yearRemove");
		ToolTipHelper.add(yearRemove,"Remove the select year(s) from the RunSpec");

		Dimension buttonSize = new Dimension(87,25);
		addYearButton.setPreferredSize(buttonSize);
		yearRemove.setPreferredSize(buttonSize);

		for(Iterator<TimeSpan.Month> i=TimeSpan.allMonths.iterator();i.hasNext();) {
			TimeSpan.Month m = (TimeSpan.Month)i.next();
			MonthCheckBox b = new MonthCheckBox(m,new JCheckBox(m.name));
			b.checkBox.setName(m.name.toLowerCase());
			b.checkBox.addActionListener(this);
			ToolTipHelper.add(b.checkBox,m.name + " of any year");
			monthCheckBoxes.add(b);
		}

		selectAllMonthsButton = new JButton("Select All");
		selectAllMonthsButton.setName("selectAllMonthsButton");
		selectAllMonthsButton.setPreferredSize(buttonSize);
		ToolTipHelper.add(selectAllMonthsButton,"Select all months");
		clearAllMonthsButton = new JButton("Clear All");
		clearAllMonthsButton.setName("clearAllMonthsButton");
		ToolTipHelper.add(clearAllMonthsButton,"Clear all months");
		clearAllMonthsButton.setPreferredSize(buttonSize);

		for(Iterator<TimeSpan.Day> i=TimeSpan.allDays.iterator();i.hasNext();) {
			TimeSpan.Day d = (TimeSpan.Day)i.next();
			DayCheckBox b = new DayCheckBox(d,new JCheckBox(d.name));
			b.checkBox.setName(d.name.toLowerCase());
			b.checkBox.addActionListener(this);
			ToolTipHelper.add(b.checkBox,d.name + " of any month");
			dayCheckBoxes.add(b);
		}

		selectAllDaysButton = new JButton("Select All");
		selectAllDaysButton.setName("selectAllDaysButton");
		selectAllDaysButton.setPreferredSize(buttonSize);
		ToolTipHelper.add(selectAllDaysButton,"Select all days");
		clearAllDaysButton = new JButton("Clear All");
		clearAllDaysButton.setName("clearAllDaysButton");
		clearAllDaysButton.setPreferredSize(buttonSize);
		ToolTipHelper.add(clearAllDaysButton,"Clear all days");

		beginHourLabel = new JLabel("Start Hour:");
		beginHourLabel.setName("beginHourLabel");
		beginHourCombo = new ExtendedComboBox<TimeSpan.Hour>();
		beginHourCombo.setName("beginHourCombo");
		beginHourCombo.setSelectedIndex(-1);
		ToolTipHelper.add(beginHourCombo,"First simulated hour of the day");
		endHourLabel = new JLabel("End Hour:");
		endHourLabel.setName("endHourLabel");
		endHourCombo = new ExtendedComboBox<TimeSpan.Hour>();
		endHourCombo.setName("endHourCombo");
		endHourCombo.setSelectedIndex(-1);
		ToolTipHelper.add(endHourCombo,"Last simulated hour of the day");
		selectAllHoursButton = new JButton("Select All");
		selectAllHoursButton.setName("selectAllHoursButton");
		selectAllHoursButton.setPreferredSize(buttonSize);
		ToolTipHelper.add(selectAllHoursButton,"Select all hours of any day");
		clearAllHoursButton = new JButton("Clear All");
		clearAllHoursButton.setName("clearAllHoursButton");
		clearAllHoursButton.setPreferredSize(buttonSize);
		ToolTipHelper.add(clearAllHoursButton,"Clear all hours of any day");

		beginHourCombo.addActionListener(this);
		endHourCombo.addActionListener(this);
		selectAllMonthsButton.addActionListener(this);
		selectAllDaysButton.addActionListener(this);
		selectAllHoursButton.addActionListener(this);
		clearAllMonthsButton.addActionListener(this);
		clearAllDaysButton.addActionListener(this);
		clearAllHoursButton.addActionListener(this);

		addYearButton.addActionListener(this);
		yearRemove.addActionListener(this);
	}

	/** Sets the layout of the controls. **/
	public void arrangeControls() {
		GridBagConstraints gbc = new GridBagConstraints();

		aggregateButtons.add(aggregateYear);
		aggregateButtons.add(aggregateMonth);
		aggregateButtons.add(aggregateDay);
		aggregateButtons.add(aggregateHour);

		aggregatePanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		LayoutUtility.setPositionOnGrid(gbc, 0, 0, "WEST", 1, 1);
		aggregatePanel.add(aggregateYear, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 1, 0, "WEST", 1, 1);
		aggregatePanel.add(aggregateMonth, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 2, 0, "WEST", 1, 1);
		aggregatePanel.add(aggregateDay, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 3, 0, "WEST", 1, 1);
		aggregatePanel.add(aggregateHour, gbc);


		yearPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 3;
		gbc.gridheight = 6;
		gbc.weightx = 0;
		gbc.weighty = 0;
		LayoutUtility.setPositionOnGrid(gbc,0, 0, "WEST", 1, 1);
		yearPanel.add(yearLabel, gbc);
		LayoutUtility.setPositionOnGrid(gbc,1, 0, "EAST", 1, 1);
		yearPanel.add(yearCombo, gbc);
		LayoutUtility.setPositionOnGrid(gbc,2, 0, "EAST", 1, 1);
		yearPanel.add(addYearButton, gbc);
		LayoutUtility.setPositionOnGrid(gbc,0, 1, "WEST", 3, 1);
		yearPanel.add(yearSelectionLabel, gbc);
		LayoutUtility.setPositionOnGrid(gbc,0, 2, "EAST", 3, 3);
		yearPanel.add(yearScrollPane, gbc);
		LayoutUtility.setPositionOnGrid(gbc,2, 5, "EAST", 1, 1);
		yearPanel.add(yearRemove, gbc);

		monthPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 2;
		gbc.gridheight = 8;
		gbc.weightx = 0;
		gbc.weighty = 0;

		int columnHeight = 6;
		int r = 0, c = 0;
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();r++) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			if(r >= columnHeight) {
				c++;
				r = 0;
			}
			LayoutUtility.setPositionOnGrid(gbc, c, r, "WEST", 1, 1);
			monthPanel.add(b.checkBox, gbc);
		}

		LayoutUtility.setPositionOnGrid(gbc, 0, 6, "WEST", 1, 1);
		monthPanel.add(selectAllMonthsButton, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 1, 6, "WEST", 1, 1);
		monthPanel.add(clearAllMonthsButton, gbc);

		dayPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 4;
		gbc.gridheight = 3;
		gbc.weightx = 0;
		gbc.weighty = 0;

		columnHeight = 2;
		r = 0;
		c = 0;
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();r++) {
			DayCheckBox b = (DayCheckBox)i.next();
			if(r >= columnHeight) {
				c++;
				r = 0;
			}
			LayoutUtility.setPositionOnGrid(gbc, c, r, "WEST", 1, 1);
			dayPanel.add(b.checkBox, gbc);
		}

		LayoutUtility.setPositionOnGrid(gbc, 2, 2, "WEST", 1, 1);
		dayPanel.add(selectAllDaysButton, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 3, 2, "WEST", 1, 1);
		dayPanel.add(clearAllDaysButton, gbc);

		hourPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 2;
		gbc.gridheight = 3;
		gbc.weightx = 0;
		gbc.weighty = 0;
		LayoutUtility.setPositionOnGrid(gbc, 0, 0, "WEST", 1, 1);
		hourPanel.add(beginHourLabel, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 1, 0, "WEST", 1, 1);
		hourPanel.add(beginHourCombo, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 0, 1, "WEST", 1, 1);
		hourPanel.add(endHourLabel, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 1, 1, "WEST", 1, 1);
		hourPanel.add(endHourCombo, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 0, 2, "WEST", 1, 1);
		hourPanel.add(selectAllHoursButton, gbc);
		LayoutUtility.setPositionOnGrid(gbc, 1, 2, "WEST", 1, 1);
		hourPanel.add(clearAllHoursButton, gbc);

		setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridwidth = 2;
		gbc.gridheight = 3;
		gbc.weightx = 0;
		gbc.weighty = 0;
		LayoutUtility.setPositionOnGrid(gbc,0,0, "CENTER", 2, 1);
		add(aggregatePanel,gbc);
		LayoutUtility.setPositionOnGrid(gbc,0,1, "CENTER", 1, 1);
		add(yearPanel,gbc);
		LayoutUtility.setPositionOnGrid(gbc,1,1, "WEST", 1, 1);
		add(monthPanel,gbc);
		LayoutUtility.setPositionOnGrid(gbc,0,2, "CENTER", 1, 1);
		add(dayPanel,gbc);
		LayoutUtility.setPositionOnGrid(gbc,1,2, "WEST", 1, 1);
		add(hourPanel,gbc);
	}

	/**
	 * Listener method for list selection changes.
	 * @param e Event caused by a user selection.
	**/
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting() == false) {
			if(yearList.getSelectedIndex() == -1) {
				//No selection: enable/disable relevant controls.
				yearRemove.setEnabled(false);
			} else {
				yearRemove.setEnabled(true);
			}
		}
	}

	/**
	 * Saves the description text to a RunSpec.
	 * @param	runspec the RunSpec to get the description text.
	**/
	public void saveToRunSpec(RunSpec runspec) {
		TimeSpan tsToSave = new TimeSpan();
		for(int i=0;i<yearListModel.getSize();i++) {
			IntegerWrapper year = (IntegerWrapper)yearListModel.getElementAt(i);
			tsToSave.years.add(year.v);
		}

		tsToSave.months.clear();
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			if(b.checkBox.isSelected()) {
				tsToSave.months.add(b.month);
			}
		}

		tsToSave.days.clear();
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			if(b.checkBox.isSelected()) {
				tsToSave.days.add(b.day);
			}
		}

		TimeSpan.Hour beginHour = (TimeSpan.Hour)beginHourCombo.getSelectedItem();
		TimeSpan.Hour endHour = (TimeSpan.Hour)endHourCombo.getSelectedItem();
		tsToSave.beginHourID = beginHour != null? beginHour.hourID : 0;
		tsToSave.endHourID = endHour != null? endHour.hourID : 0;

		Models.ModelCombination mc = Models.evaluateModels( runspec.models);
		switch (mc) {
			case M1: // ONROAD
				tsToSave.aggregateBy = OutputTimeStep.HOUR;
				if(aggregateYear.isSelected()) {
					tsToSave.aggregateBy = OutputTimeStep.YEAR;
				} else if(aggregateMonth.isSelected()) {
					tsToSave.aggregateBy = OutputTimeStep.MONTH;
				} else if(aggregateDay.isSelected()) {
					tsToSave.aggregateBy = OutputTimeStep.CLASSICAL_DAY;
				}
				break;
			case M2: // NONROAD
				// Nonroad displays Day aggregation, but still requires HOUR to be in the RunSpec.
				tsToSave.aggregateBy = OutputTimeStep.HOUR;
				break;
			default:
				break;
		}

		runspec.timeSpanSectionStatus = timeSpanSectionStatus;
		runspec.timeSpan = tsToSave;

		if(runspec.timeSpan.aggregateBy != OutputTimeStep.HOUR) {
			runspec.outputEmissionsBreakdownSelection.estimateUncertainty = false;
		}

		if(runspec.outputTimeStep != null) {
			if(runspec.outputTimeStep.compareTo(runspec.timeSpan.aggregateBy) < 0) {
				runspec.outputTimeStep = runspec.timeSpan.aggregateBy;
			}
		}

		if(runspec.outputTimeStep != null) {
			runspec.outputFactors.timeMeasurementSystem =
					runspec.outputTimeStep.getTimeMeasurementSystemDefault();
		} else {
			runspec.outputFactors.timeMeasurementSystem = null;
		}
	}

	/**
	 * Loads the description text from a RunSpec.
	 * @param	runspec the RunSpec to get the description text from.
	**/
	public void loadFromRunSpec(RunSpec runspec) {
		loadedDomain = runspec.domain;

		TimeSpan tsToLoad = new TimeSpan(runspec.timeSpan);

		if(runspec.domain != ModelDomain.PROJECT ) {
			enableAllMonthButtons();
			selectAllMonthsButton.setEnabled(true);

			enableAllDayButtons();
			selectAllDaysButton.setEnabled(true);

			selectAllHoursButton.setEnabled(true);

			endHourCombo.setEnabled(true);
		}

		Models.ModelCombination mc = Models.evaluateModels( runspec.models);
		switch (mc) {
			case M1: // ONROAD
				if(runspec.domain == ModelDomain.PROJECT || runspec.scale == ModelScale.MESOSCALE_LOOKUP) {
					tsToLoad.aggregateBy = OutputTimeStep.HOUR;
					aggregateYear.setEnabled(false);
					aggregateMonth.setEnabled(false);
					aggregateDay.setEnabled(false);
					aggregateHour.setEnabled(false);
				} else {
					aggregateYear.setEnabled(true);
					aggregateMonth.setEnabled(true);
					aggregateDay.setEnabled(true);
					aggregateHour.setEnabled(true);
				}
				break;
			case M2:
				aggregateYear.setEnabled(false);
				aggregateMonth.setEnabled(false);
				aggregateDay.setEnabled(false);
				aggregateHour.setEnabled(false);

				tsToLoad.aggregateBy = OutputTimeStep.CLASSICAL_DAY;
				break;
			default:
				break;
		}
		if(tsToLoad.aggregateBy == OutputTimeStep.YEAR) {
			aggregateYear.setSelected(true);
		} else if (tsToLoad.aggregateBy == OutputTimeStep.MONTH) {
			aggregateMonth.setSelected(true);
		} else if (tsToLoad.aggregateBy == OutputTimeStep.CLASSICAL_DAY) {
			aggregateDay.setSelected(true);
		} else {
			aggregateHour.setSelected(true);
		}

		yearListModel.clear();
		for(Iterator<Integer> yearListIter=tsToLoad.years.iterator();yearListIter.hasNext();
				yearListModel.addElement(new IntegerWrapper(yearListIter.next())));

		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			b.checkBox.setSelected(tsToLoad.hasMonthID(b.month.monthID));
		}

		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			b.checkBox.setSelected(tsToLoad.hasDayID(b.day.dayID));
			b.checkBox.setEnabled(true);
		}
		if(dayCheckBoxes.size() > 1) {
			selectAllDaysButton.setEnabled(true);
			clearAllDaysButton.setEnabled(true);
		} else if(dayCheckBoxes.size() == 1) {
			selectAllDaysButton.setEnabled(false);
			clearAllDaysButton.setEnabled(false);

			DayCheckBox b = (DayCheckBox)dayCheckBoxes.get(0);
			b.checkBox.setSelected(true);
			b.checkBox.setEnabled(false);
		}

		TimeSpan.Hour beginHour = TimeSpan.getHourByID(tsToLoad.beginHourID);
		if(beginHour != null) {
			beginHourCombo.setSelectedItem(beginHour);
		} else {
			beginHourCombo.setSelectedIndex(0);
		}

		TimeSpan.Hour endHour = TimeSpan.getHourByID(tsToLoad.endHourID);
		if(endHour != null) {
			endHourCombo.setSelectedItem(endHour);
		} else {
			endHourCombo.setSelectedIndex(0);
		}

		enableMe(aggregatePanel, runspec.models.getModelList().contains( Model.ONROAD) && runspec.domain != ModelDomain.PROJECT && runspec.scale != ModelScale.MESOSCALE_LOOKUP);
		enableMe(hourPanel,runspec.models.getModelList().contains( Model.ONROAD)); //true);
		// enableMe(dayPanel,runspec.models.getModelList().contains( Model.ONROAD)); // both NONROAD and ONROAD can chose the weekday or weekend

		EmissionProcess evapPermeation = EmissionProcess.findByID(11); // Evap Permeation
		EmissionProcess evapFuelVaporVenting
				= EmissionProcess.findByID(12); // Evap Fuel Vapor Venting
		EmissionProcess evapFuelLeaks = EmissionProcess.findByID(13); // Evap Fuel Leaks
		for(Iterator iterPollutantProcessAssociations =
				runspec.pollutantProcessAssociations.iterator();
				iterPollutantProcessAssociations.hasNext();) {
			PollutantProcessAssociation pollutantProcessAssociation =
					(PollutantProcessAssociation)iterPollutantProcessAssociations.next();
			if(pollutantProcessAssociation.emissionProcess==evapPermeation
					|| pollutantProcessAssociation.emissionProcess==evapFuelVaporVenting
					|| pollutantProcessAssociation.emissionProcess==evapFuelLeaks) {
				if(runspec.timeSpan.aggregateBy==OutputTimeStep.HOUR
						//&& runspec.timeSpan.hasAllHours()
						) {
					enableMe(aggregatePanel,false);
					//enableMe(hourPanel,false);
					break;
				}
			}
		}

		timeSpanSectionStatus = runspec.timeSpanSectionStatus;

		checkYears();

		if(runspec.domain == ModelDomain.PROJECT ) {
			cleanMonthButtons();
			selectAllMonthsButton.setEnabled(false);

			cleanDayButtons();
			selectAllDaysButton.setEnabled(false);

			cleanHourButtons();
			selectAllHoursButton.setEnabled(false);

			endHourCombo.setEnabled(false);
		}
	}

	/**
	 * Set the enable state of all the child components.
	 * @param top container A container of components to be disabled.
	 * @param enable true if components are to be enabled.
	**/
	void enableMe(Container top, boolean enable) {
		Component[] comp = top.getComponents();
		for (int i = 0; i < comp.length; i++) {
			comp[i].setEnabled(enable);
			if (comp[i] instanceof Container) {
				enableMe((Container)comp[i], enable);
			}
		}
	}

	/**
	 * Gets the RunSpec status from the current sections.
	 * @param	runspec the RunSpec to get the description text.
	 * @param	sections TreeMap containing the current sections.
	 * @return	RunSpecSectionStatus of the RunSpec based on the sections.
	**/
	public RunSpecSectionStatus calculateRunSpecSectionStatus(RunSpec runspec,
			TreeMap<String,RunSpecSectionStatus> sections) {
		Models.ModelCombination mc = runspec.getModelCombination();
		boolean isOk = true;
		if(runspec.timeSpan.years.isEmpty()) {
			isOk = false;
		} else if (!runspec.timeSpan.hasMonths()) {
			isOk = false;
		} else if (!runspec.timeSpan.hasDays()) {
			isOk = false;
		} else if (runspec.models.getModelList().contains( Model.ONROAD) && !runspec.timeSpan.hasHours()) {
			isOk = false;
		} else if (runspec.models.getModelList().contains( Model.ONROAD) && !runspec.timeSpan.hasendHourGTEbeginHour()) {
			isOk = false;
		}

		if(isOk && runspec.domain != ModelDomain.NATIONAL_ALLOCATION
				&& runspec.timeSpan.getYearCount() != 1) {
			isOk = false;
		}
		if(isOk && runspec.domain == ModelDomain.PROJECT
				&& runspec.timeSpan.getMonthCount() != 1) {
			isOk = false;
		}
		if(isOk && runspec.domain == ModelDomain.PROJECT
				&& runspec.timeSpan.getDayCount() != 1) {
			isOk = false;
		}
		if(isOk && runspec.domain == ModelDomain.PROJECT
				&& runspec.timeSpan.getHourCount() != 1) {
			isOk = false;
		}
		// In Emission Rates mode or Project domain, the time aggregation can only be hourly, so complain if it is not.
		if(isOk && (runspec.scale == ModelScale.MESOSCALE_LOOKUP || runspec.domain == ModelDomain.PROJECT)) {
			if(runspec.timeSpan != null && runspec.timeSpan.aggregateBy != null && runspec.timeSpan.aggregateBy != OutputTimeStep.HOUR) {
				isOk = false;
			}
		}

		timeSpanSectionStatus = runspec.timeSpanSectionStatus;
		boolean wasGoodOrNotDetermined = timeSpanSectionStatus==-1||timeSpanSectionStatus==1;

		if(runspec.outputTimeStep!=null) {
			timeSpanSectionStatus = 1;
			if(runspec.outputTimeStep.requiresAllHours()) {
				if(!runspec.timeSpan.hasAllHours()) {
					if(!runspec.models.contains(Model.NONROAD)) {
						if(wasGoodOrNotDetermined) {
//System.out.println("runspec.outputTimeStep=" + runspec.outputTimeStep.toString());
//System.out.println("runspec.timeSpan=" + runspec.timeSpan.toString());
							JOptionPane.showMessageDialog(this, "The selected Output Emission"
									+ " Details Time Step requires all hours.");
						}
						timeSpanSectionStatus = 0;
					}
				}
			}
			if(runspec.outputTimeStep.requiresAllDays()) {
				try {
					Connection db = DatabaseConnectionManager.getGUIConnection(
								MOVESDatabaseType.DEFAULT);
					if(db!=null) {
						if(!runspec.timeSpan.hasAllDays(db)) {
							if(wasGoodOrNotDetermined) {
								JOptionPane.showMessageDialog(this, "The selected Output"
										+ " Emission Details Time Step requires all days.");
							}
							timeSpanSectionStatus = 0;
						}
					}
				} catch(Exception e) {
					timeSpanSectionStatus = 0;
				}
			}
			if(runspec.outputTimeStep.requiresAllMonths()) {
				if(!runspec.timeSpan.hasAllMonths()) {
					if(wasGoodOrNotDetermined) {
						JOptionPane.showMessageDialog(this, "The selected Output Emission"
								+ " Details Time Step requires all months.");
					}
					timeSpanSectionStatus = 0;
				}
			}
		}

		EmissionProcess evapPermeation = EmissionProcess.findByID(11); // Evap Permeation
		EmissionProcess evapFuelVaporVenting = EmissionProcess.findByID(12); // Evap Fuel Vapor Venting
		EmissionProcess evapFuelLeaks = EmissionProcess.findByID(13); // Evap Fuel Leaks
		for(Iterator iterPollutantProcessAssociations = runspec.pollutantProcessAssociations.iterator();
				iterPollutantProcessAssociations.hasNext();) {
			PollutantProcessAssociation pollutantProcessAssociation =
					(PollutantProcessAssociation)iterPollutantProcessAssociations.next();
			if(pollutantProcessAssociation.emissionProcess==evapPermeation
					|| pollutantProcessAssociation.emissionProcess==evapFuelVaporVenting
					|| pollutantProcessAssociation.emissionProcess==evapFuelLeaks) {
				switch ( mc) {
				case M1:
					if(runspec.timeSpan.aggregateBy != OutputTimeStep.HOUR) {
						if(wasGoodOrNotDetermined) {
							JOptionPane.showMessageDialog(this, "The emission process selection, "
									+ pollutantProcessAssociation.emissionProcess
									+ " requires the time aggregation level to "
									+ "be Hour.");
						}
						timeSpanSectionStatus = 0;
						isOk = false;
					} else if(!runspec.timeSpan.hasAllHours()) {
						if(pollutantProcessAssociation.emissionProcess==evapFuelVaporVenting) {
							if(wasGoodOrNotDetermined) {
								JOptionPane.showMessageDialog(this, "The emission process selection, "
										+ pollutantProcessAssociation.emissionProcess
										+ " requires all hours to be selected.");
							}
							timeSpanSectionStatus = 0;
							isOk = false;
						}
					}
					break;
				case M2:
				default:
					break;
				}
				break;
			}
		}
		/*
		if(isOk && runspec.scale == ModelScale.MESOSCALE_LOOKUP
				&& runspec.timeSpan.aggregateBy != OutputTimeStep.HOUR) {
			isOk = false;
		}
		*/

		runspec.timeSpanSectionStatus = timeSpanSectionStatus;
		RunSpecSectionStatus status = new RunSpecSectionStatus(
				isOk?RunSpecSectionStatus.OK:RunSpecSectionStatus.NOT_READY);
		sections.remove(getName());
		sections.put(getName(),status);
		return status;
	}

	/**
	 * Sets the defaults to the RunSpec.
	 * @param	runspec the RunSpec to the description text.
	 * @param	sections TreeMap containing the current sections.
	 * @return	RunSpecSectionStatus of the RunSpec based on the sections.
	**/
	public RunSpecSectionStatus saveDefaultsToRunSpec(RunSpec runspec,
			TreeMap<String,RunSpecSectionStatus> sections) {
		runspec.timeSpan = new TimeSpan();
		runspec.timeSpanSectionStatus = -1;
		sections.remove(getName());
		RunSpecSectionStatus status = new RunSpecSectionStatus(RunSpecSectionStatus.NOT_READY);
		sections.put(getName(),status);

		if(yearCombo != null) {
			yearCombo.setSelectedItem(EMPTY_YEAR_TEXT);
		}

		return status;
	}

	/**
	 * Update current selections to be consistent with a newly selected ModelScale.
	 *
	 * The editor should also place a RunSpecSectionStatus object into sections, likely using
	 * it's name as the key.  Typically it will store a DEFAULTS or NOT_READY value.
	 * @param runspec the RunSpec to examine
	 * @param sections a table of previous status calculation results which should be updated too
	 * @return an object that can be used to determine which icon to display to the user
	**/
	public RunSpecSectionStatus onScaleChange(RunSpec runspec,
			TreeMap<String,RunSpecSectionStatus> sections) {
		if(runspec.domain == ModelDomain.PROJECT ) {
			selectAllMonthsButton.setEnabled(false);
			selectAllDaysButton.setEnabled(false);
			selectAllHoursButton.setEnabled(false);
			endHourCombo.setEnabled(false);
		} else {
			selectAllMonthsButton.setEnabled(true);
			selectAllDaysButton.setEnabled(true);
			selectAllHoursButton.setEnabled(true);
			endHourCombo.setEnabled(true);
		}
		return calculateRunSpecSectionStatus(runspec,sections);
	}

	/**
	 * Listener method, calls the appropriate button handler.
	 * @param	e the ActionEvent to be handled.
	**/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == aggregateYear) {
			processAggregateYearButton();
		} else if (e.getSource() == aggregateMonth) {
			processAggregateMonthButton();
		} else if (e.getSource() == aggregateDay) {
			processAggregateDayButton();
		} else if (e.getSource() == aggregateHour) {
			processAggregateHourButton();
		} else if (e.getSource() == addYearButton) {
			processAddYearButton();
		} else if (e.getSource() == yearRemove) {
			processYearRemoveButton();
		} else if (e.getSource() == selectAllMonthsButton) {
			processSelectAllMonthsButton();
		} else if (e.getSource() == selectAllDaysButton) {
			processSelectAllDaysButton();
		} else if (e.getSource() == selectAllHoursButton) {
			processSelectAllHoursButton();
		} else if (e.getSource() == clearAllMonthsButton) {
			processClearAllMonthsButton();
		} else if (e.getSource() == clearAllDaysButton) {
			processClearAllDaysButton();
		} else if (e.getSource() == clearAllHoursButton) {
			processClearAllHoursButton();
		} else if (e.getSource() == endHourCombo ||
		           e.getSource() == beginHourCombo) {
			processBeginAndEndHourCombo();
		} else {
			if( loadedDomain == ModelDomain.PROJECT ){
				for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
					MonthCheckBox b = (MonthCheckBox)i.next();
					if( e.getSource() == b.checkBox ) {
						cleanMonthButtons();
						break;
					}
				}
				for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
					DayCheckBox b = (DayCheckBox)i.next();
					if( e.getSource() == b.checkBox ) {
						cleanDayButtons();
						break;
					}
				}
				cleanHourButtons();
			}
		}
		MOVESNavigation.singleton.updateRunSpecSectionStatus();
	}

	/** enables or disables month buttons- enables first button found disables all others **/
	public void cleanMonthButtons() {
		boolean foundFirst=false;
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			b.checkBox.setEnabled(false);
			if( b.checkBox.isSelected() && foundFirst==false ) {
				b.checkBox.setEnabled(true);
				foundFirst=true;
			}
		}
		if( foundFirst==false ){
			for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
				MonthCheckBox b = (MonthCheckBox)i.next();
				b.checkBox.setEnabled(true);
			}
		}
	}

	/** enables all month buttons **/
	public void enableAllMonthButtons() {
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			b.checkBox.setEnabled(true);
		}
	}

	/** enables or disables day buttons- enables first button found disables all others **/
	public void cleanDayButtons() {
		boolean foundFirst=false;
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			b.checkBox.setEnabled(false);
			if( b.checkBox.isSelected() && foundFirst==false ) {
				b.checkBox.setEnabled(true);
				foundFirst=true;
			}
		}
		if( foundFirst==false ){
			for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
				DayCheckBox b = (DayCheckBox)i.next();
				b.checkBox.setEnabled(true);
			}
		}
	}

	/** enables all day buttons **/
	public void enableAllDayButtons() {
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			b.checkBox.setEnabled(true);
		}
	}

	/** enables or disables day buttons- enables first button found disables all others **/
	public void cleanHourButtons() {
		int beginHour = beginHourCombo.getSelectedIndex();
		endHourCombo.setSelectedIndex(beginHour);
	}

	/** Handles the aggregate year button. **/
	public void processAggregateYearButton() {
		processSelectAllMonthsButton();
		processSelectAllDaysButton();
		processSelectAllHoursButton();
	}

	/** Handles the aggregate month button. **/
	public void processAggregateMonthButton() {
		processSelectAllDaysButton();
		processSelectAllHoursButton();
	}

	/** Handles the aggregate day button. **/
	public void processAggregateDayButton() {
		processSelectAllHoursButton();
	}

	/** Handles the aggregate hour button. **/
	public void processAggregateHourButton() {
		// No additional processing necessary at this time.
	}

	/** Handles the Add button. **/
	public void processAddYearButton() {
		String errorMessage = new String();
		Integer year = new Integer(0);
		try {
			year = parseYear();
		} catch(Exception e) {
			errorMessage = "Please select a year to add.";
			JOptionPane.showMessageDialog(this,errorMessage);
			return;
		}
		for(int i=0;i<yearListModel.getSize();i++) {
			if(year.equals(yearListModel.getElementAt(i))) {
				JOptionPane.showMessageDialog(this,year+" is already selected.");
				return;
			}
		}
		yearListModel.addElement(new IntegerWrapper(year));
		checkYears();
	}

	/** Handles the Year Remove button. **/
	public void processYearRemoveButton() {
		java.util.List<IntegerWrapper> selectedYears = yearList.getSelectedValuesList();
		for(Iterator<IntegerWrapper> i=selectedYears.iterator();i.hasNext();) {
			yearListModel.removeElement(i.next());
		}
		yearRemove.setEnabled(false);
		checkYears();
	}

	/** Handles the Select All Months button. **/
	public void processSelectAllMonthsButton() {
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			b.checkBox.setSelected(true);
		}
	}

	/** Handles the Clear All Months button. **/
	public void processClearAllMonthsButton() {
		for(Iterator i=monthCheckBoxes.iterator();i.hasNext();) {
			MonthCheckBox b = (MonthCheckBox)i.next();
			b.checkBox.setSelected(false);
		}
		if( loadedDomain == ModelDomain.PROJECT ){
			cleanMonthButtons();
		}
	}

	/** Handles the Select All Days button. **/
	public void processSelectAllDaysButton() {
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			b.checkBox.setSelected(true);
		}
		if( loadedDomain == ModelDomain.PROJECT ){
			cleanDayButtons();
		}
	}

	/** Handles the Clear All Days button. **/
	public void processClearAllDaysButton() {
		for(Iterator i=dayCheckBoxes.iterator();i.hasNext();) {
			DayCheckBox b = (DayCheckBox)i.next();
			b.checkBox.setSelected(false);
		}
		if( loadedDomain == ModelDomain.PROJECT ){
			cleanDayButtons();
		}
	}

	/** Handles the Select All Hours button. **/
	public void processSelectAllHoursButton() {
		beginHourCombo.setSelectedIndex(1);
		endHourCombo.setSelectedIndex(24);
	}

	/** Handles the Select All Hours button. **/
	public void processClearAllHoursButton() {
		beginHourCombo.setSelectedIndex(0);
		endHourCombo.setSelectedIndex(0);
		if( loadedDomain == ModelDomain.PROJECT ){
			cleanHourButtons();
		}
	}

	/** Handles the endHourCombo and beginHourCombo boxes. **/
	public void processBeginAndEndHourCombo() {
		if( loadedDomain == ModelDomain.PROJECT ){
			cleanHourButtons();
		}
		if( endHourCombo.getSelectedIndex()>0 &&
              beginHourCombo.getSelectedIndex()>0 &&
           (endHourCombo.getSelectedIndex()<beginHourCombo.getSelectedIndex()) ) {
		   JOptionPane.showMessageDialog(this, "The end hour is prior to the start hour.");
		}
	}

	/**
	 * Tries to parse the Year textfield.
	 * @return The parsed Year value as Integer.
	 * @throws NumberFormatException from either a failed valueOf() conversion, or from
	 * an invalid Year value.
	**/
	Integer parseYear() throws NumberFormatException {
		// verify Year user input
		Integer testYear = Integer.valueOf(yearCombo.getSelectedItem().toString());
		return testYear;
	}

	/**
	 * Populates droplists except for day (dependant on month and year) on this panel.
	**/
	void populateControls() {
		// Populate year combo
		loadYears();

		// Populate start hour combo
		beginHourCombo.removeAllItems();
		beginHourCombo.addItem(emptyHour);
		endHourCombo.removeAllItems();
		endHourCombo.addItem(emptyHour);
		for(Iterator<TimeSpan.Hour> i=TimeSpan.allHours.iterator();i.hasNext();) {
			TimeSpan.Hour h = (TimeSpan.Hour)i.next();
			beginHourCombo.addItem(h);
			endHourCombo.addItem(h);
		}
		beginHourCombo.setSelectedIndex(0);
		endHourCombo.setSelectedIndex(0);
	}

	/** Gets the base year value from the database. **/
	void loadYears() {
		yearCombo.removeAllItems();
		yearCombo.addItem(EMPTY_YEAR_TEXT);
		yearCombo.setSelectedIndex(-1);
		Connection db = DatabaseConnectionManager.getGUIConnection(MOVESDatabaseType.DEFAULT);
		if(null == db) {
			return;
		}
		String sql = "";
		PreparedStatement statement = null;
		try {
			sql = "SELECT yearId FROM Year";
			statement=db.prepareStatement(sql);

			ResultSet results = SQLRunner.executeQuery(statement,sql);
			while(results.next()) {
				yearCombo.addItem((new Integer(results.getInt(1))).toString());
			}
			statement.close();
		} catch(SQLException e) {
			Logger.logSqlError(e, "Get list of years failed", sql);
		} finally {
			if(statement!=null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// Failure to close on a preparedStatment should not be an issue.
				}
			}
		}
	}

	/** Enable/Disable the button for adding years based on year count and domain **/
	void checkYears() {
		boolean allowMoreYears = true;
		if(loadedDomain != null && loadedDomain != ModelDomain.NATIONAL_ALLOCATION
				&& yearListModel.size() >= 1) {
			allowMoreYears = false;
		}
		addYearButton.setEnabled(allowMoreYears);
	}

	@Override
	public RunSpecSectionStatus onModelChange(RunSpec runspec,
			TreeMap<String, RunSpecSectionStatus> sections) {
		this.loadFromRunSpec(runspec);
		return calculateRunSpecSectionStatus(runspec,sections);
	}
}
