/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_SCREEN_PANE;
import static wdk.gui.wdk_GUI.CLASS_SUBHEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_TOOLBAR_PANE;

/**
 *
 * @author Guacamole
 */
class MLBTeamsScreen {
    Stage primaryStage;
    
    // DATA TO LOAD INTO THE SCREEN
    wdk_GUI gui;
    DraftDataManager dataManager;
    ObservableList<Player> availablePlayers;
    ObservableList<Player> tablePlayers;
    
    // PANE TO HOLD CONTROLS AND LABEL FOR THE HEADER
    VBox screenPane;
    Label screenHeaderLabel;
    HBox toolbar;
    
    MessageDialog messageDialog;
    YesNoCancelDialog yncDialog;
    
    // CONTOLS
    Label mlbTeamsLabel;
    ComboBox mlbTeamsComboBox;
    
    TableView playersTable;
    TableColumn firstNameColumn;
    TableColumn lastNameColumn;
    TableColumn positionsColumn;
    
    
    public MLBTeamsScreen(wdk_GUI initGUI, Stage initPrimaryStage, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        primaryStage = initPrimaryStage;
        dataManager = initGUI.getDataManager();
        availablePlayers = dataManager.getDraft().getAvailablePlayers();
        
        // INITIALIZE THE VBOX FOR THE SCREEN
        screenPane = new VBox();
        screenPane.getStyleClass().add(CLASS_SCREEN_PANE);
        screenPane.setPadding(new Insets(30, 20, 20, 20));
        
        toolbar = new HBox();
        toolbar.getStyleClass().add(CLASS_TOOLBAR_PANE);
        toolbar.setPadding(new Insets(10, 10, 10, 0));
        
        // INITIATE THE CONTROLS
        // FIRST THE HEADER LABEL
        screenHeaderLabel = new Label("MLB Teams");
        screenHeaderLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        // THEN THE COMBOBOX
        mlbTeamsLabel = new Label("MLB Teams:");
        mlbTeamsLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        mlbTeamsComboBox = new ComboBox();
        mlbTeamsComboBox.setItems(getMLBTeams());
        mlbTeamsComboBox.setOnAction(e -> {
            playersTable.setItems(getProRoster());
        });
        
        toolbar.getChildren().add(mlbTeamsLabel);
        toolbar.getChildren().add(mlbTeamsComboBox);
        
        // AND THE TABLEVIEW
        playersTable = new TableView();
        // INITIALIZE COLUMNS
        firstNameColumn = new TableColumn("First Name");
        lastNameColumn = new TableColumn("Last Name");
        positionsColumn = new TableColumn("Qualifying Positions");
        //GIVE VALUES TO THE COLUMNS
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("lastName"));
        positionsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("positions"));
        // ADD COLUMNS TO THE TABLE
        playersTable.getColumns().add(firstNameColumn);
        playersTable.getColumns().add(lastNameColumn);
        playersTable.getColumns().add(positionsColumn);
        
        playersTable.setItems(availablePlayers);
        // ADD EVERYTHING TO THE GUI
        screenPane.getChildren().add(screenHeaderLabel);
        screenPane.getChildren().add(toolbar);
        screenPane.getChildren().add(playersTable);
    }

    public VBox getScreenPane() {
        return screenPane;
    }
    
    public ObservableList<Player> getProRoster() {
        tablePlayers = FXCollections.observableArrayList();
        String teamName = (String) mlbTeamsComboBox.getSelectionModel().getSelectedItem();
        
        //ATL, AZ, CHC, CIN, COL, LAD, MIA, MIL, NYM, PHI, PIT, SD, SF, STL, WAS. 
        for (int i=0; i<availablePlayers.size();i++) {
            if (availablePlayers.get(i).getProTeam().equals(teamName))
                tablePlayers.add(availablePlayers.get(i));
        }
        return tablePlayers;
    }
    
    private ObservableList<String> getMLBTeams() {
        ObservableList<String> teams = FXCollections.observableArrayList();
        teams.add("ATL");
        teams.add("AZ");
        teams.add("CHC");
        teams.add("CIN");
        teams.add("COL");
        teams.add("LAD");
        teams.add("MIA");
        teams.add("MIL");
        teams.add("NYM");
        teams.add("PHI");
        teams.add("PIT");
        teams.add("SD");
        teams.add("SF");
        teams.add("STL");
        teams.add("WSH");
        return teams;
    }
    
    public void setDM(DraftDataManager dm) {
        dataManager = dm;
        availablePlayers = dm.getDraft().getAvailablePlayers();
        //playersTable.setItems(availablePlayers);
        try {
           playersTable.setItems(getProRoster());
        } catch (Exception e) {}
    }

    void update(DraftDataManager dataManager) {
        mlbTeamsComboBox.setValue("");
        playersTable.getItems().clear();
    }
    
}
