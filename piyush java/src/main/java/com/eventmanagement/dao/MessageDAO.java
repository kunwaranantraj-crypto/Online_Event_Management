package com.eventmanagement.dao;

import com.eventmanagement.model.Message;
import com.eventmanagement.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    
    public boolean createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (event_id, sender_id, title, content) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, message.getEventId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getTitle());
            stmt.setString(4, message.getContent());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setMessageId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public List<Message> getMessagesByEvent(int eventId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE event_id = ? ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    public List<Message> getMessagesByAttendee(int attendeeId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.* FROM messages m " +
                     "INNER JOIN registrations r ON m.event_id = r.event_id " +
                     "WHERE r.attendee_id = ? " +
                     "ORDER BY m.sent_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE message_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, messageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMessage(rs);
                }
            }
        }
        return null;
    }
    
    public boolean deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getInt("message_id"));
        message.setEventId(rs.getInt("event_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setTitle(rs.getString("title"));
        message.setContent(rs.getString("content"));
        message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
        return message;
    }
}