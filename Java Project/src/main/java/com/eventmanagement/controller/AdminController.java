package com.eventmanagement.controller;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Event;
import com.eventmanagement.view.AdminDashboardView;
import com.eventmanagement.view.FXMLLoginView;
import com.eventmanagement.util.DashboardRefresher;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {
    
    private AdminDashboardView view;
    private UserDAO userDAO;
    private EventDAO eventDAO;
    private DashboardRefresher dashboardRefresher;
    
    public AdminController(AdminDashboardView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.eventDAO = new EventDAO();
        
        // Initialize dashboard refresher (refresh every 30 seconds)
        this.dashboardRefresher = new DashboardRefresher(() -> {
            Platform.runLater(() -> {
                try {
                    loadUserRoleChart();
                    loadEventStatusChart();
                    refreshUserTable();
                    refreshEventTable();
                } catch (SQLException e) {
                    System.err.println("Error refreshing admin dashboard: " + e.getMessage());
                }
            });
        });
    }
    
    public void loadDashboardData() {
        try {
            loadUserRoleChart();
            loadEventStatusChart();
            refreshUserTable();
            refreshEventTable();
            refreshSystemLogs();
            
            // Start auto-refresh (every 30 seconds)
            dashboardRefresher.startAutoRefresh(30);
        } catch (SQLException e) {
            showError("Error loading dashboard data", e.getMessage());
        }
    }
    
    private void loadUserRoleChart() throws SQLException {
        List<User> users = userDAO.getAllUsers();
        Map<User.UserRole, Long> roleCount = users.stream()
            .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
        
        PieChart chart = view.getUserRoleChart();
        chart.getData().clear();
        
        for (Map.Entry<User.UserRole, Long> entry : roleCount.entrySet()) {
            PieChart.Data data = new PieChart.Data(
                entry.getKey().toString(), 
                entry.getValue()
            );
            chart.getData().add(data);
        }
    }
    
    private void loadEventStatusChart() throws SQLException {
        List<Event> events = eventDAO.getAllEvents();
        Map<Event.EventStatus, Long> statusCount = events.stream()
            .collect(Collectors.groupingBy(Event::getStatus, Collectors.counting()));
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Events");
        
        for (Map.Entry<Event.EventStatus, Long> entry : statusCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(
                entry.getKey().toString(), 
                entry.getValue()
            ));
        }
        
        view.getEventStatusChart().getData().clear();
        view.getEventStatusChart().getData().add(series);
    }
    
    public void refreshUserTable() {
        try {
            List<User> users = userDAO.getAllUsers();
            view.getUserTable().setItems(FXCollections.observableArrayList(users));
        } catch (SQLException e) {
            showError("Error loading users", e.getMessage());
        }
    }
    
    public void refreshEventTable() {
        try {
            List<Event> pendingEvents = eventDAO.getEventsByStatus(Event.EventStatus.PENDING);
            view.getEventTable().setItems(FXCollections.observableArrayList(pendingEvents));
        } catch (SQLException e) {
            showError("Error loading events", e.getMessage());
        }
    }
    
    public void showAddUserDialog() {
        UserFormDialog dialog = new UserFormDialog(null);
        dialog.showAndWait().ifPresent(user -> {
            try {
                if (userDAO.createUser(user)) {
                    showInfo("Success", "User created successfully!");
                    refreshUserTable();
                    loadUserRoleChart();
                } else {
                    showError("Error", "Failed to create user.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void showEditUserDialog() {
        User selectedUser = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showWarning("No Selection", "Please select a user to edit.");
            return;
        }
        
        UserFormDialog dialog = new UserFormDialog(selectedUser);
        dialog.showAndWait().ifPresent(user -> {
            try {
                if (userDAO.updateUser(user)) {
                    showInfo("Success", "User updated successfully!");
                    refreshUserTable();
                    loadUserRoleChart();
                } else {
                    showError("Error", "Failed to update user.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void deleteSelectedUser() {
        User selectedUser = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showWarning("No Selection", "Please select a user to delete.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete User");
        confirmDialog.setContentText("Are you sure you want to delete user: " + selectedUser.getFullName() + "?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (userDAO.deleteUser(selectedUser.getUserId())) {
                        showInfo("Success", "User deleted successfully!");
                        refreshUserTable();
                        loadUserRoleChart();
                    } else {
                        showError("Error", "Failed to delete user.");
                    }
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }
    
    public void approveSelectedEvent() {
        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to approve.");
            return;
        }
        
        try {
            if (eventDAO.updateEventStatus(selectedEvent.getEventId(), Event.EventStatus.APPROVED)) {
                showInfo("Success", "Event approved successfully!");
                refreshEventTable();
                loadEventStatusChart();
            } else {
                showError("Error", "Failed to approve event.");
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }
    
    public void rejectSelectedEvent() {
        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to reject.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Rejection");
        confirmDialog.setHeaderText("Reject Event");
        confirmDialog.setContentText("Are you sure you want to reject event: " + selectedEvent.getTitle() + "?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (eventDAO.updateEventStatus(selectedEvent.getEventId(), Event.EventStatus.REJECTED)) {
                        showInfo("Success", "Event rejected successfully!");
                        refreshEventTable();
                        loadEventStatusChart();
                    } else {
                        showError("Error", "Failed to reject event.");
                    }
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }
    
    public void showEventDetails() {
        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to view details.");
            return;
        }
        
        Alert detailsDialog = new Alert(Alert.AlertType.INFORMATION);
        detailsDialog.setTitle("Event Details");
        detailsDialog.setHeaderText(selectedEvent.getTitle());
        
        String details = String.format(
            "Description: %s\n\nDate: %s\nTime: %s\nVenue: %s\nMax Attendees: %d\nStatus: %s",
            selectedEvent.getDescription(),
            selectedEvent.getEventDate(),
            selectedEvent.getEventTime(),
            selectedEvent.getVenue(),
            selectedEvent.getMaxAttendees(),
            selectedEvent.getStatus()
        );
        
        detailsDialog.setContentText(details);
        detailsDialog.showAndWait();
    }
    
    public void refreshSystemLogs() {
        // Simulate system logs - in a real application, this would fetch from database
        String logs = "System Activity Log:\n\n" +
                     "[2024-01-15 10:30:15] User 'admin' logged in\n" +
                     "[2024-01-15 10:32:22] Event 'Tech Conference 2024' approved\n" +
                     "[2024-01-15 10:35:10] New user 'john_doe' created\n" +
                     "[2024-01-15 10:40:05] Event 'Music Festival' submitted for approval\n" +
                     "[2024-01-15 10:45:30] User 'jane_smith' updated profile\n";
        
        view.getLogArea().setText(logs);
    }
    
    public void clearSystemLogs() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Clear Logs");
        confirmDialog.setHeaderText("Clear System Logs");
        confirmDialog.setContentText("Are you sure you want to clear all system logs?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                view.getLogArea().clear();
                showInfo("Success", "System logs cleared successfully!");
            }
        });
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
        try {
            loadUserRoleChart();
            loadEventStatusChart();
            refreshUserTable();
            refreshEventTable();
            refreshSystemLogs();
            showInfo("Dashboard Refreshed", "Dashboard data has been updated successfully!");
        } catch (SQLException e) {
            showError("Error refreshing dashboard", e.getMessage());
        }
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