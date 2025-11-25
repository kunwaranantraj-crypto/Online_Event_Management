package com.eventmanagement.controller;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.model.User;
import com.eventmanagement.view.LoginView;
import com.eventmanagement.view.AdminDashboardView;
import com.eventmanagement.view.OrganizerDashboardView;
import com.eventmanagement.view.AttendeeDashboardView;
import javafx.application.Platform;

public class ProgrammaticLoginController {
    
    private LoginView loginView;
    private UserDAO userDAO;
    
    public ProgrammaticLoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();
    }
    
    public void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            loginView.setStatusMessage("Please enter both username and password.");
            return;
        }
        
        try {
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                loginView.setStatusMessage("Login successful!");
                
                // Open appropriate dashboard based on user role
                Platform.runLater(() -> {
                    openDashboard(user);
                    loginView.getStage().close();
                });
                
            } else {
                loginView.setStatusMessage("Invalid username or password.");
                loginView.clearFields();
            }
            
        } catch (Exception e) {
            loginView.setStatusMessage("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
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
                    loginView.setStatusMessage("Unknown user role.");
            }
        } catch (Exception e) {
            loginView.setStatusMessage("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}