package com.eventmanagement.view;

import com.eventmanagement.controller.OrganizerController;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Ticket;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.stage.Stage;

public class OrganizerDashboardView {
    
    private Stage stage;
    private User currentUser;
    private OrganizerController controller;
    
    // UI Components
    private TabPane tabPane;
    private TableView<Event> eventTable;
    private TableView<Ticket> ticketTable;
    private TextArea messageArea;
    private LineChart<String, Number> salesChart;
    private ComboBox<Event> eventComboBox;
    private ComboBox<Event> messageEventComboBox;
    
    public OrganizerDashboardView(User user) {
        this.currentUser = user;
        this.controller = new OrganizerController(this);
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
        Tab eventManagementTab = createEventManagementTab();
        Tab ticketManagementTab = createTicketManagementTab();
        Tab communicationTab = createCommunicationTab();
        
        tabPane.getTabs().addAll(dashboardTab, eventManagementTab, ticketManagementTab, communicationTab);
        
        mainContainer.setCenter(tabPane);
        
        // Create scene
        Scene scene = new Scene(mainContainer, 1200, 800);
        
        // Create stage
        stage = new Stage();
        stage.setTitle("Organizer Dashboard - Event Management System");
        stage.setScene(scene);
        
        // Load initial data
        controller.loadDashboardData();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #27ae60;");
        
        Label titleLabel = new Label("Organizer Dashboard");
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
        
        // Sales chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        salesChart = new LineChart<>(xAxis, yAxis);
        salesChart.setTitle("Ticket Sales Over Time");
        salesChart.setPrefHeight(300);
        
        // Upcoming events list
        Label upcomingLabel = new Label("Upcoming Events");
        upcomingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ListView<Event> upcomingEventsList = new ListView<>();
        upcomingEventsList.setPrefHeight(200);
        
        content.getChildren().addAll(statsCards, salesChart, upcomingLabel, upcomingEventsList);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private HBox createStatsCards() {
        HBox statsCards = new HBox(20);
        
        // Total Events Card
        VBox totalEventsCard = createStatsCard("My Events", "0", "#3498db");
        
        // Approved Events Card
        VBox approvedEventsCard = createStatsCard("Approved Events", "0", "#2ecc71");
        
        // Total Registrations Card
        VBox totalRegistrationsCard = createStatsCard("Total Registrations", "0", "#f39c12");
        
        // Revenue Card
        VBox revenueCard = createStatsCard("Revenue", "$0", "#e74c3c");
        
        statsCards.getChildren().addAll(totalEventsCard, approvedEventsCard, totalRegistrationsCard, revenueCard);
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
    
    private Tab createEventManagementTab() {
        Tab tab = new Tab("Event Management");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button addEventButton = new Button("Create Event");
        Button editEventButton = new Button("Edit Event");
        Button deleteEventButton = new Button("Delete Event");
        Button refreshButton = new Button("Refresh");
        
        addEventButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        editEventButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        deleteEventButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        addEventButton.setOnAction(e -> controller.showCreateEventDialog());
        editEventButton.setOnAction(e -> controller.showEditEventDialog());
        deleteEventButton.setOnAction(e -> controller.deleteSelectedEvent());
        refreshButton.setOnAction(e -> controller.refreshEventTable());
        
        toolbar.getChildren().addAll(addEventButton, editEventButton, deleteEventButton, refreshButton);
        
        // Event table
        eventTable = new TableView<>();
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        
        TableColumn<Event, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        
        TableColumn<Event, String> venueCol = new TableColumn<>("Venue");
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        
        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Event, Integer> maxAttendeesCol = new TableColumn<>("Max Attendees");
        maxAttendeesCol.setCellValueFactory(new PropertyValueFactory<>("maxAttendees"));
        
        eventTable.getColumns().addAll(titleCol, dateCol, timeCol, venueCol, statusCol, maxAttendeesCol);
        
        content.getChildren().addAll(toolbar, eventTable);
        VBox.setVgrow(eventTable, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createTicketManagementTab() {
        Tab tab = new Tab("Ticket Management");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Event selection
        HBox eventSelection = new HBox(10);
        Label eventLabel = new Label("Select Event:");
        eventComboBox = new ComboBox<>();
        eventComboBox.setPromptText("Choose an event...");
        eventComboBox.setPrefWidth(300);
        eventComboBox.setOnAction(e -> controller.loadTicketsForEvent(eventComboBox.getValue()));
        
        eventSelection.getChildren().addAll(eventLabel, eventComboBox);
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button addTicketButton = new Button("Add Ticket Type");
        Button editTicketButton = new Button("Edit Ticket");
        Button deleteTicketButton = new Button("Delete Ticket");
        Button refreshButton = new Button("Refresh");
        
        addTicketButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        editTicketButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        deleteTicketButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        addTicketButton.setOnAction(e -> controller.showAddTicketDialog());
        editTicketButton.setOnAction(e -> controller.showEditTicketDialog());
        deleteTicketButton.setOnAction(e -> controller.deleteSelectedTicket());
        refreshButton.setOnAction(e -> controller.refreshTicketTable());
        
        toolbar.getChildren().addAll(addTicketButton, editTicketButton, deleteTicketButton, refreshButton);
        
        // Ticket table
        ticketTable = new TableView<>();
        
        TableColumn<Ticket, String> typeCol = new TableColumn<>("Ticket Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("ticketType"));
        
        TableColumn<Ticket, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<Ticket, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("quantityAvailable"));
        
        TableColumn<Ticket, Integer> soldCol = new TableColumn<>("Sold");
        soldCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        
        TableColumn<Ticket, Integer> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getQuantityRemaining()).asObject());
        
        ticketTable.getColumns().addAll(typeCol, priceCol, availableCol, soldCol, remainingCol);
        
        content.getChildren().addAll(eventSelection, toolbar, ticketTable);
        VBox.setVgrow(ticketTable, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createCommunicationTab() {
        Tab tab = new Tab("Communication");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Event selection for messaging
        HBox eventSelection = new HBox(10);
        Label eventLabel = new Label("Select Event:");
        messageEventComboBox = new ComboBox<>();
        messageEventComboBox.setPromptText("Choose an event...");
        messageEventComboBox.setPrefWidth(300);
        
        eventSelection.getChildren().addAll(eventLabel, messageEventComboBox);
        
        // Message form
        VBox messageForm = new VBox(10);
        
        Label titleLabel = new Label("Message Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter message title");
        
        Label contentLabel = new Label("Message Content:");
        messageArea = new TextArea();
        messageArea.setPromptText("Enter your message to attendees...");
        messageArea.setPrefRowCount(8);
        
        Button sendMessageButton = new Button("Send Message");
        sendMessageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        sendMessageButton.setOnAction(e -> controller.sendMessage(
            messageEventComboBox.getValue(), 
            titleField.getText(), 
            messageArea.getText()
        ));
        
        messageForm.getChildren().addAll(titleLabel, titleField, contentLabel, messageArea, sendMessageButton);
        
        content.getChildren().addAll(eventSelection, messageForm);
        
        tab.setContent(content);
        
        return tab;
    }
    
    public void show() {
        stage.show();
    }
    
    // Getters for controller access
    public TableView<Event> getEventTable() { return eventTable; }
    public TableView<Ticket> getTicketTable() { return ticketTable; }
    public TextArea getMessageArea() { return messageArea; }
    public LineChart<String, Number> getSalesChart() { return salesChart; }
    public ComboBox<Event> getEventComboBox() { return eventComboBox; }
    public ComboBox<Event> getMessageEventComboBox() { return messageEventComboBox; }
    public Stage getStage() { return stage; }
    public User getCurrentUser() { return currentUser; }
}