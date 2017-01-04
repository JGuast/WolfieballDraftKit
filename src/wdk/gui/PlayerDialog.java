/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static wdk.base.WDK_StartupConstants.PATH_FLAG_IMAGES;
import static wdk.base.WDK_StartupConstants.PATH_PLAYER_IMAGES;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import wdk.data.Team;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_PROMPT_LABEL;
import static wdk.gui.wdk_GUI.PRIMARY_STYLE_SHEET;

/**
 *
 * @author Guacamole
 */
public class PlayerDialog extends Stage {
    // THIS IS THE OBJECT DATA BEHIND THIS UI
    wdk_GUI gui;
    Draft draft;
    Player player;
    Player editPlayer;
    ObservableList<String> proTeams;
    ObservableList<String> positions;
    ObservableList<String> positionCandidates;
    ObservableList<String> contracts;
    DraftDataManager dataManager;
    ImageView pictureFrame;
    ImageView flagFrame;
    
    // GUI CONTROLS FOR OUR ADDDIALOG
    GridPane addPane;
    Scene addDialogScene;
    Scene editDialogScene;
    Label headingLabel;
    Label firstNameLabel;
    Label lastNameLabel;
    TextField firstNameTextField;
    TextField lastNameTextField;
    Label proTeamLabel;
    ComboBox proTeamComboBox;
    Button completeButtonA;
    Button cancelButtonA;
    
    // GUI CONTROL FOR OUR EDITDIALOG
    GridPane editPane;
    Image stockImage;
    Image flagImage;
    Label playerNameLabel;
    Label playerPositionsLabel;
    Label fantasyTeamLabel;
    ComboBox fantasyTeamsComboBox;
    Label positionsLabel;
    ComboBox positionsComboBox;
    Label contractLabel;
    ComboBox contractsComboBox;
    Label salaryLabel;
    TextField salaryTextField;
    Button completeButtonB;
    Button cancelButtonB;
   
    // CHECKBOXES
    HBox toolbar;
    CheckBox catcherBox;
    CheckBox fbBox;
    CheckBox tbBox;
    CheckBox sbBox;
    CheckBox ssBox;
    CheckBox ofBox;
    CheckBox pBox;
    
    // THIS IS FOR KEEPING TRACK OF WHICH BUTTON THE USER PRESSED
    String selection;
    
    // CONSTANTS FOR OUR UI
    public static final String COMPLETE = "Complete";
    public static final String CANCEL = "Cancel";
    public static final String FIRST_NAME_PROMPT = "First Name: ";
    public static final String LAST_NAME_PROMPT = "Last Name:";
    public static final String PRO_TEAM_PROMPT = "Pro Team:";
    public static final String PLAYER_HEADING = "Player Details";
    public static final String ADD_PLAYER_TITLE = "Add New Player";
    public static final String EDIT_PLAYER_TITLE = "Edit Player";
    
