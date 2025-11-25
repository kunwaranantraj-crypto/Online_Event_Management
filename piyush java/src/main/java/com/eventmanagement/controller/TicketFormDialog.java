package com.eventmanagement.controller;

import com.eventmanagement.model.Ticket;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.math.BigDecimal;

public class TicketFormDialog extends Dialog<Ticket> {
    
    private TextField ticketTypeField;
    private TextField priceField;
    private TextField quantityField;
    private int eventId;
    
    public TicketFormDialog(Ticket ticket, int eventId) {
        this.eventId = eventId;
        
        setTitle(ticket == null ? "Add Ticket Type" : "Edit Ticket Type");
        setHeaderText(ticket == null ? "Enter ticket details:" : "Edit ticket details:");
        
        // Create the custom dialog
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ticketTypeField = new TextField();
        ticketTypeField.setPromptText("e.g., General Admission, VIP, Premium");
        ticketTypeField.setPrefWidth(250);
        
        priceField = new TextField();
        priceField.setPromptText("e.g., 50.00");
        priceField.setPrefWidth(250);
        
        quantityField = new TextField();
        quantityField.setPromptText("e.g., 100");
        quantityField.setPrefWidth(250);
        
        grid.add(new Label("Ticket Type:"), 0, 0);
        grid.add(ticketTypeField, 1, 0);
        grid.add(new Label("Price ($):"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity Available:"), 0, 2);
        grid.add(quantityField, 1, 2);
        
        // If editing existing ticket, populate fields
        if (ticket != null) {
            ticketTypeField.setText(ticket.getTicketType());
            priceField.setText(ticket.getPrice().toString());
            quantityField.setText(String.valueOf(ticket.getQuantityAvailable()));
        }
        
        dialogPane.setContent(grid);
        
        // Enable/Disable OK button depending on whether required fields are filled
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, actionEvent -> {
            if (!isValidInput()) {
                actionEvent.consume();
                showValidationError();
            }
        });
        
        // Convert the result to a Ticket when the OK button is clicked
        setResultConverter(new Callback<ButtonType, Ticket>() {
            @Override
            public Ticket call(ButtonType b) {
                if (b == ButtonType.OK) {
                    try {
                        Ticket resultTicket = ticket != null ? ticket : new Ticket();
                        resultTicket.setEventId(eventId);
                        resultTicket.setTicketType(ticketTypeField.getText().trim());
                        resultTicket.setPrice(new BigDecimal(priceField.getText().trim()));
                        resultTicket.setQuantityAvailable(Integer.parseInt(quantityField.getText().trim()));
                        return resultTicket;
                    } catch (Exception e) {
                        showValidationError();
                        return null;
                    }
                }
                return null;
            }
        });
    }
    
    private boolean isValidInput() {
        try {
            return !ticketTypeField.getText().trim().isEmpty() &&
                   !priceField.getText().trim().isEmpty() &&
                   !quantityField.getText().trim().isEmpty() &&
                   new BigDecimal(priceField.getText().trim()).compareTo(BigDecimal.ZERO) >= 0 &&
                   Integer.parseInt(quantityField.getText().trim()) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void showValidationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Please fill in all required fields correctly.\n" +
                           "- Ticket type cannot be empty\n" +
                           "- Price must be a valid number (0 or greater)\n" +
                           "- Quantity must be a positive number");
        alert.showAndWait();
    }
}