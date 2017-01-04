/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.base;

import wdk.file.JsonDraftFileManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import wdk.gui.wdk_GUI;
import properties_manager.PropertiesManager;
import static wdk.base.WDK_StartupConstants.PATH_DATA;
import static wdk.base.WDK_StartupConstants.PROPERTIES_FILE_NAME;
import static wdk.base.WDK_StartupConstants.PROPERTIES_SCHEMA_FILE_NAME;
import static wdk.base.WDK_Properties.PROP_APP_TITLE;
import wdk.data.DraftDataManager;
import xml_utilities.InvalidXMLFileFormatException;

/**
 *
 * @author Guacamole
 */
public class WolfieballDraftKit extends Application {
    wdk_GUI gui;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        loadProperties();
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String appTitle = props.getProperty(PROP_APP_TITLE);
        
        JsonDraftFileManager jsonManager = new JsonDraftFileManager();
        
        //ArrayList<Hitter> hitters = jsonManager.loadHitters(JSON_FILE_PATH_HITTERS);
        //ArrayList<Pitcher> pitchers = jsonManager.loadPitchers(JSON_FILE_PATH_PITCHERS);
        
        gui = new wdk_GUI(primaryStage, jsonManager);
        
        // CONSTRUCT THE DATA MANAGER AND GIVE IT TO THE GUI
        DraftDataManager dataManager = new DraftDataManager(gui); 
        gui.setDataManager(dataManager);
        gui.setDraftFileManager(jsonManager);
        
        gui.initGUI(appTitle);
    }
    
    public boolean loadProperties() {
        try {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(PropertiesManager.DATA_PATH_PROPERTY, PATH_DATA);
            props.loadProperties(PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            return true;
       } catch (InvalidXMLFileFormatException ixmlffe) {
            // SOMETHING WENT WRONG INITIALIZING THE XML FILE
            /*ErrorHandler eH = ErrorHandler.getErrorHandler();
            eH.handlePropertiesFileError();*/
            return false;
        }        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
