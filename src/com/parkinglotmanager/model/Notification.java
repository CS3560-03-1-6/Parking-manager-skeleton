package com.parkinglotmanager.model;

import java.time.LocalDateTime;

/**
 * Represents a system notification sent to a user.
 * Maps to the 'Notification' table in the database.
 */
public class Notification {
    private int notificationId;     // Maps to notificationID (INT)
    private int recipientId;        // Maps to recipientID (INT)
    private String message;         // Maps to message (VARCHAR)
    private String type;            // Maps to notificationType (VARCHAR)
    private boolean isRead;         // Maps to isRead (TINYINT)
    private LocalDateTime sentTime; // Maps to sentTime (DATETIME)

    /**
     * Constructor for creating a new notification (before saving to DB).
     * Sets ID to 0, isRead to false, and sentTime to NOW().
     */
    public Notification(int recipientId, String message, String type) {
        this.notificationId = 0;
        this.recipientId = recipientId;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.sentTime = LocalDateTime.now();
    }

    /**
     * Constructor for loading from database.
     */
    public Notification(int notificationId, int recipientId, String message, String type, boolean isRead, LocalDateTime sentTime) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.sentTime = sentTime;
    }

    // --- Getters and Setters ---

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (%s)", 
            sentTime, type, message, isRead ? "Read" : "Unread");
    }
}