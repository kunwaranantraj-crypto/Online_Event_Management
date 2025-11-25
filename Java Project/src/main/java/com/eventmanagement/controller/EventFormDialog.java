package com.eventmanagement.controller;

import com.eventmanagement.model.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventFormDialog extends Dialog<Event> {
    
    private TextField titleField;
    private TextArea descriptionArea;
    private DatePicker datePicker;
    private TextField timeField;
    private TextField venueField;
    private TextField maxAttendeesField;
    private int organizerId;
    
    public EventFormDialog(Event event, int organizerId) {
        this.organizerId = organizerId;
        
        setTitle(event == null ? "Create New Event" : "Edit Event");
        setHeaderText(event == null ? "Enter event details:" : "Edit event details:");
        
        // Create the custom dialog
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        titleField = new TextField();
        titleField.setPromptText("Event Title");
        titleField.setPrefWidth(300);
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Event Description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setPrefWidth(300);
        
        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setValue(LocalDate.now().plusDays(1));
        
        timeField = new TextField();
        timeField.setPromptText("HH:MM (24-hour format)");
        timeField.setText("09:00");
        
        venueField = new TextField();
        venueField.setPromptText("Event Venue");
        venueField.setPrefWidth(300);
        
        maxAttendeesField = new TextField();
        maxAttendeesField.setPromptText("Maximum Attendees");
        maxAttendeesField.setText("100");
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("Venue:"), 0, 4);
        grid.add(venueField, 1, 4);
        grid.add(new Label("Max Attendees:"), 0, 5);
        grid.add(maxAttendeesField, 1, 5);
        
        // If editing existing event, populate fields
        if (event != null) {
            titleField.setText(event.getTitle());
            descriptionArea.setText(event.getDescription());
            datePicker.setValue(event.getEventDate());
            timeField.setText(event.getEventTime().toString());
            venueField.setText(event.getVenue());
            maxAttendeesField.setText(String.valueOf(event.getMaxAttendees()));
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
        
        // Convert the result to an Event when the OK button is clicked
        setResultConverter(new Callback<ButtonType, Event>() {
            @Override
            public Event call(ButtonType b) {
                if (b == ButtonType.OK) {
                    try {
                        Event resultEvent = event != null ? event : new Event();
                        resultEvent.setTitle(titleField.getText().trim());
                        resultEvent.setDescription(descriptionArea.getText().trim());
                        resultEvent.setEventDate(datePicker.getValue());
                        resultEvent.setEventTime(LocalTime.parse(timeField.getText().trim()));
                        resultEvent.setVenue(venueField.getText().trim());
                        resultEvent.setMaxAttendees(Integer.parseInt(maxAttendeesField.getText().trim()));
                        resultEvent.setOrganizerId(organizerId);
                        return resultEvent;
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
            return !titleField.getText().trim().isEmpty() &&
                   !descriptionArea.getText().trim().isEmpty() &&
                   datePicker.getValue() != null &&
                   !timeField.getText().trim().isEmpty() &&
                   !venueField.getText().trim().isEmpty() &&
                   !maxAttendeesField.getText().trim().isEmpty() &&
                   LocalTime.parse(timeField.getText().trim()) != null &&
                   Integer.parseInt(maxAttendeesField.getText().trim()) > 0 &&
                   datePicker.getValue().isAfter(LocalDate.now().minusDays(1));
        } catch (Exception e) {
            return false;
        }
    }
    
    private void showValidationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Please fill in all required fields correctly.\n" +
                           "- Time format: HH:MM (e.g., 09:00, 14:30)\n" +
                           "- Max attendees must be a positive number\n" +
                           "- Date must be today or in the future");
        alert.showAndWait();
    }
}