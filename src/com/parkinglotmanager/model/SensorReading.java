package com.parkinglotmanager.model;

import java.time.LocalDateTime;

/**
 * Represents a single reading from a parking sensor.
 * Contains occupancy status and confidence level.
 */
public class SensorReading {
    private long readingId;
    private String sensorId;
    private LocalDateTime timestamp;
    private boolean occupied;
    private float confidence;
    private String rawData; // JSON or other raw sensor data

    /**
     * Constructor for creating a new sensor reading
     */
    public SensorReading(String sensorId, boolean occupied, float confidence) {
        this.sensorId = sensorId;
        this.timestamp = LocalDateTime.now();
        this.occupied = occupied;
        this.confidence = confidence;
    }

    /**
     * Constructor for loading from database
     */
    public SensorReading(long readingId, String sensorId, LocalDateTime timestamp,
            boolean occupied, float confidence, String rawData) {
        this.readingId = readingId;
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.occupied = occupied;
        this.confidence = confidence;
        this.rawData = rawData;
    }

    // Getters and Setters
    public long getReadingId() {
        return readingId;
    }

    public void setReadingId(long readingId) {
        this.readingId = readingId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    /**
     * Check if reading is reliable based on confidence threshold
     */
    public boolean isReliable() {
        return confidence >= 0.7f; // 70% threshold
    }

    @Override
    public String toString() {
        return String.format("SensorReading{sensor='%s', occupied=%b, confidence=%.2f}",
                sensorId, occupied, confidence);
    }
}
