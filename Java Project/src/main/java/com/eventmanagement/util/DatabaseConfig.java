package com.eventmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseConfig {
    
    // MySQL configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/event_management";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "AmanKumar@007";
    
    // SQLite configuration (alternative for testing)
    // private static final String DB_URL = "jdbc:sqlite:event_management.db";
    
    private static Connection connection;
    
    public static void initializeDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            // For SQLite, use this instead:
            // Class.forName("org.sqlite.JDBC");
            // connection = DriverManager.getConnection(DB_URL);
            
            // MySQL doesn't need PRAGMA, but we can set some connection properties
            // Enable foreign key constraints is default in MySQL
            
            // Note: For MySQL, run database/schema.sql manually in MySQL Workbench
            // or uncomment the line below to auto-create schema (not recommended for production)
            // initializeSchema();
            
            System.out.println("Database connection established successfully!");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void initializeSchema() {
        try {
            // Check if users table exists
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT COUNT(*) FROM users LIMIT 1");
            }
        } catch (SQLException e) {
            // Table doesn't exist, create schema
            System.out.println("Creating database schema...");
            executeSQLFile("database/sqlite_schema.sql");
        }
    }
    
    private static void executeSQLFile(String filePath) {
        try {
            String sql = Files.readString(Paths.get(filePath));
            String[] statements = sql.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        stmt.execute(trimmed);
                    }
                }
            }
            
            System.out.println("Database schema created successfully!");
            
        } catch (IOException | SQLException e) {
            System.err.println("Error executing SQL file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeDatabase();
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}