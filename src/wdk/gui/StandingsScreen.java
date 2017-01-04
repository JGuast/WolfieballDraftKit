/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.gui;

import java.util.Collections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Team;
import static wdk.gui.wdk_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.wdk_GUI.CLASS_SCREEN_PANE;

/**
 *
 * @author Guacamole
 */
public class StandingsScreen {
    Stage primaryStage;
    // DATA TO LOAD INTO THE SCREEN
    Draft draft;
    wdk_GUI gui;
    DraftDataManager dataManager;
    
    // PANE TO HOLD CONTROLS AND LABEL FOR THE HEADER
    VBox screenPane;
    Label screenHeaderLabel;
    
    // STANDINGS TABLE
    TableView standingsTable;
    // columns for each stat
    TableColumn nameColumn;
    TableColumn playersNeededColumn;
    TableColumn moneyColumn;
    TableColumn perPlayerColumn;
    TableColumn runsColumn;
    TableColumn homerunsColumn;
    TableColumn rbiColumn;
    TableColumn stolenBasesColumn;
    TableColumn battingAvgColumn;
    TableColumn winsColumn;
    TableColumn savesColumn;
    TableColumn strikeoutsColumn;
    TableColumn eraColumn;
    TableColumn whipColumn;
    TableColumn pointsColumn;
    
    
    @SuppressWarnings("Convert2Diamond")
    public StandingsScreen(wdk_GUI initGUI, Stage initPrimaryStage, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        primaryStage = initPrimaryStage;
        gui = initGUI;
        dataManager = initGUI.getDataManager();
        draft = dataManager.getDraft();  //use a parameter to pass the list of teams here
        screenHeaderLabel = new Label("Fantasy Standings Estimates"); //TEAMS_HEADING_LABEL = Fantasy Teams
        screenHeaderLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        screenPane = new VBox();       //"screen_pane"
        screenPane.getStyleClass().add(CLASS_SCREEN_PANE);
        screenPane.setPadding(new Insets(30, 20, 20, 20));
        
        // INITIALIZE TABLE
        standingsTable = new TableView();
        
        // AND TABLE COLUMNS
        nameColumn = new TableColumn("Team Name");
        playersNeededColumn = new TableColumn("Players Needed");
        moneyColumn = new TableColumn("$ Left");
        perPlayerColumn = new TableColumn("$/Player");
        runsColumn = new TableColumn("R");
        homerunsColumn = new TableColumn("HR");
        rbiColumn = new TableColumn("RBI");
        stolenBasesColumn = new TableColumn("SB");
        battingAvgColumn = new TableColumn("BA");
        winsColumn = new TableColumn("W");
        savesColumn = new TableColumn("SV");
        strikeoutsColumn = new TableColumn("K");
        eraColumn = new TableColumn("ERA");
        whipColumn = new TableColumn("WHIP");
        pointsColumn = new TableColumn("Total Points");
        
        // LINK THEM WITH THE DATA
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        playersNeededColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("playersNeeded"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("money"));
        perPlayerColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("moneyPerPlayer"));
        runsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("runs"));
        homerunsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("homeruns"));
        rbiColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("rbi"));
        stolenBasesColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("stolenBases"));
        battingAvgColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("battingAvg"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("wins"));
        savesColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("saves"));
        strikeoutsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("strikeouts"));
        eraColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("era"));
        whipColumn.setCellValueFactory(new PropertyValueFactory<Double, String>("whip"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<Integer, String>("points"));
        
        //  ADD COLUMNS TO THE TABLE
        standingsTable.getColumns().add(nameColumn);
        standingsTable.getColumns().add(playersNeededColumn);
        standingsTable.getColumns().add(moneyColumn);
        standingsTable.getColumns().add(perPlayerColumn);
        standingsTable.getColumns().add(runsColumn);
        standingsTable.getColumns().add(homerunsColumn);
        standingsTable.getColumns().add(rbiColumn);
        standingsTable.getColumns().add(stolenBasesColumn);
        standingsTable.getColumns().add(battingAvgColumn);
        standingsTable.getColumns().add(winsColumn);
        standingsTable.getColumns().add(savesColumn);
        standingsTable.getColumns().add(strikeoutsColumn);
        standingsTable.getColumns().add(eraColumn);
        standingsTable.getColumns().add(whipColumn);
        standingsTable.getColumns().add(pointsColumn);
        
        //pointsColumn.setSortType(TableColumn.SortType.ASCENDING);
        
        // ADD CONTROLS TO THE SCREEN
        screenPane.getChildren().add(screenHeaderLabel);
        screenPane.getChildren().add(standingsTable);
    }
    
    public void updateStats() {
        ObservableList<Team> teams = standingsTable.getItems();
        for (int i=0;i<teams.size();i++) {
            teams.get(i).calculateAggregateStats();
        }
        // calculate points for each team
        //dataManager.getDraft().calculatePointTotals();
        crunchNumbers(teams);
        
        // sort teams by points
        Collections.sort(teams);
        
        standingsTable.setItems(teams);
    }
    
    public VBox getScreenPaneForShit() {
        return screenPane;
    }
    
    public void crunchNumbers(ObservableList<Team> teams) {
        for (int i=0;i<teams.size();i++) {
            Team t1 = teams.get(i);
            //R, HR, RBI, SB, BA, W, SV, K, ERA, WHIP
            int r = 1; int hr = 1; int rbi = 1; int sb = 1; int ba = 1; 
            int w = 1; int sv = 1; int k = 1; int era = 1; int whip = 1;
            for (int j=0;j<teams.size();j++) {
                Team t2 = teams.get(j);
                if (teams.get(i).equals(teams.get(j)) == false) {
                    //compare all stats
                    if (t1.getRuns() >= t2.getRuns())
                        r++;
                    if (t1.getHomeruns() >= t2.getHomeruns())
                        hr++;
                    if (t1.getRbi() >= t2.getRbi())
                        rbi++;
                    if (t1.getStolenBases() >= t2.getStolenBases())
                        sb++;
                    if (t1.getBattingAvg() >= t2.getBattingAvg())
                        ba++;
                    if (t1.getWins() >= t2.getWins())
                        w++;
                    if (t1.getSaves() >= t2.getSaves())
                        sv++;
                    if (t1.getStrikeouts() >= t2.getStrikeouts())
                        k++;
                    if (t1.getEra() <= t2.getEra()) 
                        era++;
                    if (t1.getWhip() <= t2.getWhip())
                        whip++;
                }
            }
            int sum = r+hr+rbi+sb+ba+w+sv+k+era+whip;
            teams.get(i).setPoints(sum);
        }
    }
    
    public void update(DraftDataManager d) {
        dataManager = d;
        draft = dataManager.getDraft();
        for (int i=0;i<d.getDraft().getFantasyTeams().size();i++) {
            d.getDraft().getFantasyTeams().get(i).calculateAggregateStats();
        }
        standingsTable.setItems(d.getDraft().getFantasyTeams());
    }

    public TableView getStandingsTable() {
        return standingsTable;
    }

    public void setStandingsTable(TableView standingsTable) {
        this.standingsTable = standingsTable;
    }
    
    
}
