/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_Properties.DRAFT_ONE_ICON;
import static wdk.base.WDK_Properties.DRAFT_ONE_TOOLTIP;
import static wdk.base.WDK_Properties.PAUSE_DRAFT_TOOLTIP;
import static wdk.base.WDK_Properties.PAUSE_ICON;
import static wdk.base.WDK_Properties.START_DRAFT_TOOLTIP;
import static wdk.base.WDK_Properties.START_ICON;
import static wdk.base.WDK_StartupConstants.PATH_IMAGES;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import wdk.data.Team;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_SCREEN_PANE;
import static wdk.gui.wdk_GUI.CLASS_TOOLBAR_PANE;

/**
 *
 * @author Guacamole
 */
public class DraftScreen {
    Stage primaryStage;
    wdk_GUI gui;
    Thread thread;
    
    DraftDataManager dataManager;
    Draft draft;
    ObservableList<Player> draftedPlayers;
    int others; //non-S2 players drafted
    
    // PANE TO HOLD CONTROLS AND LABEL FOR THE HEADER
    VBox screenPane;
    HBox buttons;
    Label screenHeaderLabel;
    
    // CONTROLS FOR THE SCREEN
    Button draftOneButton;
    Button startButton;
    Button pauseButton;
    
    TableView draftedTable;
    TableColumn pickColumn;
    TableColumn firstColumn;
    TableColumn lastColumn;
    TableColumn teamColumn;
    TableColumn contractColumn;
    TableColumn salaryColumn;
    
