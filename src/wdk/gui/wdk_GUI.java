package wdk.gui;

import wdk.controller.FileController;
import wdk.file.DraftFileManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import wdk.data.DraftDataManager;
import static wdk.base.WDK_StartupConstants.CLOSE_BUTTON_LABEL;
import static wdk.base.WDK_StartupConstants.PATH_IMAGES;
import wdk.base.WDK_Properties;
import static wdk.base.WDK_StartupConstants.PATH_CSS;
import wdk.data.Draft;
import wdk.data.DraftDataView;
import wdk.file.JsonDraftFileManager;

/**
 *
 * @author Guacamole
 */
public class wdk_GUI implements DraftDataView {
    // STYLESHEET
    static final String PRIMARY_STYLE_SHEET = PATH_CSS + "wdk_style.css";
    static final String CLASS_BORDERED_PANE = "bordered_pane";
    static final String CLASS_SCREEN_PANE = "screen_pane";
    static final String CLASS_SUBJECT_PANE = "subject_pane";
    static final String CLASS_TOOLBAR_PANE = "toolbar_pane";
    static final String CLASS_HEADING_LABEL = "heading_label";
    static final String CLASS_SUBHEADING_LABEL = "subheading_label";
    static final String CLASS_PROMPT_LABEL = "prompt_label";
    static final String EMPTY_TEXT = "";
    static final int LARGE_TEXT_FIELD_LENGTH = 20;
    static final int SMALL_TEXT_FIELD_LENGTH = 5;
    
    
    // THIS MANAGES ALL OF THE APPLICATION'S DATA
    DraftDataManager dataManager;

    // THIS MANAGES COURSE FILE I/O
    DraftFileManager draftFileManager;

    // THIS MANAGES EXPORTING OUR SITE PAGES
    //DraftExporter siteExporter;  -- no longer necessary

    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    FileController fileController;

    // THIS HANDLES INTERACTIONS WITH COURSE INFO CONTROLS
    //DraftEditController draftController;
    JsonDraftFileManager jsonManager;
    
    // THIS IS THE APPLICATION WINDOW
    Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION GUI AND SCREENS
    BorderPane wdkPane;
    BorderPane screensPane;
    
    // INDIVIDUAL SCREENS
    PlayersScreen playersScreen;
    FantasyTeamsScreen fantasyTeamsScreen;
    StandingsScreen standingsScreen;
    DraftScreen draftScreen;
    MLBTeamsScreen mlbTeamsScreen;
    
    // THIS IS THE TOP TOOLBAR AND ITS CONTROLS
    FlowPane fileToolbarPane;
    Button newDraftButton;
    Button loadDraftButton;
    Button saveDraftButton;
    Button exportDraftButton;
    Button exitButton;
    
    // THIS IS THE BOTTOM TOOLBAR AND ITS CONTROLS
    FlowPane screensToolbarPane;
    Button playersScreenButton;
    Button teamsScreenButton;
    Button standingsScreenButton;
    Button draftScreenButton;
    Button mlbScreenButton;
    
    // HERE ARE OUR DIALOGS
    MessageDialog messageDialog;
    YesNoCancelDialog yesNoCancelDialog;

    // WE'LL ORGANIZE OUR WORKSPACE COMPONENTS USING A BORDER PANE
    Pane workspacePane;
    boolean workspaceActivated;
    
    public wdk_GUI(Stage initStage, JsonDraftFileManager j) {
        primaryStage = initStage;
        jsonManager = j;
    }
    
    public void initGUI(String appTitle) throws IOException{
        initDialogs();
        
        // INIT TOOLBARS
        initFileToolbar();
        initPagesToolbar();
        
        // INIT SCREENS
        initScreens();
        
        //INIT EVENTHANDLERS
        initEventHandlers();
        
        initWindow(appTitle);
    }
    
    
    //****************************************************************************************************************
    //                      METHODS TO HELP INITIALIZE THE GUI
    //****************************************************************************************************************
    
