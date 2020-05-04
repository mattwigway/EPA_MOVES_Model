/**************************************************************************************************
* @(#)MOVESWindow.java
*
*************************************************************************************************/
package gov.epa.otaq.moves.master.gui;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.text.SimpleDateFormat;
import gov.epa.otaq.moves.master.runspec.*;
import gov.epa.otaq.moves.master.framework.*;
import gov.epa.otaq.moves.common.*;
import gov.epa.otaq.gis.api.StateCountyMapGUI;
import gov.epa.otaq.moves.master.framework.importers.ImporterManager;
/**
* Class for MOVES Main Frame. Constructs the MOVESWindow frame. Creates and sets
* the layouts of the controls. The main menu items are: File (open, close, ...),
* Edit (cut, copy, ...), Pre Processing, Action, Post Processing, ExecuteDataImporter,
* Settings, and Help. The window includes a Split pane to contain the navigation UI
* on the left and the contents UI on the right, a status bar, and a timer used with
* the log handler to display messages. Various RunSpec elements can be edited as well,
* for example Scale, time Spans.
*
* @author	Daniel Cox
* @author	Wesley Faler
* @author	Don Smith
* @author	EPA-W. Aikman
* @author	EPA-Gwo S.
* @author	EPA-elg
* @author	EPA-Mitch C.
* @author	Tim Hull
* @author	Harvey Michaels
* @author  W. Aikman
* @version	    2018/03/22
**/
public class MOVESWindow extends JFrame implements ActionListener, LogHandler,
		MOVESEngineListener, KeyListener, FilenameFilter {
	// These are the actions defined for the application
	/** New menu action. **/
	NewAction newAction;
	/** Open menu action. **/
	OpenAction openAction;
	/** Close menu action. **/
	CloseAction closeAction;
	/** Save menu action. **/
	SaveAction saveAction;
	/** Save As menu action. **/
	SaveAsAction saveAsAction;
	/** Print menu action. **/
	PrintAction printAction;
	/** Previous File 1 menu action. **/
	PreviousFile1Action previousFile1Action;
	/** Previous File 2 menu action. **/
	PreviousFile2Action previousFile2Action;
	/** Previous File 3 menu action. **/
	PreviousFile3Action previousFile3Action;
	/** Previous File 4 menu action. **/
	PreviousFile4Action previousFile4Action;
	/** Exit menu action. **/
	ExitAction exitAction;
	/** Cut menu action. **/
	CutAction cutAction;
	/** Copy menu action. **/
	CopyAction copyAction;
	/** Paste menu action. **/
	PasteAction pasteAction;
	/** Clear menu action. **/
	ClearAction clearAction;
	/** ExecuteDataImporter menu action. **/
	ExecuteDataImporterAction executeDataImporterAction;
// updateWellToPumpRatesAction was removed from the model
//	UpdateWellToPumpRates menu action.  This feature has been removed.
//	/** UpdateWellToPumpRates menu action. **/
//	UpdateWellToPumpRatesAction updateWellToPumpRatesAction;
//	/** CreateFutureEmissionRates menu action. **/
//
	CreateFutureEmissionRatesAction createFutureEmissionRatesAction;
	/** State/County map action **/
	DrawStateCountyMapAction mapAction;
	/** Data Importer action **/
	DataImporterAction dataImporterAction;
	/** Nonroad Data Importer action **/
	NonroadDataImporterAction nonroadDataImporterAction;
	/** County Data Manager action **/
	CountyDataManagerAction countyDataManagerAction;
	/** Project Domain Manager action **/
	ProjectDomainManagerAction projectDomainManagerAction;
//	UpdateManufactureDisposalRates menu action.  This feature has been removed.
//	UpdateManufactureDisposalRatesAction updateManufactureDisposalRatesAction;
	/** Execute menu action. **/
	ExecuteAction executeAction;
	/** Stop menu action. **/
	StopAction stopAction;
	/** Pause menu action. **/
	PauseAction pauseAction;
	/** Resume menu action. **/
	ResumeAction resumeAction;
	/** Run MySQL Script Action **/
	RunScriptAction runScriptAction;
	/** Run MySQL Nonroad Script Action **/
	RunNonroadScriptAction runNonroadScriptAction;
	/** Convert Database Action **/
	ConverterAction converterAction;
	/** Convert 2014 Database Action **/
	Converter2014Action converter2014Action;
	/** Convert 2014A Database Action **/
	Converter2014aAction converter2014aAction;
	/** Run MySQL Script Action **/
	SummaryReportAction summaryReportAction;
	/** User Guide menu action. **/
	UserGuideAction userGuideAction;
	/** About menu action. **/
	AboutAction aboutAction;
	/** Configure menu action. **/
	ConfigureAction configureAction;
	/** MOVESRunErrorLog menu action. **/
	MOVESRunErrorLogAction MOVESRunErrorLogAction;
	/** Looping Tool menu action **/
	LoopingToolAction loopingToolAction;
	/** PDSpec menu action **/
	PDSpecGUIAction pdSpecGUIAction;
	/** MRU File list **/
	LinkedList<String> mruList;
	/** Status bar. **/
	JLabel status;
	/** File menu. **/
	JMenu fileMenu;
	/** Item in the Post Processing menue for onroad**/
	JMenuItem onRoadPostProcessingMenuItem;
	/** Item in the Post Processing menue for nonroad**/
	JMenuItem nonRoadPostProcessingMenuItem;

	/**
	 * Timer used with the log handler to display messages, if found in the message
	 * log queue, into a modal message box.  This is to prevent any threads from
	 * indirectly calling MOVESWindow.handleLog and displaying a modal message box, thus
	 * causing problems for those threads.
	**/
	javax.swing.Timer logHandlerTimer = new javax.swing.Timer(250, this);
	/** The message log queue. **/
	LinkedList<String> logHandlerQueue = new LinkedList<String>();
	/**
	 * Timer used to react when a simulation terminates.  By placing reaction logic
	 * within a timer, we avoid the engine's thread from manipulating the GUI and
	 * causing race conditions.
	**/
	javax.swing.Timer engineCompleteTimer = new javax.swing.Timer(250, this);
	/** Flag indicating if an unhandled call to engineIsCompleting has occurred **/
	boolean engineDidComplete = false;
	/** This adapter handles Mouse over messages on toolbar buttons and menu items. **/
	MouseHandler mouseHandler;
	/** Split pane to contain the navigation UI on the left and contents UI on the right. **/
	JSplitPane splitPane;
	/** Right scroll pane contains the various UI panels. **/
	JScrollPane rightScrollPane;
	/** Left scroll pane contains the application navigation panel. **/
	JScrollPane leftScrollPane;
	/** The main MOVES application navigation panel. **/
	public MOVESNavigation navigationPanel;
	/** Panel displays running progress. **/
	MOVESProgress progressPanel;
	/** Panel allows editing of the RunSpec description. **/
	Description descriptionPanel;
	/** Panel allows setting of the RunSpec scale. **/
	Scale scalePanel;
	/** Panel allows setting of the RunSpec macroscale geographic bounds. **/
	MacroscaleGeographicBounds macroscaleGeographicBoundsPanel;
	/** Panel allows setting of the RunSpec time spans. **/
	TimeSpans timeSpansPanel;
	/** Panel allows setting of the RunSpec on road vehicle equipment. **/
	OnRoadVehicleEquipment onRoadVehicleEquipmentPanel;
	/** Panel allows setting of the RunSpec NonRoad vehicle equipment. **/
	OffRoadVehicleEquipment offRoadVehicleEquipmentPanel;
	/** Panel allows setting of the RunSpec road type. **/
	RoadTypeScreen roadTypePanel;
	/** Panel allows setting of the RunSpec pollutants and processes. **/
	PollutantsAndProcesses pollutantsAndProcessesPanel;
	/** Panel allows managing of the RunSpec input data sets. **/
	ManageInputDataSets manageInputDataSetsPanel;
	/** Panel allows setting of the RunSpec output emissions breakdown. **/
	OutputEmissionsBreakdown outputEmissionsBreakdownPanel;
	/** Panel allows setting of the RunSpec general output. **/
	GeneralOutput generalOutputPanel;
	/** Panel for Advanced Performance Features of data capture and optional execution **/
	AdvancedPerformanceFeatures advancedPerformancePanel;
	/** The runspec being edited **/
	public RunSpec runSpec;
	/** The name of the current RunSpec. **/
	String runSpecFilePath = "";
	/** EPA copyright notice for MOVES **/
	public static final String COPYRIGHT_NOTICE =
			"Copyright U.S. Environmental Protection Agency ";
	/** GNU license Notice **/
	public static final String LICENSE_NOTICE =
			"Licensed for use pursuant to the GNU General Public License (GPL) ";
	/** GNU website link **/
	public static final String GNU_WEBSITE =
			"For information about the GPL see http://www.gnu.org/licenses/ ";
	/** Value for the maximum number of items to maintain in the MRU File list **/
	public static final int MAX_MRU_SIZE = 4;
	/** Value for the first menu position of the MRU items **/
	public static final int MRU_ITEM_OFFSET = 9;
	/** Value for the application title **/
	public static final String MOVES_APP_TITLE = new String("MOVES");
	/** Name of output file that performance profiles are written to **/
	static final String PERFORMANCE_PROFILER_FILE_NAME = "guiprofile.txt";
	/** Date of the Current Release **/
	public static final String MOVES_VERSION = "MOVES2014b-20181203";
	/** directory where output db processing scripts are located **/
	static final String DB_SCRIPTS_DIR = "database" + File.separator + "OutputProcessingScripts";
	static final String DB_NONROAD_SCRIPTS_DIR = "database" + File.separator + "NonroadProcessingScripts";
	/**
	 * Constructs the MOVESWindow frame, also creates, sets the layouts of the controls
	 * and initializes the actions and listeners.
	 * @param okToPopupMessages true if licensing messages can be shown immediately
	**/
	public MOVESWindow(boolean okToPopupMessages) {
		super(MOVES_APP_TITLE);
		ArrayList<Image> iconList = new ArrayList<Image>();
		iconList.add(new ImageIcon("gov/epa/otaq/moves/master/gui/images/moves_16x16.png").getImage());
		iconList.add(new ImageIcon("gov/epa/otaq/moves/master/gui/images/moves_32x32.png").getImage());
		iconList.add(new ImageIcon("gov/epa/otaq/moves/master/gui/images/moves_48x48.png").getImage());
		iconList.add(new ImageIcon("gov/epa/otaq/moves/master/gui/images/moves_256x256.png").getImage());
		setIconImages(iconList);
		MOVESAPI.setupFlagForMaster();
		// tell the logger object that this class implements LogHandler
		Logger.addLogHandler(this);
		runSpec = MOVESAPI.getTheAPI().getRunSpec();
		initActions();
		logHandlerTimer.start();
		engineCompleteTimer.start();
		status = createStatusBar();
		mouseHandler = new MouseHandler(status);
		//LinkedList<String> mruList = new LinkedList<String>();
		createControls();
		arrangeControls();
		if(okToPopupMessages) {
			handleAboutAction(true);
		}
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//prevents window from closing when exit canceled
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				handleExitAction(); // all windowClosing code done in exitAction
		/*		saveMRUList();
				DatabaseConnectionManager.flushTables();
				PerformanceProfiler.writeProfiles(PERFORMANCE_PROFILER_FILE_NAME);
				Logger.log(LogMessageCategory.DEBUG, "Exiting... Calling MOVESEngine.stop");
				try {
					MOVESAPI.getTheAPI().stopMOVESEngine();
				} catch(Exception e) {
					//Logger.logException(e);
					Logger.logError(e, "Failed to stop MOVES engine");
				}
				try {
					SystemConfiguration.getTheSystemConfiguration().saveConfigurationData();
				} catch(Exception e) {
					//Logger.logException(e);
					Logger.logError(e, "Failed to save configuration data");
				}
				MOVESThread.signalAllToTerminate();
				MOVESAPI.shutdownFlagForMaster();
				System.exit(0); */
			}
		});
		loadMRUList();
		System.out.println("MOVES is making its first connections to the database, "
				+ "this may take a moment.");
		loadAppDefaults();
		// This class receives engine progress notifications
		MOVESEngine.subscribeToProgress(this);
		addKeyListener(this);
		JRootPane root = getRootPane();
		if(root != null) {
			root.addKeyListener(this);
		}
		// Show the window and let the users interact with it now that all connections
		// have been made.
		setVisible(true);
		Logger.log(LogMessageCategory.INFO,"Main window shown. Ready for user interaction.");
	}
	/**
	 * Method invoked to receive progress notifications.
	 * <br>Currently not used in this class.
	 * @param srcEngine The MOVESEngine responsible for the notifications.
	**/
	public void engineProgressUpdate(MOVESEngine srcEngine) {
	}
	/**
	 * Called when the MOVESEngine object is completing
	 * @param srcEngine The MOVESEngine this notification comes from.
	**/
	public synchronized void engineIsCompleting(MOVESEngine srcEngine) {
		if(srcEngine.pdEntry == null || srcEngine.isLastPDSpecEntry) {
			engineDidComplete = true;
		}
	}
	/** Handles the engineCompleteTimer and checks if engineIsCompleting was called recently **/
	synchronized void handleEngineCompleteTimer() {
		if(engineDidComplete) {
			progressPanel.handleEngineIsCompleting();
			engineDidComplete = false;
			executeAction.setEnabled(true);
			stopAction.setEnabled(false);
			pauseAction.setEnabled(false);
			resumeAction.setEnabled(false);
			if(pdSpecGUIAction != null) {
				pdSpecGUIAction.setEnabled(true);
			}
			progressPanel.setProgressBarVisible(false);
			setProgressOnlyMode(false);
			navigationPanel.clearSelection();
			if(MOVESEngine.theInstance.pdEntry == null) {
				JOptionPane.showMessageDialog(this,
						"Run has ended. Output database was: " +
						MOVESAPI.getTheAPI().getRunSpec().outputDatabase.databaseName,
						"Run Completion",JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"DONE file pickup has ended.",
						"PDSpec Completion",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	/**
	 * Set the title of the window, showing the distributed ID if available.
	 * @param filePath the file name and path currently open.  May be null or empty.
	**/
	void setupTitle(String filePath) {
		String title = MOVES_APP_TITLE;
		if(filePath != null && filePath.length() > 0) {
			title += " - " + filePath;
		}
		if(SystemConfiguration.getTheSystemConfiguration().distributedMasterID != null) {
			title += " - ID "
					+ SystemConfiguration.getTheSystemConfiguration().distributedMasterID;
		}
		setTitle(title);
	}
	/**
	 * Loads the MRU file list from persisted storage.
	**/
	void loadMRUList() {
		// in case the call below fails, at least should be non-NULL
		mruList = new LinkedList<String>();
		try {
			FileInputStream fileInStream = new FileInputStream("mrulist.tmp");
			ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
			Object t = objInStream.readObject();
			if(t != null && t instanceof LinkedList) {
				LinkedList tl = (LinkedList)t;
				for(Iterator i=tl.iterator();i.hasNext();) {
					Object item = i.next();
					if(item != null) {
						mruList.add(item.toString());
					}
				}
			}
			UpdateMRUItems();
		} catch(Exception e) {
			// It is okay to start up without an "mrulist.tmp" file.
		}
	}
	/**
	 * Saves the MRU file list to persisted storage.
	**/
	void saveMRUList() {
		// get an output stream and save the list to it
		try {
			FileOutputStream fileOutStream = new FileOutputStream("mrulist.tmp");
			ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
			objOutStream.writeObject(mruList);
		} catch(Exception e) {
			Logger.logError(e, "Failed to save MRU List");
		}
	}
	/**
	 * Adds an MRU file to the list.  Also maintains the maximum length of the list.
	 * @param	fileName the file name and path of the new item to add as String.
	**/
	void addToMRUList(String fileName) {
		mruList.addFirst(fileName);
		// check if this fileName is already in the list
		int testIndex = mruList.lastIndexOf(fileName);
		if(testIndex > 0) {
			mruList.remove(testIndex);
		}
		for(int i = MAX_MRU_SIZE; i < mruList.size(); i++) {
			// remove any items from the end of the list, if larger than the max size
			mruList.remove(i);
		}
	}
	/**
	 * Updates the JMenu items based on the current MRU list.
	**/
	void UpdateMRUItems() {
		JMenuItem nextItem;
		String newText = new String("");
		for(int i = 0; i < 4; i++) {
			if(i < mruList.size()) {
				newText = mruList.get(i).toString();
			} else {
				newText = "[No recent file]";
			}
			switch(i) {
				case 0:
					previousFile1Action.setShortDescription(newText);
					nextItem = fileMenu.getItem(MRU_ITEM_OFFSET + i);
					nextItem.setText(previousFile1Action.getShortDescription());
					break;
				case 1:
					previousFile2Action.setShortDescription(newText);
					nextItem = fileMenu.getItem(MRU_ITEM_OFFSET + i);
					nextItem.setText(previousFile2Action.getShortDescription());
					break;
				case 2:
					previousFile3Action.setShortDescription(newText);
					nextItem = fileMenu.getItem(MRU_ITEM_OFFSET + i);
					nextItem.setText(previousFile3Action.getShortDescription());
					break;
				case 3:
					previousFile4Action.setShortDescription(newText);
					nextItem = fileMenu.getItem(MRU_ITEM_OFFSET + i);
					nextItem.setText(previousFile4Action.getShortDescription());
					break;
			}
		}
	}
	/** Creates and initializes the actions for this frame. **/
	void initActions() {
		newAction = new NewAction();
		newAction.addActionListener(this);
		openAction = new OpenAction();
		openAction.addActionListener(this);
		closeAction = new CloseAction();
		closeAction.addActionListener(this);
		saveAction = new SaveAction();
		saveAction.addActionListener(this);
		saveAsAction = new SaveAsAction();
		saveAsAction.addActionListener(this);
		printAction = new PrintAction();
		printAction.addActionListener(this);
		previousFile1Action = new PreviousFile1Action();
		previousFile1Action.addActionListener(this);
		previousFile2Action = new PreviousFile2Action();
		previousFile2Action.addActionListener(this);
		previousFile3Action = new PreviousFile3Action();
		previousFile3Action.addActionListener(this);
		previousFile4Action = new PreviousFile4Action();
		previousFile4Action.addActionListener(this);
		exitAction = new ExitAction();
		exitAction.addActionListener(this);
		cutAction = new CutAction();
		cutAction.addActionListener(this);
		copyAction = new CopyAction();
		copyAction.addActionListener(this);
		pasteAction = new PasteAction();
		pasteAction.addActionListener(this);
		clearAction = new ClearAction();
		clearAction.addActionListener(this);
		executeDataImporterAction = new ExecuteDataImporterAction();
		executeDataImporterAction.addActionListener(this);
// 		updateWellToPumpRatesAction was removed from the model
//		updateWellToPumpRatesAction = new UpdateWellToPumpRatesAction();
//		updateWellToPumpRatesAction.addActionListener(this);
//
		createFutureEmissionRatesAction = new CreateFutureEmissionRatesAction();
		createFutureEmissionRatesAction.addActionListener(this);
		dataImporterAction = new DataImporterAction();
		dataImporterAction.addActionListener(this);
		countyDataManagerAction = new CountyDataManagerAction();
		countyDataManagerAction.addActionListener(this);
		projectDomainManagerAction = new ProjectDomainManagerAction();
		projectDomainManagerAction.addActionListener(this);
		nonroadDataImporterAction = new NonroadDataImporterAction();
		nonroadDataImporterAction.addActionListener(this);
//         For now, this has been removed from the model.
//		updateManufactureDisposalRatesAction = new UpdateManufactureDisposalRatesAction();
//		updateManufactureDisposalRatesAction.addActionListener(this);
		executeAction = new ExecuteAction();
		executeAction.addActionListener(this);
		executeAction.setEnabled(false);
		stopAction = new StopAction();
		stopAction.addActionListener(this);
		stopAction.setEnabled(false);
		pauseAction = new PauseAction();
		pauseAction.addActionListener(this);
		pauseAction.setEnabled(false);
		resumeAction = new ResumeAction();
		resumeAction.addActionListener(this);
		resumeAction.setEnabled(false);
		runScriptAction = new RunScriptAction();
		runScriptAction.addActionListener(this);
		runNonroadScriptAction = new RunNonroadScriptAction();
		runNonroadScriptAction.addActionListener(this);
		converterAction = new ConverterAction();
		converterAction.addActionListener(this);
		converter2014Action = new Converter2014Action();
		converter2014Action.addActionListener(this);
		converter2014aAction = new Converter2014aAction();
		converter2014aAction.addActionListener(this);
		summaryReportAction = new SummaryReportAction();
		summaryReportAction.addActionListener(this);
		userGuideAction = new UserGuideAction();
		userGuideAction.addActionListener(this);
		aboutAction = new AboutAction();
		aboutAction.addActionListener(this);
		configureAction = new ConfigureAction();
		configureAction.addActionListener(this);
		MOVESRunErrorLogAction = new MOVESRunErrorLogAction();
		MOVESRunErrorLogAction.addActionListener(this);
		mapAction = new DrawStateCountyMapAction();
		mapAction.addActionListener(this);
		loopingToolAction = new LoopingToolAction();
		loopingToolAction.addActionListener(this);
		pdSpecGUIAction = new PDSpecGUIAction();
		pdSpecGUIAction.addActionListener(this);
	}
	/** Creates and initializes the menu for this frame, including actions and listeners. **/
	JMenuBar createMenu()  {
		JMenuBar menuBar = new JMenuBar();
		JMenuItem menuItem;
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuItem = fileMenu.add(newAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(openAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(closeAction);
		menuItem.addMouseListener(mouseHandler);
		fileMenu.addSeparator();
		menuItem = fileMenu.add(saveAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(saveAsAction);
		menuItem.addMouseListener(mouseHandler);
		fileMenu.addSeparator();
		menuItem = fileMenu.add(printAction);
		menuItem.addMouseListener(mouseHandler);
		fileMenu.addSeparator();
		menuItem = fileMenu.add(previousFile1Action);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(previousFile2Action);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(previousFile3Action);
		menuItem.addMouseListener(mouseHandler);
		menuItem = fileMenu.add(previousFile4Action);
		menuItem.addMouseListener(mouseHandler);
		fileMenu.addSeparator();
		menuItem = fileMenu.add(exitAction);
		menuItem.addMouseListener(mouseHandler);
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		/** Cut action has been disabled **/
		cutAction.setEnabled(true);
		menuItem = editMenu.add(cutAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = editMenu.add(copyAction);
		/** Copy action has been disabled **/
		copyAction.setEnabled(true);
		menuItem.addMouseListener(mouseHandler);
		menuItem = editMenu.add(pasteAction);
		menuItem.addMouseListener(mouseHandler);
		/** Paste action has been disabled **/
		pasteAction.setEnabled(true);
		editMenu.addSeparator();
		menuItem = editMenu.add(clearAction);
		menuItem.addMouseListener(mouseHandler);
		/** Copy action has been disabled **/
		clearAction.setEnabled(true);
		JMenu preProcessingMenu = new JMenu("Pre Processing");
		preProcessingMenu.setMnemonic('R');
		
		//menuItem = preProcessingMenu.add(executeDataImporterAction);
		//executeDataImporterAction.setEnabled(false);
		//menuItem.addMouseListener(mouseHandler);
// 		updateWellToPumpRatesAction was removed from the model
//		menuItem = preProcessingMenu.add(updateWellToPumpRatesAction);
//		//updateWellToPumpRatesAction.setEnabled(false)
//		menuItem.addMouseListener(mouseHandler);
//
		/* FERC removed from the draft model
		menuItem = preProcessingMenu.add(createFutureEmissionRatesAction);
		menuItem.addMouseListener(mouseHandler);
		*/
		menuItem = preProcessingMenu.add(dataImporterAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = preProcessingMenu.add(countyDataManagerAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = preProcessingMenu.add(projectDomainManagerAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = preProcessingMenu.add(nonroadDataImporterAction);
		menuItem.addMouseListener(mouseHandler);
//          This has been removed from the model.
//		menuItem = preProcessingMenu.add(updateManufactureDisposalRatesAction);
//		menuItem.addMouseListener(mouseHandler);
		JMenu actionMenu = new JMenu("Action");
		actionMenu.setMnemonic('A');
		menuItem = actionMenu.add(executeAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = actionMenu.add(stopAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = actionMenu.add(pauseAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = actionMenu.add(resumeAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = actionMenu.add(MOVESRunErrorLogAction);
		menuItem.addMouseListener(mouseHandler);
		JMenu postProcessingMenu = new JMenu("Post Processing");
		postProcessingMenu.setMnemonic('P');
		onRoadPostProcessingMenuItem = postProcessingMenu.add(runScriptAction);
		onRoadPostProcessingMenuItem.addMouseListener(mouseHandler);
		nonRoadPostProcessingMenuItem = postProcessingMenu.add(runNonroadScriptAction);
		nonRoadPostProcessingMenuItem.addMouseListener(mouseHandler);
		menuItem = postProcessingMenu.add(summaryReportAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = postProcessingMenu.add(mapAction);
		menuItem.addMouseListener(mouseHandler);

		postProcessingMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				onRoadPostProcessingMenuItem.setEnabled(true);
				nonRoadPostProcessingMenuItem.setEnabled(true);
				if (runSpec.outputDatabase == null || runSpec.outputDatabase.databaseName.length() < 3) {
				}//end of if runspec doesn't have an output database
				else{
					Connection oConn = runSpec.outputDatabase.openConnectionOrNull();
					if (oConn == null) {
					}//end of if can't connect
					else{
						try{
							ResultSet results = SQLRunner.executeQuery(oConn,"SELECT models FROM movesrun");
							boolean dbcontainsonroad= false; 														
							boolean dbcontainsnonroad= false; 
							while(results.next()){
								if (results.getString(1).equals("onroad")){
									dbcontainsonroad= true; 
								}
								if (results.getString(1).equals("nonroad")){
									dbcontainsnonroad= true; 
								}
							}
							onRoadPostProcessingMenuItem.setEnabled(dbcontainsonroad);
							nonRoadPostProcessingMenuItem.setEnabled(dbcontainsnonroad);
							if(dbcontainsonroad==false && dbcontainsnonroad==false){
								onRoadPostProcessingMenuItem.setEnabled(true);
								nonRoadPostProcessingMenuItem.setEnabled(true);							
							}
						} catch(SQLException x){}
					}//end of if you can connect to the output database 
					try {
						oConn.close();
					} catch (Exception x) {
						; // can't do anything if there is a failure to close
					}
				}//end of if runspec has an output database
			}
			@Override
			public void menuDeselected(MenuEvent e) {}
			@Override
			public void menuCanceled(MenuEvent e) {}
		});

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('T');
		menuItem = toolsMenu.add(loopingToolAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = toolsMenu.add(pdSpecGUIAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = toolsMenu.add(converter2014aAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem = toolsMenu.add(converter2014Action);
		menuItem.addMouseListener(mouseHandler);
		menuItem = toolsMenu.add(converterAction);
		menuItem.addMouseListener(mouseHandler);
		JMenu settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic('S');
		settingsMenu.setName("settingsMenu");
		menuItem = settingsMenu.add(configureAction);
		menuItem.addMouseListener(mouseHandler);
		menuItem.setName("configureMenuItem");
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		menuItem = helpMenu.add(userGuideAction);
		menuItem.addMouseListener(mouseHandler);
		helpMenu.addSeparator();
		menuItem = helpMenu.add(aboutAction);
		menuItem.addMouseListener(mouseHandler);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(preProcessingMenu);
		menuBar.add(actionMenu);
		menuBar.add(postProcessingMenu);
		if(toolsMenu != null) {
			menuBar.add(toolsMenu);
		}
		menuBar.add(settingsMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}
	/** Creates the status bar. **/
	JLabel createStatusBar()  {
		status = new JLabel("Ready...");
		status.setBorder(BorderFactory.createEtchedBorder());
		return status;
	}
	/**
	 * This method acts as the Action handler delegate for all the actions.
	 * @param evt The event caused by an action being performed.
	**/
	public void actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		// Compare the action command to the known actions.
		if (logHandlerTimer == evt.getSource()) {
			handleLogHandlerTimer();
		} else if (engineCompleteTimer == evt.getSource()) {
			handleEngineCompleteTimer();
		} else if (command.equals(newAction.getActionCommand()))  {
			handleNewAction();
		} else if (command.equals(openAction.getActionCommand())) {
			handleOpenAction();
		} else if (command.equals(closeAction.getActionCommand())) {
			handleCloseAction();
		} else if (command.equals(saveAction.getActionCommand())) {
			handleSaveAction();
		} else if (command.equals(saveAsAction.getActionCommand())) {
			handleSaveAsAction();
		} else if (command.equals(printAction.getActionCommand())) {
			handlePrintAction();
		} else if (command.equals(previousFile1Action.getActionCommand())) {
			handlePreviousFile1Action();
		} else if (command.equals(previousFile2Action.getActionCommand())) {
			handlePreviousFile2Action();
		} else if (command.equals(previousFile3Action.getActionCommand())) {
			handlePreviousFile3Action();
		} else if (command.equals(previousFile4Action.getActionCommand())) {
			handlePreviousFile4Action();
		} else if (command.equals(exitAction.getActionCommand())) {
			handleExitAction();
		} else if (command.equals(cutAction.getActionCommand())) {
			handleCutAction(evt);
		} else if (command.equals(copyAction.getActionCommand())) {
			handleCopyAction(evt);
		} else if (command.equals(pasteAction.getActionCommand())) {
			handlePasteAction(evt);
		} else if (command.equals(clearAction.getActionCommand())) {
			handleClearAction(evt);
		} else if (command.equals(executeDataImporterAction.getActionCommand())) {
			handleExecuteDataImporterAction(evt);
// updateWellToPumpRatesAction was removed from the model
//		} else if (command.equals(updateWellToPumpRatesAction.getActionCommand())) {
//			handleUpdateWellToPumpRatesAction(evt);
//
		} else if (command.equals(createFutureEmissionRatesAction.getActionCommand())) {
			handleCreateFutureEmissionRatesAction(evt);
//          Removed from the model.
//		} else if (command.equals(updateManufactureDisposalRatesAction.getActionCommand())) {
//			handleUpdateManufactureDisposalRatesAction(evt);
		} else if (command.equals(executeAction.getActionCommand())) {
			handleExecuteAction();
		} else if (command.equals(stopAction.getActionCommand())) {
			handleStopAction();
		} else if (command.equals(pauseAction.getActionCommand())) {
			handlePauseAction();
		} else if (command.equals(resumeAction.getActionCommand())) {
			handleResumeAction();
		} else if (command.equals(runScriptAction.getActionCommand())) {
			handleRunScriptAction();
		} else if (command.equals(runNonroadScriptAction.getActionCommand())) {
			handleRunNonroadScriptAction();
		} else if (command.equals(converterAction.getActionCommand())) {
			handleConverterAction(Converter.MODE_2010A_TO_2010B);
		} else if (command.equals(converter2014Action.getActionCommand())) {
			handleConverterAction(Converter.MODE_2010B_TO_2014);
		} else if (command.equals(converter2014aAction.getActionCommand())) {
			handleConverterAction(Converter.MODE_2014_TO_2014A);
		} else if (command.equals(summaryReportAction.getActionCommand())) {
			handleSummaryReportAction();
		} else if (command.equals(userGuideAction.getActionCommand())) {
			handleUserGuideAction();
		} else if (command.equals(aboutAction.getActionCommand())) {
			handleAboutAction(false);
		} else if (command.equals(configureAction.getActionCommand())) {
			handleConfigureAction();
		} else if (command.equals(MOVESRunErrorLogAction.getActionCommand())) {
			handleMOVESRunErrorLogAction();
		} else if (command.equals(mapAction.getActionCommand())) {
			handleMapAction();
		} else if (command.equals(dataImporterAction.getActionCommand())) {
			handleDataImporterAction();
		} else if (command.equals(countyDataManagerAction.getActionCommand())) {
			handleCountyDataManagerAction();
		} else if (command.equals(projectDomainManagerAction.getActionCommand())) {
			handleProjectDomainManagerAction();
		} else if (command.equals(nonroadDataImporterAction.getActionCommand())) {
			handleNonroadDataImporterAction();
		} else if (command.equals(loopingToolAction.getActionCommand())) {
			handleLoopingToolAction();
		} else if (command.equals(pdSpecGUIAction.getActionCommand())) {
			handlePDSpecGUIAction();
		}
	}
	/** Handles the New menu action. **/
	void handleNewAction() {
		int answer = JOptionPane.showConfirmDialog(this,
				getDialogWording("Do you want to save the existing runspec first?"),
				"Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			if(!handleSaveAction()) {
				return;
			}
		} else if(answer == JOptionPane.CANCEL_OPTION) {
			return;
		}
		loadAppDefaults();
	}
	/**
	 * Helper function to load some application defaults, including the default database
	 * connection.
	**/
	void loadAppDefaults() {
		ButtonModel preservedModel = navigationPanel.setNavigationSelection(null);
		navigationPanel.setNavigationSelection(preservedModel);
		runSpec = MOVESAPI.getTheAPI().getRunSpec();
		MOVESAPI.getTheAPI().setRunSpecFilePath(null);
		// get the default gui connection
		if(null == DatabaseConnectionManager.getGUIConnection(MOVESDatabaseType.DEFAULT)) {
			// if this call fails, then then default database connection settings
			// will not work, but will let the app continue with a "safe" error message
			// from the call to getGUIConnection()
		}
		navigationPanel.setNavigationSelection(preservedModel);
		setupTitle(null);
		navigationPanel.onFileNew();
	}
	/** Handles the Open menu action. **/
	void handleOpenAction() {
		int answer = JOptionPane.showConfirmDialog(this,
				getDialogWording("Do you want to save the existing runspec first?"),
				"Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			if(!handleSaveAction()) {
				return;
			}
		} else if(answer == JOptionPane.CANCEL_OPTION) {
			return;
		}
		FileDialog fd = new FileDialog(this, "Load File", FileDialog.LOAD);
		fd.setVisible(true); //fd.show();
		if ((fd.getDirectory() == null) || (fd.getFile() == null)) {
			return;
		}
		String filePath = fd.getDirectory() + fd.getFile();
		openFile(filePath);
	}
	/**
	 * Helper function used by the various open file actions.
	 * @param filePath the file name and path as String to open.
	**/
	void openFile(String filePath) {
		ButtonModel preservedModel = navigationPanel.setNavigationSelection(null);
		File fileObject = new File(filePath);
		if(!MOVESAPI.getTheAPI().loadRunSpec(fileObject, true)) {
			navigationPanel.setNavigationSelection(preservedModel);
			return;
		}
		runSpec = MOVESAPI.getTheAPI().getRunSpec();
		navigationPanel.onFileOpen();
		runSpecFilePath = filePath;
		addToMRUList(filePath);
		UpdateMRUItems();
		setupTitle(filePath);
		navigationPanel.setNavigationSelection(preservedModel);
	}
	/** Handles the Close menu action. **/
	void handleCloseAction() {
		handleNewAction(); // works just like New since there is alway a RunSpec open
	}
	/**
	 * Handles the Save menu action.
	 * @return true if the file was saved successfully
	**/
	boolean handleSaveAction() {
		if(MOVESAPI.getTheAPI().getRunSpecFilePath() == null
				|| MOVESAPI.getTheAPI().getRunSpecFilePath().length() == 0) {
			return handleSaveAsAction();
		}
		//if(navigationPanel.activeEditor != null) {
		//	navigationPanel.activeEditor.saveToRunSpec(runSpec);
		//}
		navigationPanel.commitActiveEditor();
		ButtonModel preservedModel = navigationPanel.setNavigationSelection(null);
		navigationPanel.setNavigationSelection(preservedModel);
		return MOVESAPI.getTheAPI().saveRunSpec();
	}
	/**
	 * Handles the Save As menu action.
	 * @return true if the file was saved successfully
	**/
	boolean handleSaveAsAction() {
		navigationPanel.commitActiveEditor();
		FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
		fd.setVisible(true); //fd.show();
		if ((fd.getDirectory() == null) || (fd.getFile() == null)) {
			return false;
		}
		String filePath = fd.getDirectory() + fd.getFile();
		File fileObject = new File(filePath);
		MOVESAPI.getTheAPI().setRunSpecFilePath(fileObject);
		runSpecFilePath = filePath;
		addToMRUList(filePath);
		UpdateMRUItems();
		setupTitle(filePath);
		return MOVESAPI.getTheAPI().saveRunSpec();
	}
	/** Handles the Print menu action. **/
	void handlePrintAction() {
		navigationPanel.commitActiveEditor();
		StringBuffer textBuffer = new StringBuffer();
		textBuffer.append("EPA MOVES RunSpec File Name:\r\n");
		if(MOVESAPI.getTheAPI().getRunSpecFilePath() == null
				|| MOVESAPI.getTheAPI().getRunSpecFilePath().length() == 0) {
			textBuffer.append("\t(unnamed)");
		} else {
			textBuffer.append("\t" + MOVESAPI.getTheAPI().getRunSpecFilePath());
		}
		textBuffer.append("\r\n\r\n");
		Iterator panelIterator = navigationPanel.panels.listIterator();
		while (panelIterator.hasNext()) {
			JPanel iterPanel = (JPanel)panelIterator.next();
			if (iterPanel instanceof RunSpecEditor) {
				RunSpecEditor iterEditor = (RunSpecEditor)iterPanel;
				iterEditor.getPrintableDescription(runSpec, textBuffer);
			}
		}
		TextPrinter.printText(this, textBuffer.toString());
	}
	/** Handles the Previous File 1 menu action. **/
	void handlePreviousFile1Action() {
		String fileName = getMRUListItem(0);
		if(fileName.length() > 0) {
			openFile(fileName);
		}
	}
	/** Handles the Previous File 2 menu action. **/
	void handlePreviousFile2Action() {
		String fileName = getMRUListItem(1);
		if(fileName.length() > 0) {
			openFile(fileName);
		}
	}
	/** Handles the Previous File 3 menu action. **/
	void handlePreviousFile3Action() {
		String fileName = getMRUListItem(2);
		if(fileName.length() > 0) {
			openFile(fileName);
		}
	}
	/** Handles the Previous File 4 menu action. **/
	void handlePreviousFile4Action() {
		String fileName = getMRUListItem(3);
		if(fileName.length() > 0) {
			openFile(fileName);
		}
	}
	/**
	 * Helper function that finds the corresponding MRU filename from the specified index.
	 * @param 	index indicates which MRU list item to get.
	 * @return	the file name and path as String.
	**/
	String getMRUListItem(int index) {
		if((mruList.size() > index) && (mruList.get(index).toString().length() > 0)) {
			return mruList.get(index).toString();
		}
		return new String("");
	}
	/** Handles the Exit menu action. **/
	void handleExitAction() {
		int answer = JOptionPane.showConfirmDialog(this,
		getDialogWording("Do you want to save the run specification before ending?"),
		"Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			if(!handleSaveAction()) {
				return;
			}
		}
		else if(answer == JOptionPane.CANCEL_OPTION) {
			return;
		}
		saveMRUList(); // from windowClosing()
		DatabaseConnectionManager.flushTables();
		PerformanceProfiler.writeProfiles(PERFORMANCE_PROFILER_FILE_NAME);
		if(MOVESEngine.theInstance.isRunning()) {
			try { //from windowClosing()
				Logger.log(LogMessageCategory.INFO, "Exiting... Calling MOVESEngine.stop");
				MOVESAPI.getTheAPI().stopMOVESEngine();
			} catch(Exception e) {
				//Logger.logException(e);
				Logger.logError(e, "Failed to stop MOVES engine");
			}
		}
		try { //from windowClosing()
			SystemConfiguration.getTheSystemConfiguration().saveConfigurationData();
		} catch(Exception e) {
			//Logger.logException(e);
			Logger.logError(e, "Failed to save configuration data");
		}
		MOVESThread.signalAllToTerminate();
		TemporaryFileManager.doDeletions(true);
		MOVESAPI.shutdownFlagForMaster(); //from windowClosing()
		System.exit(0);
	}
	/**
	 * Handles the Clear menu action.
	 * @param e The event caused by the action.
	**/
	void handleClearAction(ActionEvent e) {
		DefaultEditorKit d = new DefaultEditorKit();
		Action[] actionsArray = d.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			if(a.getValue(Action.NAME).equals(DefaultEditorKit.selectAllAction)) {
				a.actionPerformed(e);
				break;
			}
		}
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			if(a.getValue(Action.NAME).equals(DefaultEditorKit.deleteNextCharAction)) {
				a.actionPerformed(e);
				break;
			}
		}
	}
	/**
	 * Handles the ExecuteDataImporter menu action.
	 * @param e The event caused by the action.
	**/
	void handleExecuteDataImporterAction(ActionEvent e) {
		JOptionPane.showMessageDialog(this, executeDataImporterAction.getLongDescription(),
				executeDataImporterAction.getShortDescription(),
				JOptionPane.INFORMATION_MESSAGE);
	}
// updateWellToPumpRatesAction was removed from the model
//	/**
//	 * Handles the UpdateWellToPumpRates menu action.
//	 * @param e The event caused by the action.
//	**/
//	void handleUpdateWellToPumpRatesAction(ActionEvent e) {
//		GREETInterface gi = new GREETInterface(runSpec);
//		gi.updateWellToPumpRates();
//	}
//
	/**
	 * Handles the CreateFutureEmissionRates menu action.
	 * @param e The event caused by the action.
	**/
	void handleCreateFutureEmissionRatesAction(ActionEvent e) {
		Ferc f = new Ferc(this);
		// simple offset from main window origin
		f.setLocation(getLocationOnScreen().x + 100, getLocationOnScreen().y + 100);
		(new WindowStateHandler(f)).setSizePositionAndStartTracking(-1,-1);
		f.showModal();
	}
	/**
	 * Handle the Data Importer menu action.
	**/
	void handleDataImporterAction() {
		checkImporterActions();
		if(!dataImporterAction.isEnabled()) {
			return;
		}
		showImporterManager(null,ImporterManager.STANDARD_MODE);
	}
	/**
	 * Handle the Nonroad Data Importer menu action.
	**/
	void handleNonroadDataImporterAction() {
		checkImporterActions();
		if(!nonroadDataImporterAction.isEnabled()) {
			return;
		}
		showImporterManager(null,ImporterManager.STANDARD_MODE);
	}
	/**
	 * Handle the County Data Manager menu action.
	**/
	void handleCountyDataManagerAction() {
		checkImporterActions();
		if(!countyDataManagerAction.isEnabled()) {
			return;
		}
		showImporterManager(ModelDomain.SINGLE_COUNTY,ImporterManager.STANDARD_MODE);
	}
	/**
	 * Handle the Project Domain Manager menu action.
	**/
	void handleProjectDomainManagerAction() {
		checkImporterActions();
		if(!projectDomainManagerAction.isEnabled()) {
			return;
		}
		showImporterManager(ModelDomain.PROJECT,ImporterManager.STANDARD_MODE);
	}
	/**
	 * Show the ImporterManager GUI with the option of doing so for a county domain.
	 * @param domainType domain type for the manager, or null for general importing.
	 * @param mode one of the ImporterManager.*_MODE constants
	**/
	void showImporterManager(ModelDomain domainType, int mode) {
		boolean isCountyDomain = domainType == ModelDomain.SINGLE_COUNTY;
		boolean isProjectDomain = domainType == ModelDomain.PROJECT;
		RunSpecEditor editor = navigationPanel.activeEditor;
		if(editor == null) {
			editor = navigationPanel.lastRunSpecEditor;
		}
		navigationPanel.commitActiveEditor();
		if(isCountyDomain || isProjectDomain) {
			ArrayList<String> messages = new ArrayList<String>();
			int result = ImporterManager.isReadyForCountyDomain(null,messages);
			if(result < 0) {
				// Display the error messages
				String t = "";
				if(isCountyDomain) {
					t = "Unable to open the County Data Manager.";
				} else if(isProjectDomain) {
					t = "Unable to open the Project Domain Manager.";
				}
				for(Iterator<String> i=messages.iterator();i.hasNext();) {
					t += "\r\n";
					t += i.next();
				}
				JOptionPane.showMessageDialog(this, t, "Error",	JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		ImporterManager.display(this,null,
				getLocationOnScreen().x + 50, getLocationOnScreen().y + 50,
				domainType,mode,Models.evaluateModels(runSpec.models));
		if(editor != null) {
			editor.loadFromRunSpec(runSpec);
		}
		rightScrollPane.invalidate();
		rightScrollPane.setVisible(false);
		rightScrollPane.setVisible(true);
	}
//	/**
//	 * Handles the UpdateManufactureDisposalRates menu action.
//	 * @param e The event caused by the action.
//	**/
//	void handleUpdateManufactureDisposalRatesAction(ActionEvent e) {
//		GREETInterface gi = new GREETInterface(runSpec);
//		gi.updateManufactureDisposalRates();
//	}
	/**
	 * Handles the Cut menu action.
	 * @param e The event caused by the action.
	**/
	void handleCutAction(ActionEvent e) {
		DefaultEditorKit d = new DefaultEditorKit();
		Action[] actionsArray = d.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			if(a.getValue(Action.NAME).equals(DefaultEditorKit.cutAction)) {
				a.actionPerformed(e);
				return;
			}
		}
	}
	/**
	 * Handles the Copy menu action.
	 * @param e The event caused by the action.
	**/
	void handleCopyAction(ActionEvent e) {
		DefaultEditorKit d = new DefaultEditorKit();
		Action[] actionsArray = d.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			if(a.getValue(Action.NAME).equals(DefaultEditorKit.copyAction)) {
				a.actionPerformed(e);
				return;
			}
		}
	}
	/**
	 * Handles the Paste menu action.
	 * @param e The event caused by the action.
	**/
	void handlePasteAction(ActionEvent e) {
		DefaultEditorKit d = new DefaultEditorKit();
		Action[] actionsArray = d.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			if(a.getValue(Action.NAME).equals(DefaultEditorKit.pasteAction)) {
				a.actionPerformed(e);
				return;
			}
		}
	}
	/**
	 * Obtain wording for a Yes/No/Cancel dialog that requires the description, if present,
	 * of the active RunSpec.
	 * @param primaryText primary text for the dialog.
	 * @return wording to use on the dialog.  This includes the primary wording and may
	 * include the RunSpec's description if one has been set.
	**/
	String getDialogWording(String primaryText) {
		String description = null;
		if(runSpec.description != null) {
			description = runSpec.description.trim();
			if(description.length() > 0) {
				if(description.length() > 500) {
					description = description.substring(0,500);
				}
				description = StringUtilities.wrapString("Description: " + description,60);
			}
		}
		if(description != null && description.length() > 0) {
			return primaryText + "\n" + description;
		} else {
			return primaryText;
		}
	}
	/** Handles the Execute menu action. **/
	synchronized void handleExecuteAction() {
		checkExecuteAction();
		if(!executeAction.isEnabled()) {
			return;
		}
		// Commit the currently active (i.e. selected) RunSpec editor to the runSpec before
		// starting the MOVESEngine.
		navigationPanel.commitActiveEditor();
		int answer = JOptionPane.showConfirmDialog(this,
				getDialogWording("Do you want to save the run specification before executing?"),
				"Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			if(!handleSaveAction()) {
				return;
			}
		} else if(answer == JOptionPane.NO_OPTION) {
			//Nothing to do upon NOr
		} else { // else if cancelled or merely closed
			return;
		}
		navigationPanel.clearSelection();
		progressPanel.allowTimeDialog = true;
		if(!MOVESAPI.getTheAPI().startMOVESEngine()) {
			return;
		}
		executeAction.setEnabled(false);
		stopAction.setEnabled(true);
		pauseAction.setEnabled(true);
		resumeAction.setEnabled(false);
		if(pdSpecGUIAction != null) {
			pdSpecGUIAction.setEnabled(false);
		}
		progressPanel.setProgressBarVisible(true);
		setProgressOnlyMode(true);
	}
	/** Handles the Stop menu action. **/
	void handleStopAction() {
		Logger.log(LogMessageCategory.WARNING, "Execution Terminated by User");
		try {
			MOVESAPI.getTheAPI().stopMOVESEngine();
		} catch(Exception e) {
			Logger.logError(e, "Failed to stop MOVES engine");
		}
	}
	/** Handles the Pause menu action. **/
	synchronized void handlePauseAction() {
		MOVESAPI.getTheAPI().pauseMOVESEngine();
		pauseAction.setEnabled(false);
		resumeAction.setEnabled(true);
	}
	/** Handles the Resume menu action. **/
	synchronized void handleResumeAction() {
		MOVESAPI.getTheAPI().resumeMOVESEngine();
		pauseAction.setEnabled(true);
		resumeAction.setEnabled(false);
	}
	/**
	 * Handles the Draw State/County Map menu action.
	**/
	void handleMapAction() {
		RunSpecEditor editor = navigationPanel.activeEditor;
		if(editor == null) {
			editor = navigationPanel.lastRunSpecEditor;
		}
		navigationPanel.commitActiveEditor();
		if (runSpec.outputDatabase == null | runSpec.outputDatabase.databaseName.length() < 3) {
			JOptionPane.showMessageDialog(this,
				"Can't create map: No output DB specified in current RunSpec",
				mapAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		Logger.log(LogMessageCategory.INFO,"Output database name: " + runSpec.outputDatabase.databaseName);
		Connection oConn = runSpec.outputDatabase.openConnectionOrNull();
		if (oConn == null) {
			JOptionPane.showMessageDialog(this,
				"Can't create map: Can't connect to output database",
				mapAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			StateCountyMapGUI mapGUI = new StateCountyMapGUI(this,oConn,
					runSpec.outputDatabase.databaseName);
			// simple offset from main window origin
			mapGUI.setLocation(getLocationOnScreen().x + 50, getLocationOnScreen().y + 50);
			mapGUI.showModal();
		} catch(Exception e) {
			Logger.log(LogMessageCategory.ERROR,
					"Exception occurred while creating map: " + e);
			e.printStackTrace();
		} finally {
			try {
				oConn.close();
			} catch (Exception e) {
				; // can't do anything if there is a failure to close
			}
			oConn = null;
		}
		if(editor != null) {
			editor.loadFromRunSpec(runSpec);
		}
		rightScrollPane.invalidate();
		rightScrollPane.setVisible(false);
		rightScrollPane.setVisible(true);
	}
	/** Handles the Run Script menu action. **/
	void handleRunScriptAction() {
		navigationPanel.commitActiveEditor();
		if (runSpec.outputDatabase == null | runSpec.outputDatabase.databaseName.length() < 3) {
			JOptionPane.showMessageDialog(this,
				"Can't run script: No output DB specified in current RunSpec",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		Logger.log(LogMessageCategory.INFO,"Output database name: " + runSpec.outputDatabase.databaseName);
		Connection oConn = runSpec.outputDatabase.openConnectionOrNull();
		if (oConn == null) {
			JOptionPane.showMessageDialog(this,
				"Can't run script: Can't connect to output database",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		File scriptsDir = new File(DB_SCRIPTS_DIR);
		String[] scriptNames = scriptsDir.list(this);
		if (scriptNames == null | scriptNames.length == 0) {
			try {
				oConn.close();
			} catch (Exception e) {
				; // can't do anything if there is a failure to close
			}
			JOptionPane.showMessageDialog(this,
				"No MySQL script selected",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		String scriptName = (String) JOptionPane.showInputDialog (
			this, "Select output processing script", "Select Script",
			JOptionPane.QUESTION_MESSAGE,null,scriptNames,null);
		if (scriptName == null) {
			return;
		}
		Logger.log(LogMessageCategory.INFO,"Selected script name was: " + scriptName);
		boolean scriptError = false;
		File scriptFile = new File(DB_SCRIPTS_DIR + File.separator + scriptName);
		Object[] commentBlock = FileUtilities.extractInitialCommentBlock(scriptFile);
		int userStatus = JOptionPane.showConfirmDialog (this, commentBlock,
			"Post-processing Script Documentation", JOptionPane.OK_CANCEL_OPTION);
		if (userStatus == JOptionPane.CANCEL_OPTION || userStatus == JOptionPane.CLOSED_OPTION) {
			return;
		}
		// put logic hear to display file header and
		// ask for user confirmation before continuing
		try{
			TreeMapIgnoreCase replacements = new TreeMapIgnoreCase();
			// Add ##defaultdb## replacement.
			DatabaseSelection defaultDatabase = SystemConfiguration.getTheSystemConfiguration().databaseSelections[MOVESDatabaseType.DEFAULT.getIndex()];
			replacements.put("##defaultdb##",defaultDatabase.databaseName);
			replacements.put("##scaleinputdb##",runSpec.scaleInputDatabase.databaseName);			
			DatabaseUtilities.executeScript(oConn,scriptFile,replacements);
		} catch (Exception e) {
			Logger.log(LogMessageCategory.ERROR,
					"Exception occurred running post-processing script" + e);
			scriptError=true;
		}
		try {
			oConn.close();
		} catch (Exception e) {
			; // can't do anything if there is a failure to close
		}
		if (scriptError) {
			JOptionPane.showMessageDialog(this,
			"Error occurred running post-processing script",
			runScriptAction.getShortDescription(),
			JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
			"Post processing script executed successfully",
			runScriptAction.getShortDescription(),
			JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/** Handles the Run Nonroad Script menu action. **/
	void handleRunNonroadScriptAction() {
		navigationPanel.commitActiveEditor();
		if (runSpec.outputDatabase == null | runSpec.outputDatabase.databaseName.length() < 3) {
			JOptionPane.showMessageDialog(this,
				"Can't run script: No output DB specified in current RunSpec",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		Logger.log(LogMessageCategory.INFO,"Output database name: " + runSpec.outputDatabase.databaseName);
		Connection oConn = runSpec.outputDatabase.openConnectionOrNull();
		if (oConn == null) {
			JOptionPane.showMessageDialog(this,
				"Can't run script: Can't connect to output database",
				runNonroadScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		File scriptsDir = new File(DB_NONROAD_SCRIPTS_DIR);
		String[] scriptNames = scriptsDir.list(this);
		if (scriptNames == null | scriptNames.length == 0) {
			try {
				oConn.close();
			} catch (Exception e) {
				; // can't do anything if there is a failure to close
			}
			JOptionPane.showMessageDialog(this,
				"No MySQL script selected",
				runNonroadScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		String scriptName = (String) JOptionPane.showInputDialog (
			this, "Select Nonroad processing script", "Select Script",
			JOptionPane.QUESTION_MESSAGE,null,scriptNames,null);
		if (scriptName == null) {
			return;
		}
		Logger.log(LogMessageCategory.INFO,"Selected script name was: " + scriptName);
			
		File scriptFile = new File(DB_NONROAD_SCRIPTS_DIR + File.separator + scriptName);
		Object[] commentBlock = FileUtilities.extractInitialCommentBlock(scriptFile);
		// Display file header and ask for user confirmation before continuing
		int userStatus = JOptionPane.showConfirmDialog (this, commentBlock,
			"Nonroad post-processing Script Documentation", JOptionPane.OK_CANCEL_OPTION);
		if (userStatus == JOptionPane.CANCEL_OPTION || userStatus == JOptionPane.CLOSED_OPTION) {
			return;
		}
		
		// Ask where to save output
		navigationPanel.commitActiveEditor();
		FileDialog fd = new FileDialog(this,
				"Save Nonroad Post Processing Script Output As...", FileDialog.SAVE);
		fd.setVisible(true); //fd.show();
		if ((fd.getDirectory() == null) || (fd.getFile() == null)) {
			return;
		}
		String saveFileName = fd.getDirectory() + fd.getFile();
		
		boolean hasErrors = false;
		try {
			// Add ##defaultdb## replacement.
			TreeMapIgnoreCase replacements = new TreeMapIgnoreCase();
			DatabaseSelection defaultDatabase = SystemConfiguration.getTheSystemConfiguration().databaseSelections[MOVESDatabaseType.DEFAULT.getIndex()];
			replacements.put("##defaultdb##",defaultDatabase.databaseName);
			replacements.put("##scaleinputdb##",runSpec.scaleInputDatabase.databaseName);			
			DatabaseUtilities.executeScript(oConn,scriptFile,replacements);
			hasErrors = RunNonroadScriptActionHelper.processScriptOutput(scriptName, saveFileName, oConn);			
		} catch (Exception e) {
			hasErrors = true;
			Logger.log(LogMessageCategory.ERROR,
					"Exception occurred running nonroad post-processing script" + e);
		}
		
		if (hasErrors) {
			JOptionPane.showMessageDialog(this,
					"Error occurred running nonroad post-processing script",
					runNonroadScriptAction.getShortDescription(),
					JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Nonroad post processing script executed successfully",
					runNonroadScriptAction.getShortDescription(),
					JOptionPane.INFORMATION_MESSAGE);
		}
		
		try {
			oConn.close();
		} catch (Exception e) {
			; // can't do anything if there is a failure to close
		}
	}
	
	/** Implements the FilenameFilter Interface **/
	public boolean accept(File f, String fn) {
		return (fn.toUpperCase()).endsWith(".SQL");
	}
	/** Handles the Summary Report menu action. **/
	void handleSummaryReportAction() {
		navigationPanel.commitActiveEditor();
		if (runSpec.outputDatabase == null || runSpec.outputDatabase.databaseName.length() < 3) {
			JOptionPane.showMessageDialog(this,
				"Can't produce summary report: No output DB specified in current RunSpec",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		// System.out.println("Output database name: " + runSpec.outputDatabase.databaseName);
		Connection oConn = runSpec.outputDatabase.openConnectionOrNull();
		if (oConn == null) {
			JOptionPane.showMessageDialog(this,
				"Can't produce summary report: Can't connect to output database",
				runScriptAction.getShortDescription(),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		SummaryReporter sr = new SummaryReporter(this, oConn,runSpec);
		//boolean reportStatus = sr.produceSummaryReport();
		sr.produceSummaryReport();
		try {
			oConn.close();
		} catch (Exception e) {
			; // can't do anything if there is a failure to close
		}
		/*if (!reportStatus) {
			JOptionPane.showMessageDialog(this,
			"Some or all summary reports not generated",
			runScriptAction.getShortDescription(),
			JOptionPane.ERROR_MESSAGE);
		} else {
			String[] message = new String[2];
			message[0] = "Summary Report(s) generated successfully";
			message[1] = "Output Database is: " + runSpec.outputDatabase.databaseName;
			JOptionPane.showMessageDialog(this, message,
			runScriptAction.getShortDescription(), JOptionPane.INFORMATION_MESSAGE);
		}*/
	}
	/**
	 * Handles the Convert Database menu action.
	 * @param mode one of the Converter.MODE_* constants.
	**/
	void handleConverterAction(int mode) {
		Converter c = new Converter(this,mode);
		// simple offset from main window origin
		c.setLocation(getLocationOnScreen().x + 100, getLocationOnScreen().y + 100);
		c.showModal();
	}
	/** Handles the Configure menu action. **/
	void handleConfigureAction() {
		Configure c = new Configure(this);
		// simple offset from main window origin
		c.setLocation(getLocationOnScreen().x + 100, getLocationOnScreen().y + 100);
		c.showModal();
	}
	/** Handles the User Guide menu action. **/
	void handleUserGuideAction() {
		showURL("http://www.epa.gov/otaq/models/moves/");
	}
	/**
	 * Open a web page in the default browser.
	 * @param url web page to be shown.
	**/
	void showURL(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		} catch(Exception e) {
			Logger.logError(e,"Unable to display documentation: " + e.getMessage());
		}
	}
	/**
	 * Display a PDF file.
	 * @param purpose type of PDF file, used for error message display
	 * @param fileName name and relative path to the PDF file
	**/
	void showPDF(String purpose, String fileName) {
		boolean success = false;
		File pdfFile = new File(fileName);
		Process process = null;
		try {
			if(pdfFile.exists()) {
				process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler \""
						+ pdfFile.getCanonicalPath() + "\"");
				if(process != null) {
					int exitValue = -314;
					try {
						exitValue = process.waitFor();
					} catch(Exception e) {
						// Nothing to do here
					}
					//System.out.println("exitValue=" + exitValue);
					if(exitValue == 0) {
						success = true;
					}
				}
			}
		} catch(Exception e) {
			success = false;
			Logger.logError(e,"Unable to open PDF file: " + fileName);
		}
		if(!success) {
			JOptionPane.showMessageDialog(this,
					"Unable to display " + purpose + " PDF file.\n"
					+ "Check that Adobe Acrobat Reader or other software for\n"
					+ "reading PDF files has been installed.",
					"Error Opening PDF", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * Handles the About menu action.
	 * @param shouldTimeout true if the dialog should be removed after a short time
	**/
	void handleAboutAction(boolean shouldTimeout) {
		// TODO Expire the about message box after a short time
		JOptionPane aboutPane = new JOptionPane(aboutAction.getLongDescription()
				+ "\n" + "This version : " + MOVES_VERSION
				+ "\n" + "Computer ID : "
				+ SystemConfiguration.getTheSystemConfiguration().computerID
				+ "\n" + COPYRIGHT_NOTICE
				+ "\n" + LICENSE_NOTICE
				+ "\n" + GNU_WEBSITE,
				JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION);
		JDialog aboutDialog = aboutPane.createDialog(this, aboutAction.getShortDescription());
		aboutDialog.setAlwaysOnTop(true);
		aboutDialog.setVisible(true);
	}
	/**
	 * Handles the Looping Tool menu action.
	**/
	void handleLoopingToolAction() {
		LoopingTool lt = new LoopingTool(this,runSpec,isRunSpecReady());
		// simple offset from main window origin
		lt.setLocation(getLocationOnScreen().x + 100, getLocationOnScreen().y + 100);
		lt.showModal();
	}
	/** Creates and initializes all controls on this frame. **/
	public void createControls() {
		setJMenuBar(createMenu());
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		progressPanel = new MOVESProgress(true);
		descriptionPanel = new Description();
		descriptionPanel.setName("descriptionPanel");
		scalePanel = new Scale();
		scalePanel.setName("scalePanel");
		macroscaleGeographicBoundsPanel = new MacroscaleGeographicBounds();
		macroscaleGeographicBoundsPanel.setName("macroscaleGeographicBoundsPanel");
		timeSpansPanel = new TimeSpans();
		timeSpansPanel.setName("timeSpansPanel");
		onRoadVehicleEquipmentPanel = new OnRoadVehicleEquipment();
		onRoadVehicleEquipmentPanel.setName("onRoadVehicleEquipmentPanel");
		offRoadVehicleEquipmentPanel = new OffRoadVehicleEquipment();
		offRoadVehicleEquipmentPanel.setName("offRoadVehicleEquipmentPanel");
		roadTypePanel = new RoadTypeScreen();
		roadTypePanel.setName("roadTypePanel");
		roadTypePanel.movesRootWindow = this;
		pollutantsAndProcessesPanel = new PollutantsAndProcesses();
		pollutantsAndProcessesPanel.setName("pollutantsAndProcessesPanel");
		manageInputDataSetsPanel = new ManageInputDataSets();
		manageInputDataSetsPanel.setName("manageInputDataSetsPanel");
		outputEmissionsBreakdownPanel = new OutputEmissionsBreakdown();
		outputEmissionsBreakdownPanel.setName("outputEmissionsBreakdownPanel");
		generalOutputPanel = new GeneralOutput();
		generalOutputPanel.setName("generalOutputPanel");
		advancedPerformancePanel = new AdvancedPerformanceFeatures();
		advancedPerformancePanel.setName("advancedPerformancePanel");
		// Since MOVESNavigation depends upon the other panels, create it after the others.
		navigationPanel = new MOVESNavigation(this);
	}
	/** Sets the layout of the controls. **/
	public void arrangeControls() {
		splitPane.setDividerLocation(0.25);
		splitPane.setLeftComponent(leftScrollPane);
		splitPane.setRightComponent(rightScrollPane);
		leftScrollPane.setViewportView(navigationPanel);
		rightScrollPane.setViewportView(progressPanel);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, "Center");
		getContentPane().add(status, "South");
		splitPane.setDividerLocation(200);
		progressPanel.setPreferredSize(new Dimension(460, 600));
		//setSize(820,600);
		(new WindowStateHandler(this)).setSizePositionAndStartTracking(820,600);
		//setVisible(true); Don't do this here or the user will be able to interact
		//					with the GUI before all database connections have been made.
	}
	/**
	 * Sets the window in one of two modes:
	 * <ul>
	 * <li>Progress Only Mode: Only the progress panel is visible
	 * <li>Run Spec Edit Mode: The progress panel is initially visible. The left side
	 * of the window contains navigation controls to reach all the different parameters
	 * editing screens.
	 * </ul>
	 * @param progressOnlyMode true if the progress panel should be visible
	**/
	public void setProgressOnlyMode(boolean progressOnlyMode) {
		if (progressOnlyMode) {
			if (getContentPane().isAncestorOf(splitPane)) {
				navigationPanel.setNavigationSelection(null);
				getContentPane().remove(splitPane);
				getContentPane().add(progressPanel, "Center");
				getContentPane().validate();
			}
		} else {
			if (!getContentPane().isAncestorOf(splitPane)) {
				navigationPanel.setNavigationSelection(null);
				getContentPane().remove(progressPanel);
				splitPane.setLeftComponent(leftScrollPane);
				splitPane.setRightComponent(rightScrollPane);
				leftScrollPane.setViewportView(navigationPanel);
				rightScrollPane.setViewportView(progressPanel);
				getContentPane().setLayout(new BorderLayout());
				getContentPane().add(splitPane, "Center");
				getContentPane().add(status, "South");
				setVisible(true);
				getContentPane().validate();
			}
		}
		repaint();
	}
	/**
	 * This adapter is constructed to handle mouse over component events.
	**/
	class MouseHandler extends MouseAdapter  {
		/* Label with the long description of a component. */
		JLabel label;
		/**
		 * Constructor for the adapter.
		 * @param label the JLabel which will recieve value of the
		 *              Action.LONG_DESCRIPTION key.
		**/
		public MouseHandler(JLabel label)  {
			setLabel(label);
		}
		/**
		 * Sets long description of component.
		 * @param label the JLabel which will recieve value of the
		 *              Action.LONG_DESCRIPTION key.
		**/
		public void setLabel(JLabel label)  {
			this.label = label;
		}
		/**
		 * Display label when mouse has entered component.
		 * @param evt The event caused the mouseover action.
		**/
		public void mouseEntered(MouseEvent evt)  {
			if (evt.getSource() instanceof AbstractButton)  {
				AbstractButton button = (AbstractButton)evt.getSource();
				Action action = button.getAction(); // getAction is new in JDK 1.3
				if (action != null)  {
					String message = (String)action.getValue(Action.LONG_DESCRIPTION);
					label.setText(message);
				}
			}
		}
	}
	/**
	 * Enable/disable the domain importer menu options based upon the scale
	 * selection within the runspec.
	**/
	void checkImporterActions() {
		boolean isNonroad = Models.evaluateModels(runSpec.models) == Models.ModelCombination.M2;
		boolean isNationalDomain = !isNonroad && runSpec.domain == ModelDomain.NATIONAL_ALLOCATION;
		boolean isCountyDomain = !isNonroad && runSpec.domain == ModelDomain.SINGLE_COUNTY;
		boolean isProjectDomain = !isNonroad && runSpec.domain == ModelDomain.PROJECT;
		dataImporterAction.setEnabled(isNationalDomain);
		countyDataManagerAction.setEnabled(isCountyDomain);
		projectDomainManagerAction.setEnabled(isProjectDomain);
		nonroadDataImporterAction.setEnabled(isNonroad);
	}
	/**
	 * Sets the execute action menu item enabled if all RunSpecEditors don't have the NOT_READY
	 * state with extra logic for vehicle statuses since there only needs to be at least one of
	 * those.
	**/
	void checkExecuteAction() {
		if(stopAction.isEnabled()) {
			executeAction.setEnabled(false);
			return;
		}
		// Update the Action menu based on the total ready state
		executeAction.setEnabled(isRunSpecReady());
	}
	/**
	 * Checks the RunSpec's sections, returning true if all are ready to be used.
	 * @return true if the RunSpec is ready (or at least nothing is marked as not ready)
	**/
	boolean isRunSpecReady() {
		boolean allReady = true;
		// Walk the list of option statuses (excluding vehicle statuses)
		boolean onRoadReady = true;
		boolean offRoadReady = true;
		Iterator keyIterator = navigationPanel.optionStatuses.keySet().iterator();
		while(keyIterator.hasNext()) {
			String nextEditorName = keyIterator.next().toString();
			RunSpecSectionStatus iterStatus =
					(RunSpecSectionStatus)navigationPanel.optionStatuses.get(
					nextEditorName);
			if(iterStatus.status == RunSpecSectionStatus.NOT_READY) {
				if(nextEditorName.equalsIgnoreCase("onRoadVehicleEquipmentPanel")) {
					onRoadReady = false;
				} else if(nextEditorName.equalsIgnoreCase("offRoadVehicleEquipmentPanel")) {
					offRoadReady = false;
				} else {
					allReady = false;
					break;
				}
			}
		}
		return allReady && (onRoadReady || offRoadReady);
	}
	/**
	 * Logs the message to some medium based on the type.
	 * @param	type int value indicating the type of message.  The values should come from
	 * the static Logger message-type variables.
	 * @param	message the String to get logged.
	**/
	public void handleLog(LogMessageCategory type, String message) {
		// GUI only displays messages of severity WARNING and ERROR
		if (type == LogMessageCategory.WARNING){
			addLogMessage("WARNING:  " + message);
		} else if (type == LogMessageCategory.ERROR){
			addLogMessage("ERROR:  " + message);
		}
	}
	/** Handles timer events for logHandlerTimer. **/
	void handleLogHandlerTimer() {
		String message = getLogMessage();
		if(message != null) {
			JOptionPane.showMessageDialog(this, StringUtilities.wrapString(message, 60));
		}
//		JOptionPane.showMessageDialog(this, message, "Warning",
//				JOptionPane.WARNING_MESSAGE);
	}
	/**
	 * Gets and removes the first message from the message log queue.
	 * @return String the first message to retrieve.
	**/
	synchronized String getLogMessage() {
		if(logHandlerQueue.size() > 0) {
			return (String)logHandlerQueue.removeFirst();
		}
		return null;
	}
	/**
	 * Adds a message to the end of the message log queue.
	 * @param	newMessage the String to add.
	**/
	synchronized void addLogMessage(String newMessage) {
		logHandlerQueue.addLast(newMessage);
	}
	/** Handles the MOVESRunErrorLog menu action. **/
	void handleMOVESRunErrorLogAction() {
		if(!DatabaseConnectionManager.initialize(MOVESDatabaseType.OUTPUT)) {
			Logger.log(LogMessageCategory.ERROR,
				"An Output Database has not been specified for"
				+ " this RunSpec or the database has not been created, "
				+ "please specify one on the General Output Panel.");
			return;
		}
		MOVESRunErrorLog d = new MOVESRunErrorLog(this);
		// Simple offset from main window origin
		d.setLocation(getLocationOnScreen().x + 100, getLocationOnScreen().y + 100);
		d.showModal();
	}
	/** Handles the "Process DONE Files" menu action. **/
	synchronized void handlePDSpecGUIAction() {
		if(stopAction.isEnabled()) { // don't start if a simulation is running
			return;
		}
		// Commit the currently active (i.e. selected) RunSpec editor to the runSpec before
		// starting the MOVESEngine.
		navigationPanel.commitActiveEditor();
		PDSpecGUI gui = new PDSpecGUI(this);
		PDSpec pdSpec = gui.showModal();
		if(pdSpec == null) {
			return;
		}
		int answer = JOptionPane.showConfirmDialog(this,
				getDialogWording("Do you want to save the run specification before gathering DONE files?"),
				"Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			if(!handleSaveAction()) {
				return;
			}
		} else if(answer == JOptionPane.NO_OPTION) {
			// Nothing to do upon NO
		} else { // else if cancelled or merely closed
			return;
		}
		navigationPanel.clearSelection();
		progressPanel.allowTimeDialog = false;
		if(!MOVESAPI.getTheAPI().runPDSpecAsync(pdSpec)) {
			return;
		}
		executeAction.setEnabled(false);
		pdSpecGUIAction.setEnabled(false);
		stopAction.setEnabled(true);
		pauseAction.setEnabled(true);
		resumeAction.setEnabled(false);
		progressPanel.setProgressBarVisible(true);
		setProgressOnlyMode(true);
	}
	/**
	 * Invoked when a key has been typed. This event occurs when a key press is
	 * followed by a key release.
	 * @param e associated KeyEvent
	**/
	public void keyTyped(KeyEvent e) {
		// Nothing to do here
	}
	/**
	 * Invoked when a key has been pressed.
	 * @param e associated KeyEvent
	**/
	public void keyPressed(KeyEvent e) {
		// Nothing to do here
	}
	/**
	 * Invoked when a key has been released.
	 * @param e associated KeyEvent
	**/
	public void keyReleased(KeyEvent e) {
		// Nothing to do here
	}
}
