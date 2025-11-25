package com.eventmanagement;

import com.eventmanagement.util.DatabaseConfig;
import com.eventmanagement.view.FXMLLoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection
            DatabaseConfig.initializeDatabase();
            
            // Launch FXML-based login view
            FXMLLoginView loginView = new FXMLLoginView();
            loginView.show(primaryStage);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}