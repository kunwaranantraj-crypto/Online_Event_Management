module eventmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    
    exports com.eventmanagement;
    exports com.eventmanagement.model;
    exports com.eventmanagement.view;
    exports com.eventmanagement.controller;
    exports com.eventmanagement.dao;
    exports com.eventmanagement.util;
    
    // Open packages to JavaFX for FXML injection
    opens com.eventmanagement.controller to javafx.fxml;
    opens com.eventmanagement.view to javafx.fxml;
}