package com.eventmanagement.controller;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.model.User;
import com.eventmanagement.view.AdminDashboardView;
import com.eventmanagement.view.OrganizerDashboardView;
import com.eventmanagement.view.AttendeeDashboardView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userDAO = new UserDAO();
    }
    
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }
        
        try {
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                statusLabel.setText("Login successful!");
                
                // Open appropriate dashboard based on user role
                Platform.runLater(() -> {
                    openDashboard(user);
                    getCurrentStage().close();
                });
                
            } else {
                statusLabel.setText("Invalid username or password.");
                clearFields();
            }
            
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("");
    }
    
    private Stage getCurrentStage() {
        return (Stage) loginButton.getScene().getWindow();
    }
    
    private void openDashboard(User user) {
        try {
            switch (user.getRole()) {
                case ADMIN:
                    AdminDashboardView adminDashboard = new AdminDashboardView(user);
                    adminDashboard.show();
                    break;
                    
                case ORGANIZER:
                    OrganizerDashboardView organizerDashboard = new OrganizerDashboardView(user);
                    organizerDashboard.show();
                    break;
                    
                case ATTENDEE:
                    AttendeeDashboardView attendeeDashboard = new AttendeeDashboardView(user);
                    attendeeDashboard.show();
                    break;
                    
                default:
                    statusLabel.setText("Unknown user role.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}