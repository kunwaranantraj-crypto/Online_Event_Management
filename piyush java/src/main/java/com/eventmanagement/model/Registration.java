package com.eventmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Registration {
    private int registrationId;
    private int eventId;
    private int attendeeId;
    private int ticketId;
    private LocalDateTime registrationDate;
    private PaymentStatus paymentStatus;
    private BigDecimal paymentAmount;
    
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
    
    // Constructors
    public Registration() {}
    
    public Registration(int eventId, int attendeeId, int ticketId, BigDecimal paymentAmount) {
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.ticketId = ticketId;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = PaymentStatus.PENDING;
        this.registrationDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public int getAttendeeId() { return attendeeId; }
    public void setAttendeeId(int attendeeId) { this.attendeeId = attendeeId; }
    
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
}