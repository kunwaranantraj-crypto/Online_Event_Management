package com.eventmanagement.controller;

import com.eventmanagement.dao.TicketDAO;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Ticket;
import com.eventmanagement.model.Registration;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class RegistrationDialog extends Dialog<Registration> {
    
    private Event event;
    private int attendeeId;
    private ComboBox<Ticket> ticketComboBox;
    private Label priceLabel;
    private CheckBox paymentConfirmationBox;
    private TicketDAO ticketDAO;
    
    public RegistrationDialog(Event event, int attendeeId) {
        this.event = event;
        this.attendeeId = attendeeId;
        this.ticketDAO = new TicketDAO();
        
        setTitle("Register for Event");
        setHeaderText("Register for: " + event.getTitle());
        
        // Create the custom dialog
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Event details
        VBox eventDetails = new VBox(5);
        eventDetails.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label eventTitle = new Label(event.getTitle());
        eventTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label eventDate = new Label("Date: " + event.getEventDate() + " at " + event.getEventTime());
        Label eventVenue = new Label("Venue: " + event.getVenue());
        Label eventDescription = new Label("Description: " + event.getDescription());
        eventDescription.setWrapText(true);
        
        eventDetails.getChildren().addAll(eventTitle, eventDate, eventVenue, eventDescription);
        
        // Ticket selection
        GridPane ticketSelection = new GridPane();
        ticketSelection.setHgap(10);
        ticketSelection.setVgap(10);
        
        Label ticketLabel = new Label("Select Ticket Type:");
        ticketComboBox = new ComboBox<>();
        ticketComboBox.setPrefWidth(300);
        ticketComboBox.setOnAction(e -> updatePriceLabel());
        
        Label priceLabelText = new Label("Price:");
        priceLabel = new Label("$0.00");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ticketSelection.add(ticketLabel, 0, 0);
        ticketSelection.add(ticketComboBox, 1, 0);
        ticketSelection.add(priceLabelText, 0, 1);
        ticketSelection.add(priceLabel, 1, 1);
        
        // Payment simulation
        VBox paymentSection = new VBox(10);
        paymentSection.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label paymentTitle = new Label("Payment Simulation");
        paymentTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label paymentInfo = new Label("This is a simulated payment process. In a real application, this would integrate with a payment gateway.");
        paymentInfo.setWrapText(true);
        paymentInfo.setStyle("-fx-font-style: italic;");
        
        paymentConfirmationBox = new CheckBox("I confirm payment and agree to the terms and conditions");
        
        paymentSection.getChildren().addAll(paymentTitle, paymentInfo, paymentConfirmationBox);
        
        content.getChildren().addAll(eventDetails, ticketSelection, paymentSection);
        
        // Load available tickets
        loadAvailableTickets();
        
        dialogPane.setContent(content);
        
        // Enable/Disable OK button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, actionEvent -> {
            if (!isValidInput()) {
                actionEvent.consume();
                showValidationError();
            }
        });
        
        // Convert the result to a Registration when the OK button is clicked
        setResultConverter(new Callback<ButtonType, Registration>() {
            @Override
            public Registration call(ButtonType b) {
                if (b == ButtonType.OK) {
                    Ticket selectedTicket = ticketComboBox.getValue();
                    if (selectedTicket != null) {
                        Registration registration = new Registration(
                            event.getEventId(),
                            attendeeId,
                            selectedTicket.getTicketId(),
                            selectedTicket.getPrice()
                        );
                        registration.setPaymentStatus(Registration.PaymentStatus.COMPLETED);
                        return registration;
                    }
                }
                return null;
            }
        });
    }
    
    private void loadAvailableTickets() {
        try {
            List<Ticket> availableTickets = ticketDAO.getAvailableTickets(event.getEventId());
            System.out.println("Loading tickets for event ID: " + event.getEventId() + ", found: " + availableTickets.size() + " tickets");
            
            ticketComboBox.getItems().clear();
            ticketComboBox.getItems().addAll(availableTickets);
            
            if (!availableTickets.isEmpty()) {
                ticketComboBox.setValue(availableTickets.get(0));
                updatePriceLabel();
                System.out.println("Selected first ticket: " + availableTickets.get(0).getTicketType());
            } else {
                System.out.println("No tickets available for this event");
                priceLabel.setText("No tickets available");
            }
        } catch (SQLException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
            showError("Error loading tickets", e.getMessage());
        }
    }
    
    private void updatePriceLabel() {
        Ticket selectedTicket = ticketComboBox.getValue();
        if (selectedTicket != null) {
            priceLabel.setText("$" + selectedTicket.getPrice().toString());
        }
    }
    
    private boolean isValidInput() {
        return ticketComboBox.getValue() != null && paymentConfirmationBox.isSelected();
    }
    
    private void showValidationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Please select a ticket type and confirm payment.");
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}