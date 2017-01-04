/*
 * This class contains methods to deal with requests to access/alter
 * files associated with Draft objects in this application.
 */
package wdk.controller;

import java.io.File;
import wdk.file.DraftFileManager;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_Properties.DRAFT_SAVED_MESSAGE;
import wdk.data.DraftDataManager;
import wdk.error.ErrorHandler;
import wdk.gui.MessageDialog;
import wdk.gui.YesNoCancelDialog;
import wdk.gui.wdk_GUI;
import static wdk.base.WDK_Properties.NEW_DRAFT_CREATED_MESSAGE;
import static wdk.base.WDK_Properties.SAVE_UNSAVED_WORK_MESSAGE;
import static wdk.base.WDK_StartupConstants.JSON_FILE_PATH_HITTERS;
import static wdk.base.WDK_StartupConstants.JSON_FILE_PATH_PITCHERS;
import static wdk.base.WDK_StartupConstants.PATH_DRAFTS;
import wdk.data.Draft;
import wdk.data.Hitter;
import wdk.data.Pitcher;
import wdk.data.Player;
import wdk.file.JsonDraftFileManager;
import wdk.gui.PlayersScreen;

/**
 *
 * @author Guacamole
 */
public class FileController {
    
    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    private boolean saved;

    // THIS GUY KNOWS HOW TO READ AND WRITE Draft DATA
    private DraftFileManager draftIO;

    // THIS GUY KNOWS HOW TO EXPORT COURSE SCHEDULE PAGES
    //private CourseSiteExporter exporter;

    // THIS WILL PROVIDE FEEDBACK TO THE USER WHEN SOMETHING GOES WRONG
    ErrorHandler errorHandler;
    
    // THIS WILL PROVIDE FEEDBACK TO THE USER AFTER
    // WORK BY THIS CLASS HAS COMPLETED
    MessageDialog messageDialog;
    
    // AND WE'LL USE THIS TO ASK YES/NO/CANCEL QUESTIONS
    YesNoCancelDialog yesNoCancelDialog;
    
    // WE'LL USE THIS TO GET OUR VERIFICATION FEEDBACK
    PropertiesManager properties;
    
    public FileController(MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog,
            DraftFileManager initDraftIO/*, DraftExporter initExporter*/) {
        
        // NOTHING YET
        saved = true;
        
        // KEEP THESE GUYS FOR LATER
        draftIO = initDraftIO;
        //exporter = initExporter;
        
        // BE READY FOR ERRORS
        errorHandler = ErrorHandler.getErrorHandler();
        
        // AND GET READY TO PROVIDE FEEDBACK
        messageDialog = initMessageDialog;
        yesNoCancelDialog = initYesNoCancelDialog;
        properties = PropertiesManager.getPropertiesManager();
    }
    
    public void handleNewDraftRequest(wdk_GUI gui, JsonDraftFileManager j, PlayersScreen screen) throws IOException {
        ObservableList<Hitter> hitters = j.loadHitters(JSON_FILE_PATH_HITTERS);
        ObservableList<Pitcher> pitchers = j.loadPitchers(JSON_FILE_PATH_PITCHERS);
        ObservableList<Player> players = FXCollections.observableArrayList();
        ObservableList<String> proTeams = FXCollections.observableArrayList();
        initProTeams(proTeams);
        players.addAll(hitters);
        players.addAll(pitchers);
        
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                // RESET THE DATA, WHICH SHOULD TRIGGER A RESET OF THE UI
                screen.addTableData(players);
                gui.getDataManager().getDraft().setHitters(hitters);  //DataManager for the gui is null.
                gui.getDataManager().getDraft().setPitchers(pitchers);
                gui.getDataManager().getDraft().setAvailablePlayers(players);
                gui.getPlayersScreen().clearSearchText();
                gui.getFantasyTeamsScreen().clearAll();
                
                
                DraftDataManager dataManager = gui.getDataManager();
                dataManager.reset();
                dataManager.getDraft().setHitters(hitters);
                dataManager.getDraft().setPitchers(pitchers);
                dataManager.getDraft().setAvailablePlayers(players);
                saved = false;

                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                // THE APPROPRIATE CONTROLS
                gui.updateToolbarControls(saved);

                // TELL THE USER THE COURSE HAS BEEN CREATED
                messageDialog.show(properties.getProperty(NEW_DRAFT_CREATED_MESSAGE));
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG, PROVIDE FEEDBACK
            errorHandler.handleNewDraftError();
        }
    }
    
    public void handleSaveDraftRequest(wdk_GUI gui, Draft draftToSave) {
        try {
            // SAVE IT TO A FILE
            draftIO.saveDraft(draftToSave);

            // MARK IT AS SAVED
            saved = true;

            // TELL THE USER THE FILE HAS BEEN SAVED
            messageDialog.show(properties.getProperty(DRAFT_SAVED_MESSAGE));

            // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
            // THE APPROPRIATE CONTROLS
            gui.updateToolbarControls(saved);
        } catch (IOException ioe) {
            errorHandler.handleSaveDraftError();
        }
    }
    
    public void handleLoadDraftRequest(wdk_GUI gui, JsonDraftFileManager j) {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen(gui, j);
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG
            errorHandler.handleLoadDraftError();
        }
    }
    
    public void handleExitDraftRequest(wdk_GUI gui) {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ErrorHandler.getErrorHandler();
            eH.handleExitError();
        }
    }
    
    private boolean promptToSave(wdk_GUI gui) throws IOException {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yesNoCancelDialog.show(properties.getProperty(SAVE_UNSAVED_WORK_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoCancelDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) {
            // SAVE THE COURSE
            DraftDataManager dataManager = gui.getDataManager();
            draftIO.saveDraft(dataManager.getDraft());
            saved = true;
            
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if (selection.equals(YesNoCancelDialog.CANCEL)) {
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }
    
    private void initProTeams(ObservableList list) {
        list.add("AZ");
        list.add("ATL");
        list.add("CHI");
        list.add("CIN");
        list.add("COL");
        list.add("LAD");
        list.add("MIA");
        list.add("MIL");
        list.add("NYM");
        list.add("PHI");
        list.add("PIT");
        list.add("SD");
        list.add("SF");
        list.add("STL");
        list.add("WAS");
    }
    
    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen(wdk_GUI gui, JsonDraftFileManager j) {
        // AND NOW ASK THE USER FOR THE COURSE TO OPEN
        FileChooser draftFileChooser = new FileChooser();
        draftFileChooser.setInitialDirectory(new File(PATH_DRAFTS));
        File selectedFile = draftFileChooser.showOpenDialog(gui.getWindow());
        

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                Draft draftToLoad = gui.getDataManager().getDraft();
                gui.getPlayersScreen().addTableData(draftToLoad.getAvailablePlayers());
                draftIO.loadDraft(draftToLoad, selectedFile.getAbsolutePath());
                gui.reloadDraft(draftToLoad);
                saved = true;
                gui.updateToolbarControls(saved);
                gui.updateScreenPane();
            } catch (Exception e) {
                ErrorHandler eH = ErrorHandler.getErrorHandler();
                eH.handleLoadDraftError();
            }
        }
    }
}
