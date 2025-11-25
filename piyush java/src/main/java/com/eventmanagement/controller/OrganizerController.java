package com.eventmanagement.controller;

import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.TicketDAO;
import com.eventmanagement.dao.MessageDAO;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Ticket;
import com.eventmanagement.model.Message;
import com.eventmanagement.view.OrganizerDashboardView;
import com.eventmanagement.view.FXMLLoginView;
import com.eventmanagement.util.DashboardRefresher;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.sql.SQLException;
import java.util.List;

public class OrganizerController {
    
    private OrganizerDashboardView view;
    private EventDAO eventDAO;
    private TicketDAO ticketDAO;
    private MessageDAO messageDAO;
    private DashboardRefresher dashboardRefresher;
    
    public OrganizerController(OrganizerDashboardView view) {
        this.view = view;
        this.eventDAO = new EventDAO();
        this.ticketDAO = new TicketDAO();
        this.messageDAO = new MessageDAO();
        
        // Initialize dashboard refresher (refresh every 30 seconds)
        this.dashboardRefresher = new DashboardRefresher(() -> {
            Platform.runLater(() -> {
                populateEventDropdowns();
                updateDashboardStats();
            });
        });
    }
    
    public void loadDashboardData() {
        refreshEventTable();
        refreshTicketTable();
        populateEventDropdowns();
        updateDashboardStats();
        
        // Start auto-refresh (every 30 seconds)
        dashboardRefresher.startAutoRefresh(30);
    }
    
    public void refreshEventTable() {
        try {
            List<Event> events = eventDAO.getEventsByOrganizer(view.getCurrentUser().getUserId());
            view.getEventTable().setItems(FXCollections.observableArrayList(events));
            
            // Also update dropdowns when events change
            populateEventDropdowns();
            updateDashboardStats();
        } catch (SQLException e) {
            showError("Error loading events", e.getMessage());
        }
    }
    
    public void refreshTicketTable() {
        // This will be called when an event is selected
        view.getTicketTable().setItems(FXCollections.observableArrayList());
    }
    
    public void populateEventDropdowns() {
        try {
            List<Event> events = eventDAO.getEventsByOrganizer(view.getCurrentUser().getUserId());
            
            // Populate ticket management dropdown
            view.getEventComboBox().setItems(FXCollections.observableArrayList(events));
            
            // Populate messaging dropdown
            view.getMessageEventComboBox().setItems(FXCollections.observableArrayList(events));
            
        } catch (SQLException e) {
            showError("Error loading events for dropdowns", e.getMessage());
        }
    }
    
    public void updateDashboardStats() {
        try {
            List<Event> events = eventDAO.getEventsByOrganizer(view.getCurrentUser().getUserId());
            
            // Count events by status
            long totalEvents = events.size();
            long approvedEvents = events.stream().filter(e -> e.getStatus() == Event.EventStatus.APPROVED).count();
            long pendingEvents = events.stream().filter(e -> e.getStatus() == Event.EventStatus.PENDING).count();
            
            // Update stats cards (you can implement this in the view if needed)
            System.out.println("Dashboard Stats - Total: " + totalEvents + ", Approved: " + approvedEvents + ", Pending: " + pendingEvents);
            
        } catch (SQLException e) {
            showError("Error updating dashboard stats", e.getMessage());
        }
    }
    
    private void createDefaultTickets(Event event) {
        try {
            // Create default ticket types
            Ticket generalTicket = new Ticket(event.getEventId(), "General Admission", 
                                            new java.math.BigDecimal("50.00"), 100);
            Ticket vipTicket = new Ticket(event.getEventId(), "VIP", 
                                        new java.math.BigDecimal("150.00"), 20);
            
            ticketDAO.createTicket(generalTicket);
            ticketDAO.createTicket(vipTicket);
            
            System.out.println("Created default tickets for event: " + event.getTitle());
        } catch (SQLException e) {
            System.err.println("Error creating default tickets: " + e.getMessage());
        }
    }
    
