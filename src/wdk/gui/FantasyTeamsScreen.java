/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_Properties.ADD_ICON;
import static wdk.base.WDK_Properties.ADD_TEAM_TOOLTIP;
import static wdk.base.WDK_Properties.EDIT_TEAM_ICON;
import static wdk.base.WDK_Properties.EDIT_TEAM_TOOLTIP;
import static wdk.base.WDK_Properties.MINUS_ICON;
import static wdk.base.WDK_Properties.REMOVE_TEAM_TOOLTIP;
import static wdk.base.WDK_StartupConstants.PATH_IMAGES;
import wdk.controller.PlayerEditController;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import wdk.data.Team;
import static wdk.gui.wdk_GUI.CLASS_BORDERED_PANE;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_SCREEN_PANE;
import static wdk.gui.wdk_GUI.CLASS_SUBHEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_TOOLBAR_PANE;

/**
 *
 * @author Guacamole
 */
public class FantasyTeamsScreen extends Stage {
    Stage primaryStage;
    // DATA TO LOAD INTO THE SCREEN
    Team currentTeam;
    wdk_GUI gui;
    DraftDataManager dataManager;
    
    // PANE TO HOLD CONTROLS AND LABEL FOR THE HEADER
    VBox screenPane;
    HBox nameToolbar;
    HBox controlsToolbar;
    HBox toolbars;
    Label screenHeaderLabel;
    
    MessageDialog messageDialog;
    YesNoCancelDialog yncDialog;
    
    // CONTROLS FOR THE SCREEN
    Label draftNameLabel;
    TextField draftNameTextField;
    Button addTeamButton;
    Button removeTeamButton;
    Button editTeamButton;
    Label selectTeamLabel;
    ComboBox teamsComboBox;
    
    SplitPane tablesPane;
    ScrollPane tablesScrollPane;
    VBox startingLineupBox;
    Label startingLineupLabel;
    VBox taxiBox;
    Label taxiSquadLabel;
    
    // TABLE FOR THE MAIN ROSTER
    TableView rosterTable;
    TableColumn positionInTeamColumn;
    TableColumn firstNameColumn;
    TableColumn lastNameColumn;
    TableColumn proTeamColumn;
    TableColumn positionsColumn;
    TableColumn runsWinsColumn;
    TableColumn hrSaveColumn;
    TableColumn rbiKColumn;
    TableColumn sbERAColumn;
    TableColumn baWHIPColumn;
    TableColumn estValueColumn;
    TableColumn contractColumn;
    TableColumn salaryColumn;
    
    // TABLE FOR THE TAXI ROSTER
    TableView taxiTable;
    TableView rosterTableT;
    TableColumn positionInTeamColumnT;
    TableColumn firstNameColumnT;
    TableColumn lastNameColumnT;
    TableColumn proTeamColumnT;
    TableColumn positionsColumnT;
    TableColumn runsWinsColumnT;
    TableColumn hrSaveColumnT;
    TableColumn rbiKColumnT;
    TableColumn sbERAColumnT;
    TableColumn baWHIPColumnT;
    TableColumn estValueColumnT;
    TableColumn contractColumnT;
    TableColumn salaryColumnT;
    
