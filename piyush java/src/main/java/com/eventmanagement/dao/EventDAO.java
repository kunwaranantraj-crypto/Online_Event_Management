package com.eventmanagement.dao;

import com.eventmanagement.model.Event;
import com.eventmanagement.util.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    
    public boolean createEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, event_date, event_time, venue, organizer_id, max_attendees) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, Date.valueOf(event.getEventDate()));
            stmt.setTime(4, Time.valueOf(event.getEventTime()));
            stmt.setString(5, event.getVenue());
            stmt.setInt(6, event.getOrganizerId());
            stmt.setInt(7, event.getMaxAttendees());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setEventId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, event_date = ?, event_time = ?, venue = ?, max_attendees = ? WHERE event_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, Date.valueOf(event.getEventDate()));
            stmt.setTime(4, Time.valueOf(event.getEventTime()));
            stmt.setString(5, event.getVenue());
            stmt.setInt(6, event.getMaxAttendees());
            stmt.setInt(7, event.getEventId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateEventStatus(int eventId, Event.EventStatus status) throws SQLException {
        String sql = "UPDATE events SET status = ? WHERE event_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, eventId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteEvent(int eventId) throws SQLException {
        String sql = "UPDATE events SET status = 'CANCELLED' WHERE event_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    public List<Event> getEventsByOrganizer(int organizerId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, organizerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    public List<Event> getEventsByStatus(Event.EventStatus status) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    public List<Event> getApprovedEvents() throws SQLException {
        return getEventsByStatus(Event.EventStatus.APPROVED);
    }
    
    public List<Event> getUpcomingEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'APPROVED' AND event_date >= CURDATE() ORDER BY event_date ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        }
        return null;
    }
    
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getDate("event_date").toLocalDate());
        event.setEventTime(rs.getTime("event_time").toLocalTime());
        event.setVenue(rs.getString("venue"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setStatus(Event.EventStatus.valueOf(rs.getString("status")));
        event.setMaxAttendees(rs.getInt("max_attendees"));
        event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return event;
    }
}