package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.SensorType;
import java.time.LocalDateTime;

/**
 * Represents an IoT sensor deployed in a parking lot.
 * Sensors detect vehicle presence and report occupancy status.
 */
public class Sensor {
    private String sensorId;
    private String lotId;
    private String slotId;
    private SensorType sensorType;
    private LocalDateTime installedAt;
    private LocalDateTime lastReadingAt;
    private String status; // ACTIVE, INACTIVE, MAINTENANCE, ERROR

    /**
     * Constructor for creating a new sensor
     */
    public Sensor(String sensorId, String lotId, String slotId, SensorType sensorType) {
        this.sensorId = sensorId;
        this.lotId = lotId;
        this.slotId = slotId;
        this.sensorType = sensorType;
        this.installedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    /**
     * Constructor for loading from database
     */
    public Sensor(String sensorId, String lotId, String slotId, SensorType sensorType,
            LocalDateTime installedAt, LocalDateTime lastReadingAt, String status) {
        this.sensorId = sensorId;
        this.lotId = lotId;
        this.slotId = slotId;
        this.sensorType = sensorType;
        this.installedAt = installedAt;
        this.lastReadingAt = lastReadingAt;
        this.status = status;
    }

    // Getters and Setters
    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public LocalDateTime getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }

    public LocalDateTime getLastReadingAt() {
        return lastReadingAt;
    }

    public void setLastReadingAt(LocalDateTime lastReadingAt) {
        this.lastReadingAt = lastReadingAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Check if sensor is active
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    @Override
    public String toString() {
        return String.format("Sensor{id='%s', type=%s, lot='%s', status='%s'}",
                sensorId, sensorType, lotId, status);
    }
}
