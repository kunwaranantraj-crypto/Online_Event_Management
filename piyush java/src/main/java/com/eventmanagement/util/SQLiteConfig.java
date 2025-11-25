package com.eventmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * SQLite configuration for local testing without MySQL setup
 * Switch to this in DatabaseConfig.java for easier local development
 */
public class SQLiteConfig {
    
    private static final String DB_URL = "jdbc:sqlite:event_management.db";
    private static Connection connection;
    
    public static void initializeDatabase() {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            
            connection = DriverManager.getConnection(DB_URL);
            
            // Enable foreign key constraints
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            // Initialize schema if database is new
            initializeSchema();
            
            System.out.println("SQLite database connection established successfully!");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("SQLite database connection failed: " + e.getMessage());
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
                System.out.println("SQLite database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing SQLite database connection: " + e.getMessage());
        }
    }
}