/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wdk.data.Draft;
import wdk.data.Team;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.PRIMARY_STYLE_SHEET;

/**
 *
 * @author Guacamole
 */
public class FantasyTeamDialog extends Stage{
    // DATA
    Draft draft;
    Team team;
    ObservableList<String> proTeams;
    ObservableList<String> positions;
    ObservableList<String> contracts;
    
    // CONTROLS
    GridPane gridPane;
    Scene dialogScene;
    Label headingLabel;
    Label nameLabel;
    TextField nameTextField;
    Label ownerLabel;
    TextField ownerTextField;
    
    Button completeButton;
    Button cancelButton;
    String selection;
            
    // CONSTANTS FOR OUR UI
    public static final String COMPLETE = "Complete";
    public static final String CANCEL = "Cancel";
    public static final String FIRST_NAME_PROMPT = "First Name: ";
    public static final String LAST_NAME_PROMPT = "Last Name:";
    public static final String PRO_TEAM_PROMPT = "Pro Team:";
    public static final String TEAM_HEADING = "Team Details";
    public static final String ADD_TEAM_TITLE = "Add New Fantasy Team";
    public static final String EDIT_TEAM_TITLE = "Edit Fantasy Team";
    
    public FantasyTeamDialog(Stage primaryStage, Draft d, MessageDialog messageDialog) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        // INITIATE THE CONTROLS
        // FIRST THE HEADING LABEL
        headingLabel = new Label("Fantasy Team Details");
        headingLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        // THEN THE NAME LABEL AND TEXTFIELD
        nameLabel = new Label("Name: ");
        nameTextField = new TextField();
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            team.setName(newValue);
        });
        
        // THEN THE OWNER LABEL AND TEXT
        ownerLabel = new Label("Owner: ");
        ownerTextField = new TextField();
        ownerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            team.setOwner(newValue);
        });
        // AND FINALLY, THE BUTTONS
        completeButton = new Button(COMPLETE);
        cancelButton = new Button(CANCEL);
        
        // REGISTER EVENT HANDLERS FOR OUR BUTTONS
        EventHandler completeCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            FantasyTeamDialog.this.selection = sourceButton.getText();
            FantasyTeamDialog.this.hide();
        };
        completeButton.setOnAction(completeCancelHandler);
        cancelButton.setOnAction(completeCancelHandler);
        
        // ADD EVERYTHING TO THE GRID PANE
        gridPane.add(headingLabel, 0, 0, 2, 1);
        gridPane.add(nameLabel, 0, 1, 1, 1);
        gridPane.add(nameTextField, 1, 1, 1, 1);
        gridPane.add(ownerLabel, 0, 2, 1, 1);
        gridPane.add(ownerTextField, 1, 2, 1, 1);
        gridPane.add(completeButton, 0, 3, 1, 1);
        gridPane.add(cancelButton, 1, 3, 1, 1);
        
        dialogScene = new Scene(gridPane);
        dialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        this.setScene(dialogScene);
    }
    
    public Team showAddTeamDialog() {
        // SET THE DIALOG TITLE
        setTitle(ADD_TEAM_TITLE);
        
        // RESET THE PLAYER OBJECT WITH DEFAULT VALUES
        team = new Team();
        
        // LOAD THE UI STUFF
        nameTextField.setText(team.getName());
        ownerTextField.setText(team.getOwner());
        
        // AND OPEN IT UP
        this.showAndWait();
        
        return team;
    }
    
    public void showEditTeamDialog(Team teamToEdit) {
        // SET THE DIALOG TITLE
        setTitle(EDIT_TEAM_TITLE);
        
        // LOAD THE SCHEDULE ITEM INTO OUR LOCAL OBJECT
        team = new Team();
        team.setName(teamToEdit.getName());
        team.setOwner(teamToEdit.getOwner());
        
        // AND THEN INTO OUR GUI
        //loadGUIData();
        nameTextField.setText(team.getName());
        ownerTextField.setText(team.getOwner());
               
        // AND OPEN IT UP
        this.showAndWait();
    }
    
    public boolean wasCompleteSelected() {
        return selection.equals(COMPLETE);
    }

    public Team getTeam() {
        return team;
    }
}
