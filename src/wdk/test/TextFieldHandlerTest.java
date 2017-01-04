/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Guacamole
 */
public class TextFieldHandlerTest extends Application {
    
    public static void main(String[]args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox box = new VBox();
        TextField textA = new TextField();
        TextField textB = new TextField();
        box.getChildren().add(textA);
        box.getChildren().add(textB);
        
        
        textA.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            textB.setText(newValue);
        });
        
        Scene primaryScene = new Scene(box);
        
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        
        
    }
}
