/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.controller;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_Properties.REMOVE_PLAYER_MESSAGE;
import static wdk.base.WDK_Properties.REMOVE_TEAM_MESSAGE;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.Player;
import wdk.data.Team;
import wdk.gui.FantasyTeamDialog;
import wdk.gui.MessageDialog;
import wdk.gui.PlayerDialog;
import wdk.gui.YesNoCancelDialog;
import wdk.gui.wdk_GUI;

/**
 *
 * @author Guacamole
 */
public class PlayerEditController {
    FantasyTeamDialog teamDialog;
    PlayerDialog playerDialog;
    MessageDialog messageDialog;
    YesNoCancelDialog yncDialog;
    DraftDataManager dataManager;
    
    public PlayerEditController(wdk_GUI gui, Stage initPrimaryStage,DraftDataManager initDataManager, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        teamDialog = new FantasyTeamDialog(initPrimaryStage, initDataManager.getDraft(), initMessageDialog); 
        playerDialog = new PlayerDialog(gui, initPrimaryStage, initDataManager.getDraft(), initMessageDialog, dataManager);
        messageDialog = initMessageDialog;
        yncDialog = initYesNoCancelDialog;
        dataManager = initDataManager;
    }
    
    public void handleAddPlayerRequest(wdk_GUI gui) {
        DraftDataManager ddm = gui.getDataManager();
        Draft draft = ddm.getDraft();
        playerDialog.showAddPlayerDialog();
        
        // DID THE USER CONFIRM?
        if (playerDialog.wasCompleteSelected()) {
            // GET THE SCHEDULE ITEM
            Player p = playerDialog.getPlayer();
            
            // AND ADD IT AS A ROW TO THE TABLE
            draft.getAvailablePlayers().add(p);
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }
    }