    public void showCreateEventDialog() {
        EventFormDialog dialog = new EventFormDialog(null, view.getCurrentUser().getUserId());
        dialog.showAndWait().ifPresent(event -> {
            try {
                if (eventDAO.createEvent(event)) {
                    // Create default ticket types for the new event
                    createDefaultTickets(event);
                    
                    showInfo("Success", "Event created successfully with default ticket types! It will be reviewed by an administrator.");
                    refreshEventTable(); // This now also updates dropdowns and stats
                } else {
                    showError("Error", "Failed to create event.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void showEditEventDialog() {
        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to edit.");
            return;
        }
        
        // Only allow editing if event is not approved yet
        if (selectedEvent.getStatus() == Event.EventStatus.APPROVED) {
            showWarning("Cannot Edit", "Cannot edit approved events. Please contact an administrator.");
            return;
        }
        
        EventFormDialog dialog = new EventFormDialog(selectedEvent, view.getCurrentUser().getUserId());
        dialog.showAndWait().ifPresent(event -> {
            try {
                if (eventDAO.updateEvent(event)) {
                    showInfo("Success", "Event updated successfully!");
                    refreshEventTable(); // This now also updates dropdowns and stats
                } else {
                    showError("Error", "Failed to update event.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void deleteSelectedEvent() {
        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showWarning("No Selection", "Please select an event to delete.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Event");
        confirmDialog.setContentText("Are you sure you want to delete event: " + selectedEvent.getTitle() + "?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (eventDAO.deleteEvent(selectedEvent.getEventId())) {
                        showInfo("Success", "Event deleted successfully!");
                        refreshEventTable(); // This now also updates dropdowns and stats
                    } else {
                        showError("Error", "Failed to delete event.");
                    }
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }
    
    public void loadTicketsForEvent(Event event) {
        if (event == null) return;
        
        try {
            List<Ticket> tickets = ticketDAO.getTicketsByEvent(event.getEventId());
            view.getTicketTable().setItems(FXCollections.observableArrayList(tickets));
        } catch (SQLException e) {
            showError("Error loading tickets", e.getMessage());
        }
    }
    
    public void showAddTicketDialog() {
        Event selectedEvent = view.getEventComboBox().getValue();
        if (selectedEvent == null) {
            showWarning("No Event Selected", "Please select an event from the dropdown first to add tickets.");
            return;
        }
        
        TicketFormDialog dialog = new TicketFormDialog(null, selectedEvent.getEventId());
        dialog.showAndWait().ifPresent(ticket -> {
            try {
                if (ticketDAO.createTicket(ticket)) {
                    showInfo("Success", "Ticket type added successfully!");
                    loadTicketsForEvent(selectedEvent);
                } else {
                    showError("Error", "Failed to add ticket type.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void showEditTicketDialog() {
        Ticket selectedTicket = view.getTicketTable().getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showWarning("No Selection", "Please select a ticket to edit.");
            return;
        }
        
        TicketFormDialog dialog = new TicketFormDialog(selectedTicket, selectedTicket.getEventId());
        dialog.showAndWait().ifPresent(ticket -> {
            try {
                if (ticketDAO.updateTicket(ticket)) {
                    showInfo("Success", "Ticket updated successfully!");
                    Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
                    if (selectedEvent != null) {
                        loadTicketsForEvent(selectedEvent);
                    }
                } else {
                    showError("Error", "Failed to update ticket.");
                }
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
    }
    
    public void deleteSelectedTicket() {
        Ticket selectedTicket = view.getTicketTable().getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showWarning("No Selection", "Please select a ticket to delete.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Ticket Type");
        confirmDialog.setContentText("Are you sure you want to delete this ticket type?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (ticketDAO.deleteTicket(selectedTicket.getTicketId())) {
                        showInfo("Success", "Ticket type deleted successfully!");
                        Event selectedEvent = view.getEventTable().getSelectionModel().getSelectedItem();
                        if (selectedEvent != null) {
                            loadTicketsForEvent(selectedEvent);
                        }
                    } else {
                        showError("Error", "Failed to delete ticket type.");
                    }
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }
    
    public void sendMessage(Event event, String title, String content) {
        if (event == null) {
            showWarning("No Event Selected", "Please select an event to send messages to its attendees.");
            return;
        }
        
        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            showWarning("Invalid Input", "Please enter both title and message content.");
            return;
        }
        
        try {
            Message message = new Message(event.getEventId(), view.getCurrentUser().getUserId(), title, content);
            if (messageDAO.createMessage(message)) {
                showInfo("Success", "Message sent to all registered attendees!");
                view.getMessageArea().clear();
            } else {
                showError("Error", "Failed to send message.");
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
        refreshEventTable(); // This also updates dropdowns and stats
        refreshTicketTable();
        populateEventDropdowns();
        updateDashboardStats();
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