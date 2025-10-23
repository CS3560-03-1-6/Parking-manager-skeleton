package com.parkinglotmanager.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents aggregated occupancy data for a parking lot.
 * Combines sensor data, user reports, and other sources.
 */
public class OccupancyReport {
    private String lotId;
    private LocalDateTime timestamp;
    private int estimatedAvailable;
    private Map<String, Integer> sourceBreakdown;

    /**
     * Constructor for creating a new occupancy report
     */
    public OccupancyReport(String lotId, int estimatedAvailable) {
        this.lotId = lotId;
        this.timestamp = LocalDateTime.now();
        this.estimatedAvailable = estimatedAvailable;
        this.sourceBreakdown = new HashMap<>();
    }

    /**
     * Constructor for loading from database
     */
    public OccupancyReport(String lotId, LocalDateTime timestamp, int estimatedAvailable,
            Map<String, Integer> sourceBreakdown) {
        this.lotId = lotId;
        this.timestamp = timestamp;
        this.estimatedAvailable = estimatedAvailable;
        this.sourceBreakdown = sourceBreakdown != null ? sourceBreakdown : new HashMap<>();
    }

    // Getters and Setters
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

    public int getEstimatedAvailable() {
        return estimatedAvailable;
    }

    public void setEstimatedAvailable(int estimatedAvailable) {
        this.estimatedAvailable = estimatedAvailable;
    }

    public Map<String, Integer> getSourceBreakdown() {
        return sourceBreakdown;
    }

    public void setSourceBreakdown(Map<String, Integer> sourceBreakdown) {
        this.sourceBreakdown = sourceBreakdown;
    }

    /**
     * Add a source contribution to the breakdown
     */
    public void addSourceData(String source, int count) {
        sourceBreakdown.put(source, count);
    }

    /**
     * Calculate confidence score based on data sources
     * 
     * @return confidence score between 0.0 and 1.0
     */
    public float asConfidenceScore() {
        int totalSources = sourceBreakdown.size();
        if (totalSources == 0)
            return 0.0f;

        // Weight different sources
        float sensorWeight = 0.8f;
        float userReportWeight = 0.5f;
        float databaseWeight = 1.0f;

        float totalWeight = 0.0f;
        float weightedSum = 0.0f;

        if (sourceBreakdown.containsKey("sensors")) {
            weightedSum += sensorWeight * sourceBreakdown.get("sensors");
            totalWeight += sensorWeight;
        }

        if (sourceBreakdown.containsKey("userReports")) {
            weightedSum += userReportWeight * sourceBreakdown.get("userReports");
            totalWeight += userReportWeight;
        }

        if (sourceBreakdown.containsKey("database")) {
            weightedSum += databaseWeight * sourceBreakdown.get("database");
            totalWeight += databaseWeight;
        }

        return totalWeight > 0 ? Math.min(weightedSum / (estimatedAvailable * totalWeight), 1.0f) : 0.0f;
    }

    /**
     * Get number of data sources contributing to this report
     */
    public int getSourceCount() {
        return sourceBreakdown.size();
    }

    @Override
    public String toString() {
        return String.format("OccupancyReport{lot='%s', available=%d, confidence=%.2f, sources=%d}",
                lotId, estimatedAvailable, asConfidenceScore(), getSourceCount());
    }
}
