/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import java.util.Stack;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_Properties.ADD_ICON;
import static wdk.base.WDK_Properties.MINUS_ICON;
import static wdk.base.WDK_Properties.ADD_PLAYER_TOOLTIP;
import static wdk.base.WDK_Properties.REMOVE_PLAYER_TOOLTIP;
import static wdk.base.WDK_StartupConstants.PATH_IMAGES;
import wdk.controller.PlayerEditController;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_SCREEN_PANE;
import static wdk.gui.wdk_GUI.CLASS_SUBHEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_TOOLBAR_PANE;
import static wdk.gui.wdk_GUI.LARGE_TEXT_FIELD_LENGTH;

/**
 * This class contains everything necessary for creating and editing a players screen.
 * @author Guacamole
 */
public class PlayersScreen {
    Stage primaryStage;
    // DATA TO LOAD INTO THE SCREEN
    //ObservableList<Player> availablePlayers;
    String textFieldContent;
    Stack<ObservableList<Player>> itemStack;
    wdk_GUI gui;
    DraftDataManager dataManager;
    
    // PANE TO HOLD CONTROLS AND LABEL FOR THE HEADER
    VBox screenPane;
    Label screenHeaderLabel;
    
    MessageDialog messageDialog;
    YesNoCancelDialog yncDialog;
    
    // CONTROLS FOR THE SCREEN
    
    // TABLE FIRST
    TableView playersTable;
    TableColumn firstNameColumn;
    TableColumn lastNameColumn;
    TableColumn proTeamColumn;
    TableColumn positionsColumn;
    TableColumn yearOfBirthColumn;
    TableColumn runsWinsColumn;
    TableColumn homerunsSavesColumn;
    TableColumn rbiStrikeoutColumn;
    TableColumn stolenBasesERAColumn;
    TableColumn battingAvgWhipColumn;
    TableColumn estimatedValueColumn;
    TableColumn notesColumn;
    
    // AND THE RADIO BUTTONS
    ToggleGroup rbGroup;
    RadioButton allRB;
    RadioButton cRB;
    RadioButton fbRB;
    RadioButton ciRB; // 1B CI 3B 2B MI SS OF U P
    RadioButton tbRB;
    RadioButton sbRB;
    RadioButton miRB;
    RadioButton ssRB;
    RadioButton ofRB;
    RadioButton uRB;
    RadioButton pRB;
    
    // THEN EVERYTHING ELSE
    Label searchLabel;
    TextField searchTextField;
    Button addPlayerButton;
    Button removePlayerButton;
    HBox controlsToolbar;
    FlowPane radioButtonToolbar;
    
    
    public PlayersScreen(wdk_GUI initGUI, Stage primaryStageA, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        primaryStage = primaryStageA;
        messageDialog = initMessageDialog;
        yncDialog = initYesNoCancelDialog;
        gui  = initGUI;
        dataManager = gui.getDataManager(); 
        //availablePlayers = dataManager.getDraft().getAvailablePlayers();
        
        screenHeaderLabel = new Label("Available Players");
        screenHeaderLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        // INITIALIZE CONTROLS
        controlsToolbar = new HBox();
        controlsToolbar.getStyleClass().add(CLASS_TOOLBAR_PANE);
        controlsToolbar.setPadding(new Insets(10, 10, 10, 0));
        initButtons();
        searchLabel = new Label("Search: ");
        searchLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        searchTextField = new TextField();
        searchTextField.setPrefColumnCount(LARGE_TEXT_FIELD_LENGTH);
        
        controlsToolbar.getChildren().add(addPlayerButton);
        controlsToolbar.getChildren().add(removePlayerButton);
        controlsToolbar.getChildren().add(searchLabel);
        controlsToolbar.getChildren().add(searchTextField);
        initTable();
        
        radioButtonToolbar = new FlowPane();
        radioButtonToolbar.getStyleClass().add(CLASS_TOOLBAR_PANE);
        //radioButtonToolbar.setPadding(new Insets(10, 10, 10, 0));
        initRadioButtons();
        
        screenPane = new VBox();
        screenPane.getStyleClass().add(CLASS_SCREEN_PANE);
        screenPane.getChildren().add(screenHeaderLabel);
        screenPane.getChildren().add(controlsToolbar);
        screenPane.getChildren().add(radioButtonToolbar);
        screenPane.getChildren().add(playersTable);
        screenPane.setPadding(new Insets(30, 20, 20, 20));
        initEventHandlers();
    }

    public VBox getScreenPane() {
        return screenPane;
    }
    
