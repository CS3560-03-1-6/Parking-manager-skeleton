package com.parkinglotmanager.model;

import java.time.LocalDateTime;

/**
 * Represents a user-submitted report about parking lot availability.
 * Part of crowdsourced data collection system.
 */
public class UserReport {
    private String reportId;
    private int userId;
    private String lotId;
    private LocalDateTime timestamp;
    private int reportedAvailable;
    private int confidence;
    private String comments;

    /**
     * Constructor for creating a new user report
     */
    public UserReport(String reportId, int userId, String lotId, int reportedAvailable,
            int confidence, String comments) {
        this.reportId = reportId;
        this.userId = userId;
        this.lotId = lotId;
        this.timestamp = LocalDateTime.now();
        this.reportedAvailable = reportedAvailable;
        this.confidence = confidence;
        this.comments = comments;
    }

    /**
     * Constructor for loading from database
     */
    public UserReport(String reportId, int userId, String lotId, LocalDateTime timestamp,
            int reportedAvailable, int confidence, String comments) {
        this.reportId = reportId;
        this.userId = userId;
        this.lotId = lotId;
        this.timestamp = timestamp;
        this.reportedAvailable = reportedAvailable;
        this.confidence = confidence;
        this.comments = comments;
    }

    // Getters and Setters
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getReportedAvailable() {
        return reportedAvailable;
    }

    public void setReportedAvailable(int reportedAvailable) {
        this.reportedAvailable = reportedAvailable;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return String.format("UserReport{id='%s', lot='%s', available=%d, confidence=%d%%}",
                reportId, lotId, reportedAvailable, confidence);
    }
}
