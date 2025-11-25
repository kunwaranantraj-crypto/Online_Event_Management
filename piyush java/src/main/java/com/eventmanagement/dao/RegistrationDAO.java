package com.eventmanagement.dao;

import com.eventmanagement.model.Registration;
import com.eventmanagement.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {
    
    public boolean createRegistration(Registration registration) throws SQLException {
        String sql = "INSERT INTO registrations (event_id, attendee_id, ticket_id, payment_amount, payment_status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, registration.getEventId());
            stmt.setInt(2, registration.getAttendeeId());
            stmt.setInt(3, registration.getTicketId());
            stmt.setBigDecimal(4, registration.getPaymentAmount());
            stmt.setString(5, registration.getPaymentStatus().name());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        registration.setRegistrationId(generatedKeys.getInt(1));
                    }
                }
                
                // Update ticket sales count
                updateTicketSales(registration.getTicketId());
                
                return true;
            }
        }
        return false;
    }
    
    private void updateTicketSales(int ticketId) throws SQLException {
        String sql = "UPDATE tickets SET quantity_sold = quantity_sold + 1 WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
        }
    }
    
    public boolean cancelRegistration(int registrationId) throws SQLException {
        // First get the ticket ID to update sales count
        String getTicketSql = "SELECT ticket_id FROM registrations WHERE registration_id = ?";
        int ticketId = 0;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getTicketSql)) {
            
            stmt.setInt(1, registrationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ticketId = rs.getInt("ticket_id");
                }
            }
        }
        
        // Delete the registration
        String deleteSql = "DELETE FROM registrations WHERE registration_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            
            stmt.setInt(1, registrationId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0 && ticketId > 0) {
                // Update ticket sales count
                String updateTicketSql = "UPDATE tickets SET quantity_sold = quantity_sold - 1 WHERE ticket_id = ? AND quantity_sold > 0";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateTicketSql)) {
                    updateStmt.setInt(1, ticketId);
                    updateStmt.executeUpdate();
                }
                return true;
            }
        }
        return false;
    }
    
    public List<Registration> getRegistrationsByAttendee(int attendeeId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE attendee_id = ? ORDER BY registration_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    public List<Registration> getRegistrationsByEvent(int eventId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ? ORDER BY registration_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    public boolean isUserRegistered(int eventId, int attendeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND attendee_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, attendeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean updatePaymentStatus(int registrationId, Registration.PaymentStatus status) throws SQLException {
        String sql = "UPDATE registrations SET payment_status = ? WHERE registration_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, registrationId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public Registration getRegistrationById(int registrationId) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE registration_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, registrationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        }
        return null;
    }
    
    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        Registration registration = new Registration();
        registration.setRegistrationId(rs.getInt("registration_id"));
        registration.setEventId(rs.getInt("event_id"));
        registration.setAttendeeId(rs.getInt("attendee_id"));
        registration.setTicketId(rs.getInt("ticket_id"));
        registration.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        registration.setPaymentStatus(Registration.PaymentStatus.valueOf(rs.getString("payment_status")));
        registration.setPaymentAmount(rs.getBigDecimal("payment_amount"));
        return registration;
    }
}