    // INITIALIZES THE TABLE AND ALL OF ITS COLUMNS
    private void initTable() {
        // INITIALIZE THE TABLE ITSELF
        playersTable = new TableView();
        
        //INITIALIZE EACH COLUMN WITH APPROPRIATE LABELS
        firstNameColumn = new TableColumn("First Name");
        lastNameColumn = new TableColumn("Last Name");
        proTeamColumn = new TableColumn("Pro Team");
        positionsColumn = new TableColumn("Positions");
        yearOfBirthColumn = new TableColumn("Year of Birth");
        runsWinsColumn = new TableColumn("R/W");
        homerunsSavesColumn = new TableColumn("HR/SV");
        rbiStrikeoutColumn = new TableColumn("RBI/K");
        stolenBasesERAColumn = new TableColumn("SB/ERA");
        battingAvgWhipColumn = new TableColumn("BA/WHIP");
        estimatedValueColumn = new TableColumn("Estimated Value");
        notesColumn = new TableColumn("Notes");
        
        // LINK EACH COLUMN TO THE DATA IT WILL SHOW
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("lastName"));
        proTeamColumn.setCellValueFactory(new PropertyValueFactory<String, String>("proTeam"));
        yearOfBirthColumn.setCellValueFactory(new PropertyValueFactory<String, String>("birthYear"));
        positionsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("positions"));
        runsWinsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("runsWins"));
        homerunsSavesColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("hrSave"));
        rbiStrikeoutColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("rbiK"));
        stolenBasesERAColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("sbERA"));
        battingAvgWhipColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("baWHIP"));
        estimatedValueColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("estimatedValue"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("notes"));
        
        positionsColumn.sortableProperty().set(false);
        
        // ADD EVERYTHING TO THE TABLE
        playersTable.getColumns().add(firstNameColumn);
        playersTable.getColumns().add(lastNameColumn);
        playersTable.getColumns().add(proTeamColumn);
        playersTable.getColumns().add(positionsColumn);
        playersTable.getColumns().add(yearOfBirthColumn);
        playersTable.getColumns().add(runsWinsColumn);
        playersTable.getColumns().add(homerunsSavesColumn);
        playersTable.getColumns().add(rbiStrikeoutColumn);
        playersTable.getColumns().add(stolenBasesERAColumn);
        playersTable.getColumns().add(battingAvgWhipColumn);
        playersTable.getColumns().add(estimatedValueColumn);
        playersTable.getColumns().add(notesColumn);
        playersTable.setEditable(true);
    }
    
    // INITIALIZES ADD AND REMOVE PLAYER BUTTONS WITH IMAGES AND TOOLTIPS
    private void initButtons() {
        addPlayerButton = new Button();
        removePlayerButton = new Button();
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(ADD_ICON.toString());
        Image buttonImage = new Image(imagePath);
        Tooltip buttonTooltip = new Tooltip(props.getProperty(ADD_PLAYER_TOOLTIP.toString()));
        addPlayerButton.setGraphic(new ImageView(buttonImage));
        addPlayerButton.setTooltip(buttonTooltip);
        
        imagePath = "file:" + PATH_IMAGES + props.getProperty(MINUS_ICON.toString());
        buttonImage = new Image(imagePath);
        buttonTooltip = new Tooltip(props.getProperty(REMOVE_PLAYER_TOOLTIP.toString()));
        removePlayerButton.setGraphic(new ImageView(buttonImage));
        removePlayerButton.setTooltip(buttonTooltip);
    }
    
    // INITIALIZE RADIOBUTTONS FOR SORTING BY POSITION
    private void initRadioButtons() {
        // CREATE A TOGGLEGROUP
        rbGroup = new ToggleGroup();
        
        // INITIALIZE EACH RADIOBUTTON
        allRB = new RadioButton("All");
        cRB = new RadioButton("C");
        fbRB = new RadioButton("1B");
        ciRB = new RadioButton("CI");
        tbRB = new RadioButton("3B");
        sbRB = new RadioButton("2B");
        miRB = new RadioButton("MI");
        ssRB = new RadioButton("SS");
        ofRB = new RadioButton("OF");
        uRB = new RadioButton("U");
        pRB = new RadioButton("P");
        
        // SET THE TOGGLEGROUP AND USERDATA
        allRB.setToggleGroup(rbGroup); 
        allRB.setUserData("ALL");
        cRB.setToggleGroup(rbGroup);
        cRB.setUserData("C_");
        fbRB.setToggleGroup(rbGroup);
        fbRB.setUserData("1B");
        ciRB.setToggleGroup(rbGroup);
        ciRB.setUserData("CI");
        tbRB.setToggleGroup(rbGroup);
        tbRB.setUserData("3B");
        sbRB.setToggleGroup(rbGroup);
        sbRB.setUserData("2B");
        miRB.setToggleGroup(rbGroup);
        miRB.setUserData("MI");
        ssRB.setToggleGroup(rbGroup);
        ssRB.setUserData("SS");
        ofRB.setToggleGroup(rbGroup);
        ofRB.setUserData("OF");
        uRB.setToggleGroup(rbGroup);
        uRB.setUserData("U");
        pRB.setToggleGroup(rbGroup);
        pRB.setUserData("P");
        
        // ADD THEM ALL TO THE TOOLBAR
        radioButtonToolbar.getChildren().add(allRB);
        radioButtonToolbar.getChildren().add(cRB);
        radioButtonToolbar.getChildren().add(fbRB);
        radioButtonToolbar.getChildren().add(ciRB);
        radioButtonToolbar.getChildren().add(tbRB);
        radioButtonToolbar.getChildren().add(sbRB);
        radioButtonToolbar.getChildren().add(miRB);
        radioButtonToolbar.getChildren().add(ssRB);
        radioButtonToolbar.getChildren().add(ofRB);
        radioButtonToolbar.getChildren().add(uRB);
        radioButtonToolbar.getChildren().add(pRB);
        allRB.fire();
        
        
        
        //rbGroup.selectedToggleProperty().addListener(new OnTogglehandler());
    }
            
    // SETS UP EVENT HANDLERS FOR THE "SCREEN"
    public void initEventHandlers() {
        // HANDLER FOR RADIO BUTTONS
        rbGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (rbGroup.getSelectedToggle() != null) {
                updateItemsEvent(searchTextField.getText(), rbGroup.getSelectedToggle().getUserData().toString());
            }
        });
        
        // HANDLER FOR EDITING NOTES COLUMN
        notesColumn.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        notesColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<Player, String>>() {

            @Override
            public void handle(CellEditEvent<Player, String> t) {
                ((Player) t.getTableView().getItems().get(
                          t.getTablePosition().getRow())).setNotes(t.getNewValue());
                manyWhelps(t.getNewValue(), t.getTablePosition().getRow());
            }
        });
        
        // HANDLER FOR SEARCH TEXT FIELD
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateItemsEvent(newValue, rbGroup.getSelectedToggle().getUserData().toString());
        });
        
        // HANDLER FOR ADD PLAYER BUTTON
        PlayerEditController pec = new PlayerEditController(gui, primaryStage, dataManager, messageDialog, yncDialog);
        addPlayerButton.setOnAction(e -> {
            pec.handleAddPlayerRequest(gui);
        });
        
        removePlayerButton.setOnAction(e -> {
            Player playerToRemove = (Player) playersTable.getSelectionModel().getSelectedItem();
            // test for a null playerToRemove
            pec.handleRemovePlayerRequest(gui, playerToRemove, playersTable.getItems());
        });
        
        // AND NOW THE SCHEDULE ITEMS TABLE
        playersTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // OPEN UP THE SCHEDULE ITEM EDITOR
                Player playerToEdit = (Player) playersTable.getSelectionModel().getSelectedItem();
                pec.handleEditPlayerRequest(gui, playerToEdit);
            }
        });
    }
    
    // HANDLE IT! (Handler for player notes)
    public void manyWhelps(String newNotesValue, int selectedIndex) {
        Player p = (Player) playersTable.getItems().get(selectedIndex);
        for (int i=0;i<dataManager.getDraft().getAvailablePlayers().size();i++) {
            if (dataManager.getDraft().getAvailablePlayers().get(i).getFirstName().equals(p.getFirstName()) &&
                dataManager.getDraft().getAvailablePlayers().get(i).getLastName().equals(p.getLastName()))
                dataManager.getDraft().getAvailablePlayers().get(i).setNotes(newNotesValue);
        }
    }
    
    public void addTableData(ObservableList<Player> l) {
        playersTable.setItems(l);
        //availablePlayers = l;
        playersTable.getSelectionModel().selectFirst();
    }
    
    public void handleSearchFieldEvent(String content, String oldValue) {
        setItemsToPosition(rbGroup.getSelectedToggle().getUserData().toString());
        ObservableList<Player> trimmedList = FXCollections.observableArrayList();
        ObservableList<Player> items = playersTable.getItems();
        if (content.length() < oldValue.length()){
            playersTable.setItems(itemStack.pop());
        } else {
        for (int i=0;i<items.size();i++) {
            String fn = items.get(i).getFirstName();
            String ln = items.get(i).getLastName();
            if (nameStartsWith(fn, content) || nameStartsWith(ln, content))
                trimmedList.add(items.get(i));
        }
        itemStack.push(playersTable.getItems());
        playersTable.setItems(trimmedList);
        //itemStack.push(trimmedList);
        }
    }
    
    public boolean nameStartsWith(String name, String searchKey) {
        if (searchKey.length() > name.length())
            return false;
        else if (name.substring(0, searchKey.length()).equalsIgnoreCase(searchKey))
            return true;
        return false;
    }
    
    public void updateColumns(String position) {
        // PUSH THE CURRENT ITEMS ONTO THE ITEMSTACK 
        //itemStack.push(playersTable.getItems());      u wot m8?
        
        // CHANGE COLUMN HEADERS BASED ON WHICH BUTTONS ARE SELECTED
        // IF ALL RADIO BUTTON WAS SELECTED
        // SET TABLE ITEMS TO ALL AVAILABLE PLAYERS
        if (position.equals("ALL")) {
            runsWinsColumn.setText("R/W");
            homerunsSavesColumn.setText("HR/SV");
            rbiStrikeoutColumn.setText("RBI/K");
            stolenBasesERAColumn.setText("SB/ERA");
            battingAvgWhipColumn.setText("BA/WHIP");
            //playersTable.setItems(availablePlayers);
        }
        // IF PITCHER RADIO BUTTON WAS SELECTED
        // SET ITEMS TO PITCHERS 
        else if (position.equals("P")) {
            runsWinsColumn.setText("W");
            homerunsSavesColumn.setText("SV");
            rbiStrikeoutColumn.setText("K");
            stolenBasesERAColumn.setText("ERA");
            battingAvgWhipColumn.setText("WHIP");
            //setItemsToPosition(position);
        }
        // IF SOME HITTER BUTTON WAS SELECTED
        // SET ITEMS TO HITTES WITH SPECIFIED POSITION
        else { 
            runsWinsColumn.setText("R");
            homerunsSavesColumn.setText("HR");
            rbiStrikeoutColumn.setText("RBI");
            stolenBasesERAColumn.setText("SB");
            battingAvgWhipColumn.setText("BA");
            //setItemsToPosition(position);
        }
    }
    
    public void setItemsToPosition(String position) {
        ObservableList l = FXCollections.observableArrayList();
        for (int i=0;i<dataManager.getDraft().getAvailablePlayers().size();i++) {
            if (dataManager.getDraft().getAvailablePlayers().get(i).getPositions().contains(position))
                l.add(dataManager.getDraft().getAvailablePlayers().get(i));
        }
        playersTable.setItems(l);
    }
    
    /* Call using current content of text field and the userdata 
    associated with the currently selected toggle
    */
    public void updateItemsEvent(String search, String position) {
        ObservableList<Player> updated = FXCollections.observableArrayList();
        updateColumns(position);
        
        /*
        seach is empty and position is ALL
        search is empty and position is not ALL
        search has content and position is ALL
        search has content and position is not ALL
        */
        
        
        for (int i=0;i<dataManager.getDraft().getAvailablePlayers().size();i++) {
            String p = dataManager.getDraft().getAvailablePlayers().get(i).getPositions();
            String fn = dataManager.getDraft().getAvailablePlayers().get(i).getFirstName();
            String ln = dataManager.getDraft().getAvailablePlayers().get(i).getLastName();
            
            if (search.isEmpty()){
                if (position.equals("ALL")) {
                    playersTable.setItems(dataManager.getDraft().getAvailablePlayers());
                    return;
                } else { // POSITION IS NOT ALL
                    if (p.contains(position))
                        updated.add(dataManager.getDraft().getAvailablePlayers().get(i));
                }
            } else { // SEARCH IS NOT EMPTY
                if (position.equals("ALL")) {
                    if (nameStartsWith(fn, search) || nameStartsWith(ln, search))
                        updated.add(dataManager.getDraft().getAvailablePlayers().get(i));
                } else { // POSITION IS NOT ALL AND SEARCH IS NOT EMPTY
                    if ((nameStartsWith(fn, search) || nameStartsWith(ln, search)) && p.contains(position))
                            updated.add(dataManager.getDraft().getAvailablePlayers().get(i));
                }
            }
                
        }
        playersTable.setItems(updated);
    }

    public void update(DraftDataManager dm) {
        dataManager = dm;
        Draft d = dataManager.getDraft();
        searchTextField.clear();
        playersTable.setItems(d.getAvailablePlayers());
        allRB.fire();
    }

    public void clearSearchText() {
        searchTextField.clear();
    }
    
    
}