    public void handleRemovePlayerRequest(wdk_GUI gui, Player playerToRemove, ObservableList<Player> items) {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yncDialog.show(PropertiesManager.getPropertiesManager().getProperty(REMOVE_PLAYER_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yncDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) { 
            try {
                gui.getDataManager().getDraft().removePlayer(playerToRemove);
                items.remove(playerToRemove);
                gui.getStandingsScreen().update(gui.getDataManager());
                gui.updateToolbarControls(false);
            } catch (NullPointerException e) {
                System.out.println("No Player Selected");
            }
        }
    }

    public void handleEditPlayerRequest(wdk_GUI gui, Player playerToEdit) {
        DraftDataManager ddm = gui.getDataManager();
        Draft draft = ddm.getDraft();
        //try {
        playerDialog.showEditPlayerDialog(playerToEdit, ddm);
        //} catch (Exception e){}
        
        // DID THE USER CONFIRM?
        if (playerDialog.wasCompleteSelected()) {
            // UPDATE THE PLAYER
            Player p = playerDialog.getPlayer();
            handleCompletedEdit(gui, playerToEdit, p);
            gui.getStandingsScreen().updateStats();
            //gui.getDraftScreen().incrementCount();
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }  
    }
    
    public void handleAddTeamRequest(wdk_GUI gui, Team currentTeam) {
        DraftDataManager ddm = gui.getDataManager();
        Draft draft = ddm.getDraft();
        teamDialog.showAddTeamDialog();
        
        // DID THE USER CONFIRM?
        if (teamDialog.wasCompleteSelected()) {
            // GET THE TEAM
            Team t = teamDialog.getTeam();
            
            // AND ADD IT TO THE GUI
            draft.getFantasyTeams().add(t);
            currentTeam = t;
            gui.getStandingsScreen().update(ddm);
            gui.updateToolbarControls(false);
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }
    }
    
    public void handleEditTeamRequest(wdk_GUI gui, Team teamToEdit) {
        //Do somehing would ya?
        DraftDataManager ddm = gui.getDataManager();
        Draft draft = ddm.getDraft();
        teamDialog.showEditTeamDialog(teamToEdit);
        
        // DID THE USER CONFIRM?
        if (teamDialog.wasCompleteSelected()) {
            // UPDATE THE SCHEDULE ITEM
            Team t = teamDialog.getTeam();
            teamToEdit.setName(t.getName());
            teamToEdit.setOwner(t.getOwner());
            gui.updateToolbarControls(false);
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        } 
    }
    
    public void handleRemoveTeamRequest(wdk_GUI gui, Team teamToRemove, ObservableList<Player> rosterItems, ObservableList<Player> taxiItems) {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yncDialog.show(PropertiesManager.getPropertiesManager().getProperty(REMOVE_TEAM_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yncDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) { 
            try {
                gui.getDataManager().getDraft().removeTeam(teamToRemove);
                for (int i=0;i<rosterItems.size();i++) {
                    gui.getDraftScreen().removePlayer(teamToRemove.getRoster().get(i));
                    gui.getDataManager().getDraft().getAvailablePlayers().add(rosterItems.get(i));
                }
                for (int i=0;i<taxiItems.size();i++) {
                    gui.getDraftScreen().removePlayer(taxiItems.get(i));
                    gui.getDataManager().getDraft().getAvailablePlayers().add(taxiItems.get(i));
                }
                gui.getDataManager().getDraft().getFantasyTeams().remove(teamToRemove);
                gui.updateToolbarControls(false);
            } catch (NullPointerException e) {
                System.out.println("No Team Selected");
            }
        }
    }
    
    public void handleCompletedEdit(wdk_GUI gui, Player playerToEdit, Player p) {
        
        // PLAYER ISNT CHANGING TEAMS 
        if (p.getFantasyTeam().equals(playerToEdit.getFantasyTeam())) {
            // IF THE ROSTER IS NOT FULL
            playerToEdit.setPositionInTeam(p.getPositionInTeam());
            playerToEdit.setSalary(p.getSalary());
            // IF PLAYER TO EDIT HAS S2 CONTRACT (is on draft screen)
            if (playerToEdit.getContract().equals("S2")) {
                // IF P DOES NOT HAVE AN S2 CONTRACT (needs to be removed from draft screen)
                if (p.getContract().equals("S2") == false) {
                    //then the player was on the draft screen and should be removed
                    gui.getDraftScreen().removePlayer(playerToEdit);
                    playerToEdit.setContract(p.getContract());
                }
            } //PLAYER TO EDIT DOES NOT HAVE AN S2 CONTRACT (is not on draft screen)
            else if (p.getContract().equals("S2")) {
                //P2E is going from not on draft screen to on draft screen 
                playerToEdit.setContract(p.getContract()); //should be S2
                gui.getDraftScreen().addPlayer(playerToEdit);
            }
            gui.getStandingsScreen().update(gui.getDataManager());
        } else {
        // IF PLAYER TO EDIT IS TO BE ADDED TO FREE AGENCY
        //try {
        if (p.getFantasyTeam().equals("Free Agent"))
            // IF PLAYER TO EDIT IS A FREE AGENT
            if (dataManager.getDraft().getAvailablePlayers().contains(playerToEdit)) {
                playerToEdit.setFantasyTeam("Free Agent");
                playerToEdit.setPositionInTeam("N/A");
                playerToEdit.setContract("X");
                playerToEdit.setSalary(0);
            } else {
                // IF PLAYER TO EDIT IS NOT A FREE AGENT
                Team from = dataManager.getDraft().getTeamByName(playerToEdit.getFantasyTeam());
                // IF HE IS ON THE TEAMS ROSTER
                if (from.getRoster().contains(playerToEdit)) {
                    gui.getDraftScreen().removePlayer(playerToEdit);
                    from.getRoster().remove(playerToEdit);
                    playerToEdit.setFantasyTeam("Free Agent");
                    playerToEdit.setPositionInTeam("N/A");
                    playerToEdit.setContract("X");
                    playerToEdit.setSalary(0);
                    dataManager.getDraft().getAvailablePlayers().add(playerToEdit);
                    gui.getStandingsScreen().update(gui.getDataManager());
                } else { // HE MUST BE ON THE TAXI SQUAD
                    from.getTaxiSquad().remove(playerToEdit);
                    
                    playerToEdit.setFantasyTeam("Free Agent");
                    playerToEdit.setPositionInTeam("N/A");
                    playerToEdit.setContract("X");
                    playerToEdit.setSalary(0);
                    
                    dataManager.getDraft().getAvailablePlayers().add(playerToEdit);
                }
            }
        else { // IF PLAYER TO EDIT IS BEING SENT TO A TEAM
            // IF TEAM TO HAS A FULL ROSTER
            if (dataManager.getDraft().getTeamByName(p.getFantasyTeam()).hasVacancy() == false) {
                // IF ALL ROSTERS ARE FULL AND TEAM TO HAS A TAXI OPENING
                if (dataManager.getDraft().hasTeamWithVacancy() == false 
                        && dataManager.getDraft().getTeamByName(p.getFantasyTeam()).hasTaxiVacancy()) {
                    Team to = dataManager.getDraft().getTeamByName(p.getFantasyTeam());
                    
                    playerToEdit.setFantasyTeam(p.getFantasyTeam());
                    playerToEdit.setPositionInTeam(p.getPositionInTeam());
                    playerToEdit.setContract(p.getContract());
                    playerToEdit.setSalary(p.getSalary());
                    
                    dataManager.getDraft().getAvailablePlayers().remove(playerToEdit);
                    to.getTaxiSquad().add(playerToEdit);
                }
                
            }
            else {
            // IF PLAYER TO EDIT IS A FREE AGENT
            if (dataManager.getDraft().getAvailablePlayers().contains(playerToEdit)) {
                Team to = dataManager.getDraft().getTeamByName(p.getFantasyTeam());
                
                dataManager.getDraft().getAvailablePlayers().remove(playerToEdit);
                playerToEdit.setFantasyTeam(p.getFantasyTeam());
                playerToEdit.setPositionInTeam(p.getPositionInTeam());
                playerToEdit.setContract(p.getContract());
                playerToEdit.setSalary(p.getSalary());
                //DOES HE GO INTO THE DRAFT SCREEN?
                if (playerToEdit.getContract().equals("S2")) {
                    playerToEdit.setPick(gui.getDraftScreen().getNextPick());
                    //to.getRoster().add(playerToEdit);
                    gui.getDraftScreen().addPlayer(playerToEdit);
                }
                to.getRoster().add(playerToEdit);
                gui.getStandingsScreen().update(gui.getDataManager());
            } else {
                // IF PLAYER TO EDIT IS ALREADY ON A TEAM
                Team to = dataManager.getDraft().getTeamByName(p.getFantasyTeam());
                Team from = dataManager.getDraft().getTeamByName(playerToEdit.getFantasyTeam());
                
                from.getRoster().remove(playerToEdit);
                playerToEdit.setFantasyTeam(p.getFantasyTeam());
                playerToEdit.setPositionInTeam(p.getPositionInTeam());
                playerToEdit.setContract(p.getContract());
                playerToEdit.setSalary(p.getSalary());
                if (playerToEdit.getContract().equals("S2")) {
                    gui.getDraftScreen().removePlayer(playerToEdit);
                    playerToEdit.setPick(gui.getDraftScreen().getNextPick());
                    gui.getDraftScreen().addPlayer(playerToEdit);
                } else {
                    gui.getDraftScreen().removePlayer(playerToEdit);
                }
                to.getRoster().add(playerToEdit);
                gui.getStandingsScreen().update(gui.getDataManager());
            }
        }
        }
        //}catch (NullPointerException e) {}
        }
    }
}