    public PlayerDialog(wdk_GUI initGUI, Stage primaryStage, Draft d, MessageDialog messageDialog, DraftDataManager initDataManager) {       
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        gui = initGUI;
        dataManager = initGUI.getDataManager();
        draft = d;
        dataManager = initDataManager;
        
        String imagePath = "file:" + PATH_PLAYER_IMAGES + "AAA_PhotoMissing.jpg";
        stockImage = new Image(imagePath);
        pictureFrame = new ImageView(stockImage);
        
        imagePath = "file:" + PATH_FLAG_IMAGES + "base_flag.jpg";
        flagImage = new Image(imagePath);
        flagFrame = new ImageView(flagImage);
        
        // FIRST OUR CONTAINERS
        addPane = new GridPane();
        addPane.setPadding(new Insets(10, 20, 20, 20));
        addPane.setHgap(10);
        addPane.setVgap(10);
        
        editPane = new GridPane();
        editPane.setPadding(new Insets(10, 20, 20, 20));
        editPane.setHgap(10);
        editPane.setVgap(10);
        
        // PUT THE HEADING IN THE GRID
        headingLabel = new Label(PLAYER_HEADING);
        headingLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        positionCandidates = FXCollections.observableArrayList();
        positionCandidates.add("");
    
        // ADDING CONTROLS--------------------------------------------
        // NOW THE FIRST NAME 
        firstNameLabel = new Label(FIRST_NAME_PROMPT);
        firstNameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        firstNameTextField = new TextField();
        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            player.setFirstName(newValue);
        });
        
        // NOW THE LAST NAME 
        lastNameLabel = new Label(LAST_NAME_PROMPT);
        lastNameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        lastNameTextField = new TextField();
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            player.setLastName(newValue);
        });
        
        // AND THE PRO TEAM
        proTeamLabel = new Label(PRO_TEAM_PROMPT);
        proTeamLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        proTeamComboBox = new ComboBox();
        initLists();
        proTeamComboBox.setItems(proTeams);
        
        proTeamComboBox.setOnAction(e -> {
            player.setProTeam((String)proTeamComboBox.getValue());
        });
        
        // AND RADIOBUTTONS
        catcherBox = new CheckBox("C");
        fbBox = new CheckBox("1B");
        tbBox = new CheckBox("3B");
        sbBox = new CheckBox("2B");
        ssBox = new CheckBox("SS");
        ofBox = new CheckBox("OF");
        pBox = new CheckBox("P");
        
        catcherBox.setOnAction(e -> {
            handleCheckBoxEvent(catcherBox.getText());
        });
        fbBox.setOnAction(e -> {
            handleCheckBoxEvent(fbBox.getText());
        });
        tbBox.setOnAction(e -> {
            handleCheckBoxEvent(tbBox.getText());
        });
        sbBox.setOnAction(e -> {
            handleCheckBoxEvent(sbBox.getText());
        });
        ssBox.setOnAction(e -> {
            handleCheckBoxEvent(ssBox.getText());
        });
        ofBox.setOnAction(e -> {
            handleCheckBoxEvent(ofBox.getText());
        });
        pBox.setOnAction(e -> {
            handleCheckBoxEvent(pBox.getText());
        });
        
        // AND TOOLBAR
        toolbar = new HBox();
        toolbar.getChildren().add(catcherBox);
        toolbar.getChildren().add(fbBox);
        toolbar.getChildren().add(tbBox);
        toolbar.getChildren().add(sbBox);
        toolbar.getChildren().add(ssBox);
        toolbar.getChildren().add(ofBox);
        toolbar.getChildren().add(pBox);
        
        // AND FINALLY, THE BUTTONS
        completeButtonA = new Button(COMPLETE);
        cancelButtonA = new Button(CANCEL);
        
        completeButtonB = new Button(COMPLETE);
        cancelButtonB = new Button(CANCEL);
        
        // REGISTER EVENT HANDLERS FOR OUR BUTTONS
        EventHandler completeCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            PlayerDialog.this.selection = sourceButton.getText();
            PlayerDialog.this.hide();
        };
        
        // EDITING CONTROLS -------------------------------- ADD EVENT LISTENERS
        playerNameLabel = new Label("");
        playerNameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        playerPositionsLabel = new Label("");
        playerPositionsLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        
        fantasyTeamLabel = new Label("Fantasy Team:");
        fantasyTeamsComboBox = new ComboBox();
        fantasyTeamsComboBox.setItems(d.getTeamsAsStrings());
        fantasyTeamsComboBox.setOnAction(e -> {
            // if player.getFantasyTeam != selected item then select it, else:
            String s = (String) fantasyTeamsComboBox.getSelectionModel().getSelectedItem();
            if (s != null)
                player.setFantasyTeam(s);
            //updatePositionsComboBox(editPlayer);
            updateCompleteButton(player);
        });
        
        positionsLabel = new Label("Position:");
        positionsComboBox = new ComboBox();
        positionsComboBox.setItems(positionCandidates);
        positionsComboBox.setOnAction(e -> {
            player.setPositionInTeam((String)positionsComboBox.getSelectionModel().getSelectedItem());
            //updatePositionsComboBox(editPlayer);
            updateCompleteButton(player);
        });
        
        contractLabel = new Label("Contract:");
        contractsComboBox = new ComboBox();
        contractsComboBox.setItems(contracts);
        contractsComboBox.setOnAction(e ->{
            player.setContract((String)contractsComboBox.getSelectionModel().getSelectedItem());
            updateCompleteButton(player);
        });
        
        salaryLabel = new Label("Salary (USD):");
        salaryTextField = new TextField();
        salaryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                player.setSalary(Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                salaryTextField.clear();
            }
        });
        
        /*String imagePath = "file:" + PATH_IMAGES + "/players/AAA_PhotoMissing.jpg";
        playerImage = new Image(imagePath);
        piv = new ImageView(playerImage);*/
        
        completeButtonA.setOnAction(completeCancelHandler);
        cancelButtonA.setOnAction(completeCancelHandler);
        completeButtonB.setOnAction(completeCancelHandler);
        cancelButtonB.setOnAction(completeCancelHandler);

        // NOW LET'S ARRANGE THEM ALL AT ONCE
        addPane.add(headingLabel, 0, 0, 2, 1);
        addPane.add(firstNameLabel, 0, 1, 1, 1);
        addPane.add(firstNameTextField, 1, 1, 1, 1);
        addPane.add(lastNameLabel, 0, 2, 1, 1);
        addPane.add(lastNameTextField, 1, 2, 1, 1);
        addPane.add(proTeamLabel, 0, 3, 1, 1);
        addPane.add(proTeamComboBox, 1, 3, 1, 1);
        addPane.add(toolbar, 0, 5, 1, 1);
        addPane.add(completeButtonA, 0, 7, 1, 1);
        addPane.add(cancelButtonA, 1, 7, 1, 1);
        
        editPane.add(headingLabel, 0, 0, 2, 1);
        editPane.add(pictureFrame, 0, 1, 1, 1);
        editPane.add(flagFrame, 1, 1, 1, 1);
        editPane.add(playerNameLabel, 1, 2, 1, 1);
        editPane.add(playerPositionsLabel, 1, 3, 1, 1);
        editPane.add(fantasyTeamLabel, 0, 4, 1, 1);        // must add player picture and flag
        editPane.add(fantasyTeamsComboBox, 1, 4, 1, 1);
        editPane.add(positionsLabel, 0, 5, 1, 1);
        editPane.add(positionsComboBox, 1, 5, 1, 1);
        editPane.add(contractLabel, 0, 6, 1, 1);
        editPane.add(contractsComboBox, 1, 6, 1, 1);
        editPane.add(salaryLabel, 0, 7, 1, 1);
        editPane.add(salaryTextField, 1, 7, 1, 1);
        editPane.add(completeButtonB, 0, 8, 1, 1);
        editPane.add(cancelButtonB, 1, 8, 1, 1);
        // completeButton
        // cancelButton

        // AND PUT THE GRID PANE IN THE WINDOW
        addDialogScene = new Scene(addPane);
        addDialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        //this.setScene(addDialogScene);
        
        editDialogScene = new Scene(editPane);
        editDialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
    }
    
    public Player showAddPlayerDialog() {
        // SET THE DIALOG TITLE
        setTitle(ADD_PLAYER_TITLE);
        
        // RESET THE PLAYER OBJECT WITH DEFAULT VALUES
        player = new Player();
        
        // LOAD THE UI STUFF
        firstNameTextField.setText(player.getFirstName());
        lastNameTextField.setText(player.getLastName());
        proTeamComboBox.setValue(player.getProTeam());
        
        // AND OPEN IT UP
        //useAddPane();
        this.setScene(addDialogScene);
        this.showAndWait();
        
        return player;
    }
    
    public void showEditPlayerDialog(Player playerToEdit, DraftDataManager d) {
        // SET THE DIALOG TITLE
        setTitle(EDIT_PLAYER_TITLE);
        //editPlayer = playerToEdit;
        
        // LOAD THE PLAYER INTO OUR LOCAL OBJECT
        player = new Player();
        player.setFirstName(playerToEdit.getFirstName());
        player.setLastName(playerToEdit.getLastName());
        player.setPositions(playerToEdit.getPositions());
        player.setFantasyTeam(playerToEdit.getFantasyTeam());
        player.setBirthNation(playerToEdit.getBirthNation());
        player.setPositionInTeam(playerToEdit.getPositionInTeam());
        player.setContract(playerToEdit.getContract());
        player.setSalary(playerToEdit.getSalary());
        player.setPick(playerToEdit.getPick());
        
        // AND THEN INTO OUR GUI
        // IMAGES FIRST
        try {
            String imagePath = "file:" + PATH_PLAYER_IMAGES + player.getLastName() + player.getFirstName() + ".jpg";
            Image image = new Image(imagePath);
            pictureFrame.setImage(image);
        } catch(Exception e) { pictureFrame.setImage(stockImage);}
        
        try {
            String fImagePath = "file:" + PATH_FLAG_IMAGES + player.getBirthNation() + ".png";
            Image fImage = new Image(fImagePath);
            flagFrame.setImage(fImage);
        } catch(Exception ex) { flagFrame.setImage(flagImage);}
        
        // THEN THE REST
        playerNameLabel.setText(player.getFirstName() + " " + player.getLastName());
        playerPositionsLabel.setText(player.getPositions());
        //updatePositionsComboBox(playerToEdit);
        //String s = (String)fantasyTeamsComboBox.getSelectionModel().getSelectedItem();
        String s = playerToEdit.getFantasyTeam();
        if (s.equals("Free Agent") == false) {
            Team t = draft.getTeamByName(s);
            positionsComboBox.setItems(t.getVacantPositions(createCandidates(player)));
        } 
        else positionsComboBox.setItems(createCandidates(player));
        positionsComboBox.getSelectionModel().selectFirst();
        
        ObservableList<String> l = FXCollections.observableArrayList();
        l.add("Free Agent"); 
        l.addAll(d.getDraft().getTeamsAsStrings());
        fantasyTeamsComboBox.setItems(l);
        
        //fantasyTeamsComboBox.getSelectionModel().select(player.getFantasyTeam());
        fantasyTeamsComboBox.setValue(playerToEdit.getFantasyTeam());
        positionsComboBox.setValue(playerToEdit.getPositionInTeam());
        contractsComboBox.setValue(playerToEdit.getContract());
        salaryTextField.setText(playerToEdit.getSalary() + "");
               
        // AND OPEN IT UP
        updateCompleteButton(player);
        this.setScene(editDialogScene);
        this.showAndWait();
    }
    
    public Player getPlayer() {
        return player;
    }
        
    public boolean wasCompleteSelected() {
        return selection.equals(COMPLETE);
    }
    
    public void loadGUIData() {
        // LOAD THE UI STUFF
        //updatePositionsComboBox(playerToEdit);
        //lectureTopicTextField.setText(lectureItem.getTopic());
        //numSessionsComboBox.setValue(lectureItem.getSessions()); 
    }
    
    public void useEditPane() {
        addDialogScene = new Scene(editPane);
        addDialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        this.setScene(addDialogScene);
    }
    
    public void useAddPane() {
        addDialogScene = new Scene(addPane);
        addDialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        this.setScene(addDialogScene);
    }
    
    private void initLists() {
        // proTeams list
        proTeams = FXCollections.observableArrayList();
        proTeams.add("AZ");
        proTeams.add("ATL");
        proTeams.add("CHI");
        proTeams.add("CIN");
        proTeams.add("COL");
        proTeams.add("LAD");
        proTeams.add("MIA");
        proTeams.add("MIL");
        proTeams.add("NYM");
        proTeams.add("PHI");
        proTeams.add("PIT");
        proTeams.add("SD");
        proTeams.add("SF");
        proTeams.add("STL");
        proTeams.add("WAS");
        
        // positions list
        positions = FXCollections.observableArrayList();
        positions.add("C");
        positions.add("1B");
        positions.add("3B");
        positions.add("CI");
        positions.add("2B");
        positions.add("SS");
        positions.add("MI");
        positions.add("OF");
        positions.add("P");
        positions.add("U");
        
        // contracts list
        contracts = FXCollections.observableArrayList();
        contracts.add("S2");
        contracts.add("S1");
        contracts.add("X");
    }
    
    private void adjustPositionCandidates(Player p) {
        try {
        Team t = dataManager.getDraft().getTeamByName(p.getFantasyTeam());
        ObservableList<String> temp = FXCollections.observableArrayList();
        for (int i=0;i<p.getPositionCandidates().size();i++) {
            if (t.hasMetQuota(p.getPositionCandidates().get(i)) == false)
                temp.add(p.getPositionCandidates().get(i));
        }
        p.getPositionCandidates().clear();
        p.getPositionCandidates().addAll(temp);
        }catch (NullPointerException e) {
            // YA DONE FUCKED UP
        }
    }
    
    private ObservableList<String> createCandidates(Player p) {
        ObservableList<String> positions = FXCollections.observableArrayList();
        if (p.getPositions().contains("C_"))
            positions.add("C");
        if (p.getPositions().contains("1B"))
            positions.add("1B");
        if (p.getPositions().contains("3B"))
            positions.add("3B");
        if (p.getPositions().contains("CI"))
            positions.add("CI");
        if (p.getPositions().contains("2B"))
            positions.add("2B");
        if (p.getPositions().contains("SS"))
            positions.add("SS");
        if (p.getPositions().contains("MI"))
            positions.add("MI");
        if (p.getPositions().contains("OF"))
            positions.add("OF");
        if (p.getPositions().contains("U"))
            positions.add("U");
        if (p.getPositions().contains("P"))
            positions.add("P");
        return positions;
    }
    
    public void handleCheckBoxEvent(String s) {
        if (s.equals("P")) {
           catcherBox.setSelected(false);
           fbBox.setSelected(false);
           tbBox.setSelected(false);
           sbBox.setSelected(false);
           ssBox.setSelected(false);
           ofBox.setSelected(false);
           
           player.setPositions("P");
        } else {
            if (pBox.isSelected()) {
                pBox.setSelected(false);
                player.setPositions(s);
            } else {
                s = "";
                if (catcherBox.isSelected())
                    s += "C_";
                
                if (fbBox.isSelected())
                    if (tbBox.isSelected())
                        s += "1B_3B_CI_";
                    else s+= "1B_CI_";
                else if (tbBox.isSelected())
                        s += "3B_CI_";
                
                if (sbBox.isSelected())
                    if (ssBox.isSelected())
                        s += "2B_SS_MI_";
                    else s+= "2B_MI_";
                else if (ssBox.isSelected())
                        s += "SS_MI_";
                
                if (ofBox.isSelected())
                    s += "OF_";
                
                s += "U";
                player.setPositions(s);
            }
        }
            
        //switch(s)
    }
    
    private void updatePositionsComboBox(Player p) {
        String s = (String)fantasyTeamsComboBox.getSelectionModel().getSelectedItem();
        if (s == null)
            return;
        if (s.equals("Free Agent"))
            return;
        Team fTeam = draft.getTeamByName(s);
        String boxPosition = (String) positionsComboBox.getSelectionModel().getSelectedItem();
        ObservableList<String> newPositions = FXCollections.observableArrayList();
        ObservableList<String> pCandidates = p.getElligiblePositions();
        // TEAM SELECTED IS NOT FREE AGENT
        if (fTeam.equals("Free Agent") == false) {
            // LOOP FOR EACH ELIGIBLE POSITION OF THE PLAYER
            for (int i=0;i<p.getPositionCandidates().size();i++) {
                // IF THE SELECTED TEAM HAS NOT MET THE QUOTA FOR THAT POSITION
                if (fTeam.hasMetQuota(pCandidates.get(i)) == false)
                    // ADD IT TO THE NEW ITEMS
                    newPositions.add(pCandidates.get(i));
           }
        } 
        // TEAM SELECTED IS FREE AGENT
        else {
           // DO NOTHING 
        }
        
        
        /*
        if (p.getFantasyTeam().equals("Free Agent")) {
            
        } else {
        Team t = draft.getTeamByName(p.getFantasyTeam());
        ObservableList<String> pp = p.getPositionCandidates();
        ObservableList<String> tp = t.getVacantPositions(positions);
        ObservableList<String> newItems = FXCollections.observableArrayList();
        for (int i=0;i<pp.size();i++) {
            if (tp.contains(pp.get(i)))
                newItems.add(pp.get(i));
        }
        positionsComboBox.setItems(newItems);
        }*/
    }
    
    private void updateCompleteButton(Player p) {
        try {
        String ps = (String) positionsComboBox.getSelectionModel().getSelectedItem();
        if (ps.equals("N/A"))
            completeButtonB.setDisable(true);
        else
            completeButtonB.setDisable(false);
    } catch (NullPointerException e) {
        // something went wrong
    }
    }
        
}
