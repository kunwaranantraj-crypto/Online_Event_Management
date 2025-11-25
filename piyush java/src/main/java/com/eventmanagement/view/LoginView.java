package com.eventmanagement.view;

import com.eventmanagement.controller.ProgrammaticLoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    
    private Stage stage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;
    private ProgrammaticLoginController controller;
    
    public LoginView() {
        controller = new ProgrammaticLoginController(this);
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f0f0f0;");
        
        // Title
        Label titleLabel = new Label("Event Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Login form
        GridPane loginForm = createLoginForm();
        
        // Status label
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");
        
        mainContainer.getChildren().addAll(titleLabel, loginForm, statusLabel);
        
        // Create scene
        Scene scene = new Scene(mainContainer, 400, 300);
        
        // Create stage
        stage = new Stage();
        stage.setTitle("Login - Event Management System");
        stage.setScene(scene);
        stage.setResizable(false);
    }
    
    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        // Username field
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(200);
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(200);
        
        // Login button
        loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(e -> controller.handleLogin());
        
        // Add components to grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        
        // Handle Enter key press
        passwordField.setOnAction(e -> controller.handleLogin());
        
        return grid;
    }
    
    public void show(Stage primaryStage) {
        this.stage = primaryStage;
        stage.show();
    }
    
    public String getUsername() {
        return usernameField.getText();
    }
    
    public String getPassword() {
        return passwordField.getText();
    }
    
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
    
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("");
    }
    
    public Stage getStage() {
        return stage;
    }
}