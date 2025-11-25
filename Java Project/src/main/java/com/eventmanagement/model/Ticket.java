package com.eventmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private int eventId;
    private String ticketType;
    private BigDecimal price;
    private int quantityAvailable;
    private int quantitySold;
    private LocalDateTime createdAt;
    
    // Constructors
    public Ticket() {}
    
    public Ticket(int eventId, String ticketType, BigDecimal price, int quantityAvailable) {
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
        this.quantitySold = 0;
    }
    
    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public int getQuantityRemaining() {
        return quantityAvailable - quantitySold;
    }
    
    @Override
    public String toString() {
        return ticketType + " - $" + price + " (" + getQuantityRemaining() + " available)";
    }
}