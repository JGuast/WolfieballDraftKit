/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wdk.file.DraftFileManager;
import wdk.gui.wdk_GUI;

/**
 *
 * @author Guacamole
 */
public class DraftDataManager {
    ObservableList<String> proTeams;
    // THE DRAFT BEING EDITED
    Draft draft;
    
    wdk_GUI gui;
    
    // THIS IS THE UI, WHICH MUST BE UPDATED
    // WHENEVER OUR MODEL'S DATA CHANGES
    DraftDataView view;
    
    // THIS HELPS US LOAD THINGS FOR OUR COURSE
    DraftFileManager fileManager;
    
    
    public DraftDataManager(wdk_GUI initGUI) {
        //view = initView;
        initProTeams();
        gui = initGUI;
        draft = new Draft();
        
    }
    
    public void reset() {
        // CLEAR ALL DRAFT VALUES
        draft.clearTeams();
        draft.clearHitters();
        draft.clearPitchers();
        proTeams.clear();
        
        // AND FORCE THE UI TO RELOAD THE UPDATED DRAFT
        //view.reloadDraft(draft);
    }

    public ObservableList<String> getProTeams() {
        return proTeams;
    }

    public void setProTeams(ObservableList<String> proTeams) {
        this.proTeams = proTeams;
    }

    public wdk_GUI getGui() {
        return gui;
    }

    public void setGui(wdk_GUI gui) {
        this.gui = gui;
    }

    public DraftDataView getView() {
        return view;
    }

    public void setView(DraftDataView view) {
        this.view = view;
    }

    public DraftFileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(DraftFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Draft getDraft() {
        return draft;
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }
    
    private void initProTeams() {
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
    }
}