    public FantasyTeamsScreen(wdk_GUI initGUI, Stage initPrimaryStage, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        primaryStage = initPrimaryStage;
        gui = initGUI;
        dataManager = initGUI.getDataManager();
        messageDialog = initMessageDialog;
        yncDialog = initYesNoCancelDialog;
        
        nameToolbar = new HBox();
        draftNameLabel = new Label("Draft Name:  ");
        draftNameTextField = new TextField();
        nameToolbar.getChildren().add(draftNameLabel);
        nameToolbar.getChildren().add(draftNameTextField);
        
        controlsToolbar = new HBox();
        initButtons();
        selectTeamLabel = new Label("Select Fantasy Team:  ");
        teamsComboBox = new ComboBox();
        teamsComboBox.setItems(dataManager.getDraft().getTeamsAsStrings());
        controlsToolbar.getChildren().add(addTeamButton);
        controlsToolbar.getChildren().add(removeTeamButton);
        controlsToolbar.getChildren().add(editTeamButton);
        controlsToolbar.getChildren().add(selectTeamLabel);
        controlsToolbar.getChildren().add(teamsComboBox);
        
        startingLineupBox = new VBox();
        startingLineupBox.getStyleClass().add(CLASS_BORDERED_PANE);
        startingLineupLabel = new Label("Starting Lineup");
        startingLineupLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        taxiBox = new VBox();
        taxiBox.getStyleClass().add(CLASS_BORDERED_PANE);
        taxiSquadLabel = new Label("Taxi Squad");
        taxiSquadLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        initTables();
        startingLineupBox.getChildren().add(startingLineupLabel);
        startingLineupBox.getChildren().add(rosterTable);
        taxiBox.getChildren().add(taxiSquadLabel);
        taxiBox.getChildren().add(taxiTable);
        
        tablesPane = new SplitPane();
        tablesPane.setOrientation(Orientation.VERTICAL);
        tablesPane.getItems().add(startingLineupBox);
        tablesPane.getItems().add(taxiBox);
        tablesScrollPane = new ScrollPane();
        tablesScrollPane.setContent(tablesPane);
        tablesScrollPane.setFitToWidth(true);
        
        
        
        screenHeaderLabel = new Label("Fantasy Teams"); //TEAMS_HEADING_LABEL = Fantasy Teams
        screenHeaderLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        screenPane = new VBox();
        screenPane.getStyleClass().add(CLASS_SCREEN_PANE);
        screenPane.setPadding(new Insets(30, 20, 20, 20));
        
        toolbars = new HBox();
        toolbars.getStyleClass().add(CLASS_TOOLBAR_PANE);
        toolbars.getChildren().add(nameToolbar);
        toolbars.getChildren().add(controlsToolbar);
        
        // ADD EVERYTHING TO THE SCREENPANE
        screenPane.getChildren().add(screenHeaderLabel);
        screenPane.getChildren().add(toolbars);
        //screenPane.getChildren().add(nameToolbar);
        //screenPane.getChildren().add(controlsToolbar);
        screenPane.getChildren().add(tablesScrollPane);
        initEventHandlers();
        
    }
    
    
    private void initEventHandlers() {
        // FIRST THE NAME TEXTFIELD
        draftNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            dataManager.getDraft().setName(newValue);
        });
        
        PlayerEditController pec = new PlayerEditController(gui, primaryStage, dataManager, messageDialog, yncDialog);
        // THEN THE ADD/REMOVE/EDIT TEAM BUTTONS
        addTeamButton.setOnAction(e -> {
            pec.handleAddTeamRequest(gui, currentTeam);
            //updateStandings(currentTeam);
            removeTeamButton.setDisable(false);
            editTeamButton.setDisable(false);
            
            teamsComboBox.setItems(dataManager.getDraft().getTeamsAsStrings());
            teamsComboBox.getSelectionModel().select(teamsComboBox.getItems().size()-1);
            rosterTable.setItems(dataManager.getDraft().getTeamByName(
                    (String) teamsComboBox.getSelectionModel().getSelectedItem()).getRoster());
            taxiTable.setItems(dataManager.getDraft().getTeamByName(
                    (String) teamsComboBox.getSelectionModel().getSelectedItem()).getTaxiSquad());
        });
        
        removeTeamButton.setOnAction(e -> {
            //currentTeam = (Team)teamsComboBox.getSelectionModel().getSelectedItem();
            pec.handleRemoveTeamRequest(gui, currentTeam, rosterTable.getItems(), taxiTable.getItems());
            if (dataManager.getDraft().getFantasyTeams().isEmpty())
                clearTeamsScreen();
            else {
                currentTeam = dataManager.getDraft().getFantasyTeams().get(0);
                updateGUI(currentTeam);
            }
        });
        
        editTeamButton.setOnAction(e -> {
            //currentTeam = (Team)teamsComboBox.getSelectionModel().getSelectedItem();
            pec.handleEditTeamRequest(gui, currentTeam);
        });
    
        
        // THEN THE FANTASY TEAM COMBO BOX
        teamsComboBox.setOnAction(e -> {
           if (teamsComboBox.getItems().isEmpty() == false) {
                try{
                    currentTeam = dataManager.getDraft().getFantasyTeams().get(teamsComboBox.getSelectionModel().getSelectedIndex());
                } catch (ArrayIndexOutOfBoundsException ae) {
                    currentTeam = dataManager.getDraft().getFantasyTeams().get(0);
                }
                updateGUI(currentTeam);
           }
        });
        
        // THEN THE STARTING LINEUP TABLE (Player Editing)
        // AND NOW THE ROSTER TABLE
        rosterTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // OPEN UP THE SCHEDULE ITEM EDITOR
                Player playerToEdit = (Player) rosterTable.getSelectionModel().getSelectedItem();
                pec.handleEditPlayerRequest(gui, playerToEdit);
                //dataManager.getDraft().
            }
        });
        
        // AND FINALLY THE TAXI TABLE 
        taxiTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // OPEN UP THE SCHEDULE ITEM EDITOR
                Player playerToEdit = (Player) taxiTable.getSelectionModel().getSelectedItem();
                pec.handleEditPlayerRequest(gui, playerToEdit);
                //dataManager.getDraft().
            }
        });
    }
    
    public void updateStandings(Team t) {
        gui.getStandingsScreen().getStandingsTable().getItems().add(t);
    }
    
    private void clearTeamsScreen() {
        teamsComboBox.getItems().clear();
        rosterTable.getItems().clear();
        taxiTable.getItems().clear();
        currentTeam = null;
        removeTeamButton.setDisable(true);
        editTeamButton.setDisable(true);
    }
    
    private void updateGUI(Team current) {
        teamsComboBox.setItems(dataManager.getDraft().getTeamsAsStrings());
        teamsComboBox.setValue(teamsComboBox.getItems().get(teamsComboBox.getItems().indexOf(currentTeam.getName())));
        rosterTable.setItems(current.getRoster());
        taxiTable.setItems(current.getTaxiSquad());
    }
    
    private void initTables() {
        // INITIALIZE THE TABLE ITSELF
        rosterTable = new TableView();
        
        // ROSTER TABLE FIRST
        //INITIALIZE EACH COLUMN WITH APPROPRIATE LABELS
        positionInTeamColumn = new TableColumn("Position");
        firstNameColumn = new TableColumn("First Name");
        lastNameColumn = new TableColumn("Last Name");
        proTeamColumn = new TableColumn("Pro Team");
        positionsColumn = new TableColumn("Positions");
        runsWinsColumn = new TableColumn("R/W");
        hrSaveColumn = new TableColumn("HR/SV");
        rbiKColumn = new TableColumn("RBI/K");
        sbERAColumn = new TableColumn("SB/ERA");
        baWHIPColumn = new TableColumn("BA/WHIP");
        estValueColumn = new TableColumn("Estimated Value");
        contractColumn = new TableColumn("Contract");
        salaryColumn = new TableColumn("Salary");
        
        // LINK EACH COLUMN TO THE DATA IT WILL SHOW
        positionInTeamColumn.setCellValueFactory(new PropertyValueFactory<String, String>("positionInTeam"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("lastName"));
        proTeamColumn.setCellValueFactory(new PropertyValueFactory<String, String>("proTeam"));
        positionsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("positions"));
        runsWinsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("runsWins"));
        hrSaveColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("hrSave"));
        rbiKColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("rbiK"));
        sbERAColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("sbERA"));
        baWHIPColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("baWHIP"));
        estValueColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("estimatedValue"));
        contractColumn.setCellValueFactory(new PropertyValueFactory<String, String>("contract"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("salary"));
        
        
        // ADD EVERYTHING TO THE TABLE
        rosterTable.getColumns().add(positionInTeamColumn);
        rosterTable.getColumns().add(firstNameColumn);
        rosterTable.getColumns().add(lastNameColumn);
        rosterTable.getColumns().add(proTeamColumn);
        rosterTable.getColumns().add(positionsColumn);
        rosterTable.getColumns().add(runsWinsColumn);
        rosterTable.getColumns().add(hrSaveColumn);
        rosterTable.getColumns().add(rbiKColumn);
        rosterTable.getColumns().add(sbERAColumn);
        rosterTable.getColumns().add(baWHIPColumn);
        rosterTable.getColumns().add(estValueColumn);
        rosterTable.getColumns().add(contractColumn);
        rosterTable.getColumns().add(salaryColumn);
        
        // THEN THE TAXI TABLE
        taxiTable = new TableView();
        
        // INITIALIZE THE COLUMNS
        positionInTeamColumnT = new TableColumn("Position");
        firstNameColumnT = new TableColumn("First Name");
        lastNameColumnT = new TableColumn("Last Name");
        proTeamColumnT = new TableColumn("Pro Team");
        positionsColumnT = new TableColumn("Positions");
        runsWinsColumnT = new TableColumn("R/W");
        hrSaveColumnT = new TableColumn("HR/SV");
        rbiKColumnT = new TableColumn("RBI/K");
        sbERAColumnT = new TableColumn("SB/ERA");
        baWHIPColumnT = new TableColumn("BA/WHIP");
        estValueColumnT = new TableColumn("Estimated Value");
        contractColumnT = new TableColumn("Contract");
        salaryColumnT = new TableColumn("Salary");
        
        // LINK EACH COLUMN TO THE DATA IT WILL SHOW
        positionInTeamColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("positionInTeam"));
        firstNameColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("firstName"));
        lastNameColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("lastName"));
        proTeamColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("proTeam"));
        positionsColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("positions"));
        runsWinsColumnT.setCellValueFactory(new PropertyValueFactory<Integer, String>("runsWins"));
        hrSaveColumnT.setCellValueFactory(new PropertyValueFactory<Integer, String>("hrSave"));
        rbiKColumnT.setCellValueFactory(new PropertyValueFactory<Integer, String>("rbiK"));
        sbERAColumnT.setCellValueFactory(new PropertyValueFactory<Double, String>("sbERA"));
        baWHIPColumnT.setCellValueFactory(new PropertyValueFactory<Double, String>("baWHIP"));
        estValueColumnT.setCellValueFactory(new PropertyValueFactory<Integer, String>("estimatedValue"));
        contractColumnT.setCellValueFactory(new PropertyValueFactory<String, String>("contract"));
        salaryColumnT.setCellValueFactory(new PropertyValueFactory<Integer, String>("salary"));
        
        // ADD THEM ALL TO THE TABLE
        taxiTable.getColumns().add(positionInTeamColumnT);
        taxiTable.getColumns().add(firstNameColumnT);
        taxiTable.getColumns().add(lastNameColumnT);
        taxiTable.getColumns().add(proTeamColumnT);
        taxiTable.getColumns().add(positionsColumnT);
        taxiTable.getColumns().add(runsWinsColumnT);
        taxiTable.getColumns().add(hrSaveColumnT);
        taxiTable.getColumns().add(rbiKColumnT);
        taxiTable.getColumns().add(sbERAColumnT);
        taxiTable.getColumns().add(baWHIPColumnT);
        taxiTable.getColumns().add(estValueColumnT);
        taxiTable.getColumns().add(contractColumnT);
        taxiTable.getColumns().add(salaryColumnT);
    }
    
    private void initButtons() {
        addTeamButton = new Button();
        removeTeamButton = new Button();
        removeTeamButton.setDisable(true);
        editTeamButton = new Button();
        editTeamButton.setDisable(true);
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(ADD_ICON.toString());
        Image buttonImage = new Image(imagePath);
        Tooltip buttonTooltip = new Tooltip(props.getProperty(ADD_TEAM_TOOLTIP.toString()));
        addTeamButton.setGraphic(new ImageView(buttonImage));
        addTeamButton.setTooltip(buttonTooltip);
        
        imagePath = "file:" + PATH_IMAGES + props.getProperty(MINUS_ICON.toString());
        buttonImage = new Image(imagePath);
        buttonTooltip = new Tooltip(props.getProperty(REMOVE_TEAM_TOOLTIP.toString()));
        removeTeamButton.setGraphic(new ImageView(buttonImage));
        removeTeamButton.setTooltip(buttonTooltip);
        
        imagePath = "file:" + PATH_IMAGES + props.getProperty(EDIT_TEAM_ICON.toString());
        buttonImage = new Image(imagePath);
        buttonTooltip = new Tooltip(props.getProperty(EDIT_TEAM_TOOLTIP.toString()));
        editTeamButton.setGraphic(new ImageView(buttonImage));
        editTeamButton.setTooltip(buttonTooltip);
    }
    
    public VBox getScreenPane() {
        return screenPane;
    }    
    
    public void update(DraftDataManager dm) {
        dataManager = dm;
        Draft d = dataManager.getDraft();
        draftNameTextField.setText(d.getName());
        try {
        currentTeam = d.getFantasyTeams().get(0);
        rosterTable.setItems(currentTeam.getRoster());
        taxiTable.setItems(currentTeam.getTaxiSquad());
        teamsComboBox.setItems(d.getTeamsAsStrings());
        teamsComboBox.setValue(teamsComboBox.getItems().get(0));
        removeTeamButton.setDisable(false);
        editTeamButton.setDisable(false);
        } catch (Exception e) {
            currentTeam = null;
            rosterTable.setItems(null);
            taxiTable.setItems(null);
            teamsComboBox.setValue(null);
            teamsComboBox.setItems(null);
            removeTeamButton.setDisable(true);
            editTeamButton.setDisable(true);
        }
        
    }

    public void clearAll() {
        draftNameTextField.clear();
        
        try{
        rosterTable.getItems().clear();
        } catch(Exception e) {
            System.out.println("rosterTable has no items");
        }
        
        try{
            teamsComboBox.getItems().clear(); 
        } catch(Exception ex){
            System.out.println("teamsComboBox has no items");
        }
        
    }
}
