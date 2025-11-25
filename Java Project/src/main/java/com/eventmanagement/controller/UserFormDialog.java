package com.eventmanagement.controller;

import com.eventmanagement.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class UserFormDialog extends Dialog<User> {
    
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField emailField;
    private TextField fullNameField;
    private ComboBox<User.UserRole> roleComboBox;
    private CheckBox activeCheckBox;
    
    public UserFormDialog(User user) {
        setTitle(user == null ? "Add New User" : "Edit User");
        setHeaderText(user == null ? "Enter user details:" : "Edit user details:");
        
        // Create the custom dialog
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        emailField = new TextField();
        emailField.setPromptText("Email");
        
        fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(User.UserRole.values());
        roleComboBox.setValue(User.UserRole.ATTENDEE);
        
        activeCheckBox = new CheckBox("Active");
        activeCheckBox.setSelected(true);
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Full Name:"), 0, 3);
        grid.add(fullNameField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleComboBox, 1, 4);
        grid.add(activeCheckBox, 1, 5);
        
        // If editing existing user, populate fields
        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            emailField.setText(user.getEmail());
            fullNameField.setText(user.getFullName());
            roleComboBox.setValue(user.getRole());
            activeCheckBox.setSelected(user.isActive());
        }
        
        dialogPane.setContent(grid);
        
        // Enable/Disable login button depending on whether a username was entered
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!isValidInput()) {
                event.consume();
                showValidationError();
            }
        });
        
        // Convert the result to a User when the OK button is clicked
        setResultConverter(new Callback<ButtonType, User>() {
            @Override
            public User call(ButtonType b) {
                if (b == ButtonType.OK) {
                    User resultUser = user != null ? user : new User();
                    resultUser.setUsername(usernameField.getText());
                    resultUser.setPassword(passwordField.getText());
                    resultUser.setEmail(emailField.getText());
                    resultUser.setFullName(fullNameField.getText());
                    resultUser.setRole(roleComboBox.getValue());
                    resultUser.setActive(activeCheckBox.isSelected());
                    return resultUser;
                }
                return null;
            }
        });
    }
    
    private boolean isValidInput() {
        return !usernameField.getText().trim().isEmpty() &&
               !passwordField.getText().trim().isEmpty() &&
               !emailField.getText().trim().isEmpty() &&
               !fullNameField.getText().trim().isEmpty() &&
               roleComboBox.getValue() != null;
    }
    
    private void showValidationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Please fill in all required fields.");
        alert.showAndWait();
    }
}