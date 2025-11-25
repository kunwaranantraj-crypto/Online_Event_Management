package com.eventmanagement.view;

import com.eventmanagement.controller.AttendeeController;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Registration;
import com.eventmanagement.model.Message;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AttendeeDashboardView {
    
    private Stage stage;
    private User currentUser;
    private AttendeeController controller;
    
    // UI Components
    private TabPane tabPane;
    private TableView<Event> availableEventsTable;
    private TableView<Registration> registrationTable;
    private ListView<Message> messagesList;
    private TextField nameField;
    private TextField emailField;
    
    public AttendeeDashboardView(User user) {
        this.currentUser = user;
        this.controller = new AttendeeController(this);
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Create main container
        BorderPane mainContainer = new BorderPane();
        
        // Create header
        HBox header = createHeader();
        mainContainer.setTop(header);
        
        // Create tab pane
        tabPane = new TabPane();
        
        // Create tabs
        Tab dashboardTab = createDashboardTab();
        Tab eventsTab = createEventsTab();
        Tab registrationsTab = createRegistrationsTab();
        Tab messagesTab = createMessagesTab();
        Tab profileTab = createProfileTab();
        
        tabPane.getTabs().addAll(dashboardTab, eventsTab, registrationsTab, messagesTab, profileTab);
        
        mainContainer.setCenter(tabPane);
        
        // Create scene
        Scene scene = new Scene(mainContainer, 1200, 800);
        
        // Create stage
        stage = new Stage();
        stage.setTitle("Attendee Dashboard - Event Management System");
        stage.setScene(scene);
        
        // Load initial data
        controller.loadDashboardData();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #9b59b6;");
        
        Label titleLabel = new Label("Attendee Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshDashboardButton = new Button("🔄 Refresh Dashboard");
        refreshDashboardButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshDashboardButton.setOnAction(e -> controller.refreshDashboard());
        
        Label userLabel = new Label("Welcome, " + currentUser.getFullName());
        userLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> controller.handleLogout());
        
        header.getChildren().addAll(titleLabel, spacer, refreshDashboardButton, userLabel, logoutButton);
        return header;
    }
    
    private Tab createDashboardTab() {
        Tab tab = new Tab("Dashboard");
        tab.setClosable(false);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Statistics cards
        HBox statsCards = createStatsCards();
        
        // Quick actions
        VBox quickActions = new VBox(10);
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox actionButtons = new HBox(10);
        Button browseEventsButton = new Button("Browse Events");
        Button viewRegistrationsButton = new Button("My Registrations");
        Button viewMessagesButton = new Button("Messages");
        Button updateProfileButton = new Button("Update Profile");
        
        browseEventsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-pref-width: 150;");
        viewRegistrationsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-pref-width: 150;");
        viewMessagesButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-pref-width: 150;");
        updateProfileButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-pref-width: 150;");
        
        browseEventsButton.setOnAction(e -> tabPane.getSelectionModel().select(1));
        viewRegistrationsButton.setOnAction(e -> tabPane.getSelectionModel().select(2));
        viewMessagesButton.setOnAction(e -> tabPane.getSelectionModel().select(3));
        updateProfileButton.setOnAction(e -> tabPane.getSelectionModel().select(4));
        
        actionButtons.getChildren().addAll(browseEventsButton, viewRegistrationsButton, viewMessagesButton, updateProfileButton);
        quickActions.getChildren().addAll(quickActionsLabel, actionButtons);
        
        // Upcoming events preview
        Label upcomingLabel = new Label("My Upcoming Events");
        upcomingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ListView<Event> upcomingEventsList = new ListView<>();
        upcomingEventsList.setPrefHeight(200);
        
        content.getChildren().addAll(statsCards, quickActions, upcomingLabel, upcomingEventsList);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private HBox createStatsCards() {
        HBox statsCards = new HBox(20);
        
        // Total Registrations Card
        VBox totalRegistrationsCard = createStatsCard("My Registrations", "0", "#3498db");
        
        // Upcoming Events Card
        VBox upcomingEventsCard = createStatsCard("Upcoming Events", "0", "#2ecc71");
        
        // Messages Card
        VBox messagesCard = createStatsCard("New Messages", "0", "#f39c12");
        
        // Total Spent Card
        VBox totalSpentCard = createStatsCard("Total Spent", "$0", "#e74c3c");
        
        statsCards.getChildren().addAll(totalRegistrationsCard, upcomingEventsCard, messagesCard, totalSpentCard);
        return statsCards;
    }
    
    private VBox createStatsCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(200);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private Tab createEventsTab() {
        Tab tab = new Tab("Browse Events");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Search and filter
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        searchField.setPrefWidth(300);
        
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        
        searchButton.setOnAction(e -> controller.searchEvents(searchField.getText()));
        refreshButton.setOnAction(e -> controller.refreshAvailableEvents());
        
        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton, refreshButton);
        
        // Events table
        availableEventsTable = new TableView<>();
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        
        TableColumn<Event, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        
        TableColumn<Event, String> venueCol = new TableColumn<>("Venue");
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        venueCol.setPrefWidth(150);
        
        TableColumn<Event, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(250);
        
        availableEventsTable.getColumns().addAll(titleCol, dateCol, timeCol, venueCol, descriptionCol);
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        Button registerButton = new Button("Register for Event");
        Button viewDetailsButton = new Button("View Details");
        
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        registerButton.setOnAction(e -> controller.showRegistrationDialog());
        viewDetailsButton.setOnAction(e -> controller.showEventDetails());
        
        actionButtons.getChildren().addAll(registerButton, viewDetailsButton);
        
        content.getChildren().addAll(searchBox, availableEventsTable, actionButtons);
        VBox.setVgrow(availableEventsTable, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createRegistrationsTab() {
        Tab tab = new Tab("My Registrations");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button refreshButton = new Button("Refresh");
        Button cancelRegistrationButton = new Button("Cancel Registration");
        
        cancelRegistrationButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        refreshButton.setOnAction(e -> controller.refreshRegistrations());
        cancelRegistrationButton.setOnAction(e -> controller.cancelSelectedRegistration());
        
        toolbar.getChildren().addAll(refreshButton, cancelRegistrationButton);
        
        // Registrations table
        registrationTable = new TableView<>();
        
        TableColumn<Registration, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("eventId")); // This would need custom cell factory
        
        TableColumn<Registration, String> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        
        TableColumn<Registration, String> statusCol = new TableColumn<>("Payment Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        
        TableColumn<Registration, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        
        registrationTable.getColumns().addAll(eventCol, dateCol, statusCol, amountCol);
        
        content.getChildren().addAll(toolbar, registrationTable);
        VBox.setVgrow(registrationTable, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createMessagesTab() {
        Tab tab = new Tab("Messages");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button refreshButton = new Button("Refresh Messages");
        Button markReadButton = new Button("Mark as Read");
        
        refreshButton.setOnAction(e -> controller.refreshMessages());
        
        toolbar.getChildren().addAll(refreshButton, markReadButton);
        
        // Messages list
        messagesList = new ListView<>();
        messagesList.setCellFactory(listView -> new MessageListCell());
        
        content.getChildren().addAll(toolbar, messagesList);
        VBox.setVgrow(messagesList, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createProfileTab() {
        Tab tab = new Tab("Profile");
        tab.setClosable(false);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Profile form
        GridPane profileForm = new GridPane();
        profileForm.setHgap(10);
        profileForm.setVgap(15);
        profileForm.setPadding(new Insets(20));
        profileForm.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        Label profileLabel = new Label("Profile Information");
        profileLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        nameField = new TextField();
        nameField.setText(currentUser.getFullName());
        nameField.setPrefWidth(300);
        
        emailField = new TextField();
        emailField.setText(currentUser.getEmail());
        emailField.setPrefWidth(300);
        
        Label usernameLabel = new Label("Username: " + currentUser.getUsername());
        usernameLabel.setStyle("-fx-font-weight: bold;");
        
        Button updateButton = new Button("Update Profile");
        updateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        updateButton.setOnAction(e -> controller.updateProfile());
        
        profileForm.add(new Label("Full Name:"), 0, 0);
        profileForm.add(nameField, 1, 0);
        profileForm.add(new Label("Email:"), 0, 1);
        profileForm.add(emailField, 1, 1);
        profileForm.add(usernameLabel, 0, 2, 2, 1);
        profileForm.add(updateButton, 1, 3);
        
        content.getChildren().addAll(profileLabel, profileForm);
        
        tab.setContent(content);
        
        return tab;
    }
    
    // Custom cell for message display
    private class MessageListCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);
            
            if (empty || message == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox messageBox = new VBox(5);
                messageBox.setPadding(new Insets(10));
                messageBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");
                
                Label titleLabel = new Label(message.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                
                Label contentLabel = new Label(message.getContent());
                contentLabel.setWrapText(true);
                
                Label dateLabel = new Label("Sent: " + message.getSentAt().toString());
                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
                
                messageBox.getChildren().addAll(titleLabel, contentLabel, dateLabel);
                setGraphic(messageBox);
            }
        }
    }
    
    public void show() {
        stage.show();
    }
    
    // Getters for controller access
    public TableView<Event> getAvailableEventsTable() { return availableEventsTable; }
    public TableView<Registration> getRegistrationTable() { return registrationTable; }
    public ListView<Message> getMessagesList() { return messagesList; }
    public TextField getNameField() { return nameField; }
    public TextField getEmailField() { return emailField; }
    public Stage getStage() { return stage; }
    public User getCurrentUser() { return currentUser; }
}