    // INITIALIZE THE WORKSPACE
    public void initWindow(String appTitle) {
        // SET WINDOW TITLE
        primaryStage.setTitle(appTitle);
        
        // GET THE SIZE OF THE SCREEN
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        // ADD THE TOOLBARS ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A NEW DRAFT
        wdkPane = new BorderPane();
        //wdkPane.getStyleClass().add("bordered_pane");
        screensPane = new BorderPane();
        screensPane.getStyleClass().add(CLASS_BORDERED_PANE);
        wdkPane.setTop(fileToolbarPane);
        wdkPane.setCenter(screensPane);
        //wdkPane.setBottom(screensToolbarPane);
        primaryScene = new Scene(wdkPane);

        // NOW TIE THE SCENE TO THE WINDOW, SELECT THE STYLESHEET
        // WE'LL USE TO STYLIZE OUR GUI CONTROLS, AND OPEN THE WINDOW
        primaryScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
    
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    public void initFileToolbar() {
        fileToolbarPane = new FlowPane();
        
        newDraftButton = initChildButton(fileToolbarPane, WDK_Properties.NEW_DRAFT_ICON, WDK_Properties.NEW_DRAFT_TOOLTIP, false);
        loadDraftButton = initChildButton(fileToolbarPane, WDK_Properties.LOAD_DRAFT_ICON, WDK_Properties.LOAD_DRAFT_TOOLTIP, false);
        saveDraftButton = initChildButton(fileToolbarPane, WDK_Properties.SAVE_DRAFT_ICON, WDK_Properties.SAVE_DRAFT_TOOLTIP, false);
        saveDraftButton.setDisable(true);
        exportDraftButton = initChildButton(fileToolbarPane, WDK_Properties.EXPORT_DRAFT_ICON, WDK_Properties.EXPORT_DRAFT_TOOLTIP, false);
        exportDraftButton.setDisable(true);
        exitButton = initChildButton(fileToolbarPane, WDK_Properties.EXIT_ICON, WDK_Properties.EXIT_TOOLTIP, false);
        
    }
    
    /**
     * This function initializes all the buttons in the toolbar at the bottom of
     * the application window.
     */
    public void initPagesToolbar() {
        screensToolbarPane = new FlowPane();
        
        playersScreenButton = initChildButton(screensToolbarPane, WDK_Properties.PLAYERS_SCREEN_ICON, WDK_Properties.PLAYERS_SCREEN_TOOLTIP, false);
        teamsScreenButton = initChildButton(screensToolbarPane, WDK_Properties.TEAMS_SCREEN_ICON, WDK_Properties.TEAMS_SCREEN_TOOLTIP, false);
        standingsScreenButton = initChildButton(screensToolbarPane, WDK_Properties.STANDINGS_SCREEN_ICON, WDK_Properties.STANDINGS_SCREEN_TOOLTIP, false);
        draftScreenButton = initChildButton(screensToolbarPane, WDK_Properties.DRAFT_SCREEN_ICON, WDK_Properties.DRAFT_SCREEN_TOOLTIP, false);
        mlbScreenButton = initChildButton(screensToolbarPane, WDK_Properties.PROTEAMS_SCREEN_ICON, WDK_Properties.MLB_SCREEN_TOOLTIP, false);
        
    }
    
    // INITIALIZES BUTTONS WITH IMAGES AND TOOLTIPS
    public Button initChildButton(Pane toolbar, WDK_Properties icon, WDK_Properties tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(icon.toString());
        Image buttonImage = new Image(imagePath);
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
        button.setTooltip(buttonTooltip);
        toolbar.getChildren().add(button);
        return button;
    }
    
    // INITIALIZES EVENT HANDLERS FOR THE GUI
    public void initEventHandlers() throws IOException {
        // FIRST THE FILE CONTROLS
        fileController = new FileController(messageDialog, yesNoCancelDialog, draftFileManager/*, siteExporter*/);
        newDraftButton.setOnAction(e -> {
            jsonManager = new JsonDraftFileManager();
            screensPane.setBottom(screensToolbarPane);
            screensPane.setCenter(fantasyTeamsScreen.getScreenPane());
            try {
                fileController.handleNewDraftRequest(this, jsonManager, playersScreen);
            } catch (IOException ex) {
                Logger.getLogger(wdk_GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            //dataManager.getDraft().calculateEstimatedValues();
        });
        
        loadDraftButton.setOnAction(e -> {
            fileController.handleLoadDraftRequest(this, jsonManager);
            this.updateGUI();
            
        });
        
        saveDraftButton.setOnAction(e -> {
            dataManager.setFileManager(draftFileManager);
            fileController.handleSaveDraftRequest(this, dataManager.getDraft());
        });
        
        exitButton.setOnAction(e -> {
            fileController.handleExitDraftRequest(this);
        });
        
        playersScreenButton.setOnAction(e -> {
            screensPane.setCenter(playersScreen.getScreenPane());
        });
        
        teamsScreenButton.setOnAction(e -> {
            //screensPane.setCenter(new VBox());
            screensPane.setCenter(fantasyTeamsScreen.getScreenPane());
        });
        
        standingsScreenButton.setOnAction(e -> {
            screensPane.setCenter(standingsScreen.getScreenPaneForShit());
        });
        
        draftScreenButton.setOnAction(e -> {
            screensPane.setCenter(draftScreen.getDraftScreenPane());
        });
        
        mlbScreenButton.setOnAction(e -> {
            mlbTeamsScreen.setDM(dataManager);
            screensPane.setCenter(mlbTeamsScreen.getScreenPane());
        });
    }
    
    private void initDialogs() {
        messageDialog = new MessageDialog(primaryStage, CLOSE_BUTTON_LABEL);
        yesNoCancelDialog = new YesNoCancelDialog(primaryStage);
    }
    
    private void initScreens() {
       playersScreen = new PlayersScreen(this, primaryStage, messageDialog, yesNoCancelDialog);
       fantasyTeamsScreen = new FantasyTeamsScreen(this, primaryStage, messageDialog, yesNoCancelDialog);
       standingsScreen = new StandingsScreen(this, primaryStage, messageDialog, yesNoCancelDialog);
       draftScreen = new DraftScreen(this, primaryStage);
       mlbTeamsScreen = new MLBTeamsScreen(this, primaryStage, messageDialog, yesNoCancelDialog);
    }
    
    public DraftDataManager getDataManager() {
        return dataManager;
    }
    
    public void setDataManager(DraftDataManager initDataManager) {
        dataManager = initDataManager;
    }
    
    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Course has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveDraftButton.setDisable(saved);

        // ALL THE OTHER BUTTONS ARE ALWAYS ENABLED
        // ONCE EDITING THAT FIRST COURSE BEGINS
        loadDraftButton.setDisable(false);
        //exportDraftButton.setDisable(false);

        // NOTE THAT THE NEW, LOAD, AND EXIT BUTTONS
        // ARE NEVER DISABLED SO WE NEVER HAVE TO TOUCH THEM
    }

    @Override
    public void reloadDraft(Draft draftToReload) {
        // FIRST ACTIVATE THE WORKSPACE IF NECESSARY
        if (!workspaceActivated) {
            activateWorkspace();
        }

        // WE DON'T WANT TO RESPOND TO EVENTS FORCED BY
        // OUR INITIALIZATION SELECTIONS
        //courseEditController.enable(false);

        // FIRST LOAD ALL THE BASIC COURSE INFO
        playersScreen.update(dataManager);
        fantasyTeamsScreen.update(dataManager);


        // NOW WE DO WANT TO RESPOND WHEN THE USER INTERACTS WITH OUR CONTROLS
        //courseController.enable(true);
    }

    /**
     * Mutator method for the course file manager.
     *
     * @param initCourseFileManager The CourseFileManager to be used by this UI.
     */
    public void setDraftFileManager(DraftFileManager initCourseFileManager) {
        draftFileManager = initCourseFileManager;
    }
    
    /**
     * Accessor method for the window (i.e. stage).
     *
     * @return The window (i.e. Stage) used by this UI.
     */
    public Stage getWindow() {
        return primaryStage;
    }

    public PlayersScreen getPlayersScreen() {
        return playersScreen;
    }

    public void setPlayersScreen(PlayersScreen playersScreen) {
        this.playersScreen = playersScreen;
    }

    public FantasyTeamsScreen getFantasyTeamsScreen() {
        return fantasyTeamsScreen;
    }

    public void setFantasyTeamsScreen(FantasyTeamsScreen fantasyTeamsScreen) {
        this.fantasyTeamsScreen = fantasyTeamsScreen;
    }

    public StandingsScreen getStandingsScreen() {
        return standingsScreen;
    }

    public void setStandingsScreen(StandingsScreen standingsScreen) {
        this.standingsScreen = standingsScreen;
    }

    public DraftScreen getDraftScreen() {
        return draftScreen;
    }

    public void setDraftScreen(DraftScreen draftScreen) {
        this.draftScreen = draftScreen;
    }

    public MLBTeamsScreen getMlbTeamsScreen() {
        return mlbTeamsScreen;
    }

    public void setMlbTeamsScreen(MLBTeamsScreen mlbTeamsScreen) {
        this.mlbTeamsScreen = mlbTeamsScreen;
    }
    
    public void activateWorkspace() {
        if (!workspaceActivated) {
            // PUT THE WORKSPACE IN THE GUI
            wdkPane.setCenter(screensPane);
            workspaceActivated = true;
        }
    }
    
    public void updateGUI() {
        playersScreen.update(this.dataManager);
        fantasyTeamsScreen.update(this.dataManager);
        mlbTeamsScreen.update(this.dataManager);
        
        // just fix these for loading
        standingsScreen.update(this.dataManager);
        draftScreen.update(this.dataManager);
    }

    public void updateScreenPane() {
        screensPane.setBottom(screensToolbarPane);
        screensPane.setCenter(fantasyTeamsScreen.getScreenPane());
    }
}
