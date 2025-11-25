package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int eventId;
    private int senderId;
    private String title;
    private String content;
    private LocalDateTime sentAt;
    
    // Constructors
    public Message() {}
    
    public Message(int eventId, int senderId, String title, String content) {
        this.eventId = eventId;
        this.senderId = senderId;
        this.title = title;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}