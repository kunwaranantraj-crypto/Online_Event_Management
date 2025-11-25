package com.eventmanagement.controller;

import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.dao.RegistrationDAO;
import com.eventmanagement.dao.MessageDAO;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Registration;
import com.eventmanagement.model.Message;
import com.eventmanagement.view.AttendeeDashboardView;
import com.eventmanagement.view.FXMLLoginView;
import com.eventmanagement.util.DashboardRefresher;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.sql.SQLException;
import java.util.List;

public class AttendeeController {
    
    private AttendeeDashboardView view;
    private EventDAO eventDAO;
    private UserDAO userDAO;
    private RegistrationDAO registrationDAO;
    private MessageDAO messageDAO;
    private DashboardRefresher dashboardRefresher;
    
    public AttendeeController(AttendeeDashboardView view) {
        this.view = view;
        this.eventDAO = new EventDAO();
        this.userDAO = new UserDAO();
        this.registrationDAO = new RegistrationDAO();
        this.messageDAO = new MessageDAO();
        
        // Initialize dashboard refresher (refresh every 30 seconds)
        this.dashboardRefresher = new DashboardRefresher(() -> {
            Platform.runLater(() -> {
                refreshAvailableEvents();
                refreshRegistrations();
                refreshMessages();
            });
        });
    }
    
    public void loadDashboardData() {
        refreshAvailableEvents();
        refreshRegistrations();
        refreshMessages();
        
        // Start auto-refresh (every 30 seconds)
        dashboardRefresher.startAutoRefresh(30);
    }
    
    public void refreshAvailableEvents() {
        try {
            List<Event> events = eventDAO.getApprovedEvents();
            view.getAvailableEventsTable().setItems(FXCollections.observableArrayList(events));
        } catch (SQLException e) {
            showError("Error loading events", e.getMessage());
        }
    }
    
    public void searchEvents(String searchTerm) {
        try {
            List<Event> allEvents = eventDAO.getApprovedEvents();
            List<Event> filteredEvents = allEvents.stream()
                .filter(event -> 
                    event.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    event.getDescription().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    event.getVenue().toLowerCase().contains(searchTerm.toLowerCase())
                )
                .toList();
            
            view.getAvailableEventsTable().setItems(FXCollections.observableArrayList(filteredEvents));
        } catch (SQLException e) {
            showError("Error searching events", e.getMessage());
        }
    }
    
    public void showEventDetails() {
        Event selectedEvent = view.getAvailableEventsTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to view details.");
            return;
        }
        
        Alert detailsDialog = new Alert(Alert.AlertType.INFORMATION);
        detailsDialog.setTitle("Event Details");
        detailsDialog.setHeaderText(selectedEvent.getTitle());
        
        String details = String.format(
            "Description: %s\n\nDate: %s\nTime: %s\nVenue: %s\nMax Attendees: %d",
            selectedEvent.getDescription(),
            selectedEvent.getEventDate(),
            selectedEvent.getEventTime(),
            selectedEvent.getVenue(),
            selectedEvent.getMaxAttendees()
        );
        
        detailsDialog.setContentText(details);
        detailsDialog.showAndWait();
    }
    
    public void showRegistrationDialog() {
        Event selectedEvent = view.getAvailableEventsTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to register for.");
            return;
        }
        
        // Check if already registered
        try {
            if (registrationDAO.isUserRegistered(selectedEvent.getEventId(), view.getCurrentUser().getUserId())) {
                showWarning("Already Registered", "You are already registered for this event.");
                return;
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
            return;
        }
        
        RegistrationDialog dialog = new RegistrationDialog(selectedEvent, view.getCurrentUser().getUserId());
        dialog.showAndWait().ifPresent(registration -> {
            try {
                if (registrationDAO.createRegistration(registration)) {
                    showInfo("Success", "Registration successful! You will receive a confirmation email shortly.");
                    refreshRegistrations();
                } else {
                    showError("Error", "Failed to register for event.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void refreshRegistrations() {
        try {
            List<Registration> registrations = registrationDAO.getRegistrationsByAttendee(view.getCurrentUser().getUserId());
            view.getRegistrationTable().setItems(FXCollections.observableArrayList(registrations));
        } catch (SQLException e) {
            showError("Error loading registrations", e.getMessage());
        }
    }
    
    public void cancelSelectedRegistration() {
        Registration selectedRegistration = view.getRegistrationTable().getSelectionModel().getSelectedItem();
        if (selectedRegistration == null) {
            showWarning("No Selection", "Please select a registration to cancel.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Registration");
        confirmDialog.setHeaderText("Confirm Cancellation");
        confirmDialog.setContentText("Are you sure you want to cancel this registration?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (registrationDAO.cancelRegistration(selectedRegistration.getRegistrationId())) {
                        showInfo("Success", "Registration cancelled successfully!");
                        refreshRegistrations();
                    } else {
                        showError("Error", "Failed to cancel registration.");
                    }
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }
    
    public void refreshMessages() {
        try {
            List<Message> messages = messageDAO.getMessagesByAttendee(view.getCurrentUser().getUserId());
            view.getMessagesList().setItems(FXCollections.observableArrayList(messages));
        } catch (SQLException e) {
            showError("Error loading messages", e.getMessage());
        }
    }
    
    public void updateProfile() {
        String newName = view.getNameField().getText().trim();
        String newEmail = view.getEmailField().getText().trim();
        
        if (newName.isEmpty() || newEmail.isEmpty()) {
            showWarning("Invalid Input", "Please fill in all fields.");
            return;
        }
        
        try {
            User updatedUser = view.getCurrentUser();
            updatedUser.setFullName(newName);
            updatedUser.setEmail(newEmail);
            
            if (userDAO.updateUser(updatedUser)) {
                showInfo("Success", "Profile updated successfully!");
            } else {
                showError("Error", "Failed to update profile.");
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }
    
    public void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Logout");
        confirmDialog.setHeaderText("Confirm Logout");
        confirmDialog.setContentText("Are you sure you want to logout?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Stop dashboard refresher
                dashboardRefresher.stopAutoRefresh();
                
                view.getStage().close();
                
                Platform.runLater(() -> {
                    try {
                        FXMLLoginView loginView = new FXMLLoginView();
                        loginView.show(new javafx.stage.Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    
    public void refreshDashboard() {
        refreshAvailableEvents();
        refreshRegistrations();
        refreshMessages();
        showInfo("Dashboard Refreshed", "Dashboard data has been updated successfully!");
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}