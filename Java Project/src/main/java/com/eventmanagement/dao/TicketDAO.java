package com.eventmanagement.dao;

import com.eventmanagement.model.Ticket;
import com.eventmanagement.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    
    public boolean createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (event_id, ticket_type, price, quantity_available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, ticket.getEventId());
            stmt.setString(2, ticket.getTicketType());
            stmt.setBigDecimal(3, ticket.getPrice());
            stmt.setInt(4, ticket.getQuantityAvailable());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setTicketId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET ticket_type = ?, price = ?, quantity_available = ? WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticket.getTicketType());
            stmt.setBigDecimal(2, ticket.getPrice());
            stmt.setInt(3, ticket.getQuantityAvailable());
            stmt.setInt(4, ticket.getTicketId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteTicket(int ticketId) throws SQLException {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Ticket> getTicketsByEvent(int eventId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE event_id = ? ORDER BY price ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }
    
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }
    
    public boolean updateTicketSales(int ticketId, int quantitySold) throws SQLException {
        String sql = "UPDATE tickets SET quantity_sold = ? WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantitySold);
            stmt.setInt(2, ticketId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Ticket> getAvailableTickets(int eventId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE event_id = ? AND quantity_available > quantity_sold ORDER BY price ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }
    
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setEventId(rs.getInt("event_id"));
        ticket.setTicketType(rs.getString("ticket_type"));
        ticket.setPrice(rs.getBigDecimal("price"));
        ticket.setQuantityAvailable(rs.getInt("quantity_available"));
        ticket.setQuantitySold(rs.getInt("quantity_sold"));
        ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return ticket;
    }
}