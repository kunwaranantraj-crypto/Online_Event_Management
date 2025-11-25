package com.eventmanagement.view;

import com.eventmanagement.controller.AdminController;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminDashboardView {
    
    private Stage stage;
    private User currentUser;
    private AdminController controller;
    
    // UI Components
    private TabPane tabPane;
    private TableView<User> userTable;
    private TableView<Event> eventTable;
    private TextArea logArea;
    private PieChart userRoleChart;
    private BarChart<String, Number> eventStatusChart;
    
    public AdminDashboardView(User user) {
        this.currentUser = user;
        this.controller = new AdminController(this);
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
        Tab userManagementTab = createUserManagementTab();
        Tab eventApprovalTab = createEventApprovalTab();
        Tab systemLogsTab = createSystemLogsTab();
        
        tabPane.getTabs().addAll(dashboardTab, userManagementTab, eventApprovalTab, systemLogsTab);
        
        mainContainer.setCenter(tabPane);
        
        // Create scene
        Scene scene = new Scene(mainContainer, 1200, 800);
        
        // Create stage
        stage = new Stage();
        stage.setTitle("Admin Dashboard - Event Management System");
        stage.setScene(scene);
        
        // Load initial data
        controller.loadDashboardData();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label titleLabel = new Label("Admin Dashboard");
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
        
        // Charts
        HBox charts = new HBox(20);
        
        // User role distribution chart
        userRoleChart = new PieChart();
        userRoleChart.setTitle("User Role Distribution");
        userRoleChart.setPrefSize(400, 300);
        
        // Event status chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        eventStatusChart = new BarChart<>(xAxis, yAxis);
        eventStatusChart.setTitle("Events by Status");
        eventStatusChart.setPrefSize(400, 300);
        
        charts.getChildren().addAll(userRoleChart, eventStatusChart);
        
        content.getChildren().addAll(statsCards, charts);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private HBox createStatsCards() {
        HBox statsCards = new HBox(20);
        
        // Total Users Card
        VBox totalUsersCard = createStatsCard("Total Users", "0", "#3498db");
        
        // Total Events Card
        VBox totalEventsCard = createStatsCard("Total Events", "0", "#2ecc71");
        
        // Pending Approvals Card
        VBox pendingApprovalsCard = createStatsCard("Pending Approvals", "0", "#f39c12");
        
        // Total Revenue Card
        VBox totalRevenueCard = createStatsCard("Total Revenue", "$0", "#e74c3c");
        
        statsCards.getChildren().addAll(totalUsersCard, totalEventsCard, pendingApprovalsCard, totalRevenueCard);
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
    
    private Tab createUserManagementTab() {
        Tab tab = new Tab("User Management");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button addUserButton = new Button("Add User");
        Button editUserButton = new Button("Edit User");
        Button deleteUserButton = new Button("Delete User");
        Button refreshButton = new Button("Refresh");
        
        addUserButton.setOnAction(e -> controller.showAddUserDialog());
        editUserButton.setOnAction(e -> controller.showEditUserDialog());
        deleteUserButton.setOnAction(e -> controller.deleteSelectedUser());
        refreshButton.setOnAction(e -> controller.refreshUserTable());
        
        toolbar.getChildren().addAll(addUserButton, editUserButton, deleteUserButton, refreshButton);
        
        // User table
        userTable = new TableView<>();
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        TableColumn<User, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        
        userTable.getColumns().addAll(usernameCol, fullNameCol, emailCol, roleCol, activeCol);
        
        content.getChildren().addAll(toolbar, userTable);
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createEventApprovalTab() {
        Tab tab = new Tab("Event Approvals");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");
        Button viewDetailsButton = new Button("View Details");
        Button refreshButton = new Button("Refresh");
        
        approveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        rejectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        approveButton.setOnAction(e -> controller.approveSelectedEvent());
        rejectButton.setOnAction(e -> controller.rejectSelectedEvent());
        viewDetailsButton.setOnAction(e -> controller.showEventDetails());
        refreshButton.setOnAction(e -> controller.refreshEventTable());
        
        toolbar.getChildren().addAll(approveButton, rejectButton, viewDetailsButton, refreshButton);
        
        // Event table
        eventTable = new TableView<>();
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Event, String> organizerCol = new TableColumn<>("Organizer");
        organizerCol.setCellValueFactory(new PropertyValueFactory<>("organizerId"));
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        
        TableColumn<Event, String> venueCol = new TableColumn<>("Venue");
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        
        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        eventTable.getColumns().addAll(titleCol, organizerCol, dateCol, venueCol, statusCol);
        
        content.getChildren().addAll(toolbar, eventTable);
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createSystemLogsTab() {
        Tab tab = new Tab("System Logs");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        Button refreshLogsButton = new Button("Refresh Logs");
        Button clearLogsButton = new Button("Clear Logs");
        
        refreshLogsButton.setOnAction(e -> controller.refreshSystemLogs());
        clearLogsButton.setOnAction(e -> controller.clearSystemLogs());
        
        toolbar.getChildren().addAll(refreshLogsButton, clearLogsButton);
        
        // Log area
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: monospace;");
        
        content.getChildren().addAll(toolbar, logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        
        tab.setContent(content);
        
        return tab;
    }
    
    public void show() {
        stage.show();
    }
    
    // Getters for controller access
    public TableView<User> getUserTable() { return userTable; }
    public TableView<Event> getEventTable() { return eventTable; }
    public TextArea getLogArea() { return logArea; }
    public PieChart getUserRoleChart() { return userRoleChart; }
    public BarChart<String, Number> getEventStatusChart() { return eventStatusChart; }
    public Stage getStage() { return stage; }
    public User getCurrentUser() { return currentUser; }
}