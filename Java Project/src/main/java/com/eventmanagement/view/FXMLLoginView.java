package com.eventmanagement.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FXMLLoginView {
    
    private Stage stage;
    
    public void show(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        // Create scene
        Scene scene = new Scene(root, 500, 400);
        
        // Set up stage
        stage.setTitle("Login - Event Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public Stage getStage() {
        return stage;
    }
}