    public DraftScreen(wdk_GUI initGUI, Stage primaryStageA) {
        gui = initGUI;
        dataManager = initGUI.getDataManager();
        draft = initGUI.getDataManager().getDraft();
        draftedPlayers = FXCollections.observableArrayList();
        others = 0;
        
        screenHeaderLabel = new Label("Draft Summary");
        screenHeaderLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        screenPane = new VBox();
        screenPane.getStyleClass().add(CLASS_SCREEN_PANE);
        screenPane.setPadding(new Insets(30, 20, 20, 20));
        
        buttons = new HBox();
        buttons.getStyleClass().add(CLASS_TOOLBAR_PANE);
        buttons.setPadding(new Insets(5, 5, 5, 5));
        
        // INITIALIZE THE CONTROLS
        // FIRST THE BUTTONS
        draftOneButton = new Button();
        startButton = new Button();
        pauseButton = new Button();
        pauseButton.setDisable(true);
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(DRAFT_ONE_ICON);
        Image buttonImage = new Image(imagePath);
        Tooltip buttonTooltip = new Tooltip(props.getProperty(DRAFT_ONE_TOOLTIP.toString()));
        draftOneButton.setGraphic(new ImageView(buttonImage));
        draftOneButton.setTooltip(buttonTooltip);
        
        imagePath = "file:" + PATH_IMAGES + props.getProperty(START_ICON);
        buttonImage = new Image(imagePath);
        buttonTooltip = new Tooltip(props.getProperty(START_DRAFT_TOOLTIP.toString()));
        startButton.setGraphic(new ImageView(buttonImage));
        startButton.setTooltip(buttonTooltip);
        
        imagePath = "file:" + PATH_IMAGES + props.getProperty(PAUSE_ICON);
        buttonImage = new Image(imagePath);
        buttonTooltip = new Tooltip(props.getProperty(PAUSE_DRAFT_TOOLTIP.toString()));
        pauseButton.setGraphic(new ImageView(buttonImage));
        pauseButton.setTooltip(buttonTooltip);
        
        buttons.getChildren().add(draftOneButton);
        buttons.getChildren().add(startButton);
        buttons.getChildren().add(pauseButton);
        
        // THEN THE TABLE
        draftedTable = new TableView();
        pickColumn = new TableColumn("Pick");
        firstColumn = new TableColumn("First");
        lastColumn = new TableColumn("Last");
        teamColumn = new TableColumn("Team");
        contractColumn = new TableColumn("Contract");
        salaryColumn = new TableColumn("Salary");
        
        pickColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("pick"));      // save/load pick
        firstColumn.setCellValueFactory(new PropertyValueFactory<String, String>("firstName"));
        lastColumn.setCellValueFactory(new PropertyValueFactory<String, String>("lastName"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<String, String>("fantasyTeam"));
        contractColumn.setCellValueFactory(new PropertyValueFactory<String, String>("contract"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("salary"));
        
        draftedTable.getColumns().add(pickColumn);
        draftedTable.getColumns().add(firstColumn);
        draftedTable.getColumns().add(lastColumn);
        draftedTable.getColumns().add(teamColumn);
        draftedTable.getColumns().add(contractColumn);
        draftedTable.getColumns().add(salaryColumn);
        
        draftedTable.setItems(draftedPlayers);
        //draftedTable.setSortPolicy(new Callback());
        
        // INITIALIZE EVENT HANDLERS
        initEventHandlers();
        
        // ADD EVERYTHING TO THE SCREEN
        screenPane.getChildren().add(screenHeaderLabel);
        screenPane.getChildren().add(buttons);
        screenPane.getChildren().add(draftedTable);
    }

    public void initEventHandlers() {
        draftOneButton.setOnAction(e -> {
            draftOne();
        });
        
        startButton.setOnAction(e -> {
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            automaticDraft();
        });
        
        pauseButton.setOnAction(e -> {
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            pauseDraft();
        });
    }
    
    public VBox getDraftScreenPane() {
        return screenPane;
    }

    public boolean draftOne() {
        if (draft.hasTeamWithVacancy()) {
            Team t = draft.getTeamWithVacancy();
            ObservableList<Player> avPlayers = draft.getAvailablePlayers();
            // FOR EACH AVAILABLE PLAYER
            for (int i=0;i<avPlayers.size();i++) {
                // FOR EACH OF THEIR ELIGIBLE POSITIONS
                avPlayers.get(i).setPositionCandidates(avPlayers.get(i).getElligiblePositions());
                for (int j=0;j<avPlayers.get(i).getPositionCandidates().size();j++) {
                    // IF TEAM HAS NOT MET QUOTA FOR THAT POSITION
                    if (t.hasMetQuota(avPlayers.get(i).getPositionCandidates().get(j)) == false) {
                        // ADD HIM TO THE TEAM AND REMOVE HIM FROM AVAILABLE PLAYERS
                        avPlayers.get(i).setPositionInTeam(avPlayers.get(i).getPositionCandidates().get(j));
                        avPlayers.get(i).setPick(draftedTable.getItems().size()+1+others);
                        avPlayers.get(i).setFantasyTeam(t.getName());
                        avPlayers.get(i).setContract("S2");
                        avPlayers.get(i).setSalary(1);
                        t.getRoster().add(avPlayers.get(i));
                        draftedTable.getItems().add(avPlayers.get(i));
                        //Collections.sort() for table items by position
                        avPlayers.remove(i);
                        gui.getStandingsScreen().updateStats();
                        return true;
                    }
                }
            }
            return false;//should never be reached
        } else if (draft.hasTaxiOpening()) {
            Team t = draft.getTeamWithTaxiSpot();
            
            Player player = draft.getAvailablePlayers().get(0);
            String p = player.getElligiblePositions().get(0);
            
            player.setPick(draftedTable.getItems().size()+1+others);
            player.setFantasyTeam(t.getName());
            player.setPositionInTeam(PATH_IMAGES);
            player.setPositionInTeam(p);
            player.setContract("S2");
            player.setSalary(1);
            
            t.getTaxiSquad().add(player);
            draftedTable.getItems().add(player);
            draft.getAvailablePlayers().remove(0);
            gui.getStandingsScreen().update(gui.getDataManager());
            return true;
        } 
        return false;
    }
    
    public void automaticDraft() {
        //create a new thread
        //repeatedly call the draftOne method until 
        //every team's roster/taxi squad are filled 
        //or until a player "pauses"
        
        /* Multithreading */
        
        ReentrantLock progressLock = new ReentrantLock();
        
        Task<Void> task = new Task<Void>() {
            boolean continueAdding = true;
            @Override
            protected Void call () throws Exception {
                //progressLock.lock();
                while (continueAdding && draft.isFull() == false) {
                    // TO BE DONE ASYNCHRONOUSLY VIA MULTITHREDING
                    Platform.runLater(new Runnable() {
                       @Override
                        public void run() {
                            if (continueAdding == true) {
                                continueAdding = draftOne();
                            }
                            else {
                                //fill taxi squads
                            }
                        }
                    });
                    Thread.sleep(100);
                }
                startButton.setDisable(false);
                pauseButton.setDisable(true);
                return null;
            } 
        };
        
        // THIS GETS THE THREAD ROLLING
        thread = new Thread(task);
        thread.start();
    }
    
    public void pauseDraft() {
        //kill the thread
        thread.stop();
    }
    
    public void incrementCount() {
        others++;
    }
    
    public void removePlayer(Player p) {
        ObservableList<Player> items = draftedTable.getItems();
        if (items.contains(p)){
            int n = items.indexOf(p);
            items.remove(n);
            for(int i=n;i<items.size();i++) {
                items.get(i).decrementPick();
            }
            draftedTable.setItems(items);
        }
        
    }
    
    public void addPlayer(Player p) {
        p.setPick(getNextPick());
        draftedTable.getItems().add(p);
    }
    
    public int getNextPick() {
        return draftedTable.getItems().size()+1 + others;
    }
    
    public void update(DraftDataManager dataManager) {
        // CREATE A NEW LIST
        draftedPlayers = FXCollections.observableArrayList();
        // ADD ALL THE PLAYERS WITH A PICK != -1 TO THE LIST
        ObservableList<Team> teams = dataManager.getDraft().getFantasyTeams();
        for (int i=0;i<teams.size();i++) {
            Team t = teams.get(i);
            //GET ALL STARTING PLAYERS
            for (int j=0;j<t.getRoster().size();j++) {
                if (t.getRoster().get(j).getPick() != -1)
                    draftedPlayers.add(t.getRoster().get(j));
            }
            //AND ALL TAXI PLAYERS
            for (int j=0;j<t.getTaxiSquad().size();j++){
                if (t.getTaxiSquad().get(j).getPick() != -1)
                    draftedPlayers.add(t.getTaxiSquad().get(j));
            }
        }
        // SORT THE LIST
        Collections.sort(draftedPlayers);
        // SET THE DRAFTED TABLE ITEMS TO THE NEW LIST
        draftedTable.setItems(draftedPlayers);
    }
    
}
