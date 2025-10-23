package com.parkingmanager.model;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Model class representing a vehicle in the parking system.
 * Contains vehicle information and parking session details.
 */
public class Vehicle {
    private int id;
    private String licensePlate;
    private String type;
    private int spotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    
    /**
     * Constructor for creating a vehicle with full details (typically from database).
     */
    public Vehicle(int id, String licensePlate, String type, int spotNumber, LocalDateTime entryTime) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.type = type;
        this.spotNumber = spotNumber;
        this.entryTime = entryTime;
    }
    
    /**
     * Constructor for creating a new vehicle entry (before saving to database).
     */
    public Vehicle(String licensePlate, String type, int spotNumber) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.spotNumber = spotNumber;
        this.entryTime = LocalDateTime.now();
    }
    
    /**
     * Calculates the number of hours the vehicle has been parked.
     * Minimum charge is 1 hour.
     */
    public long getParkedHours() {
        LocalDateTime endTime = exitTime != null ? exitTime : LocalDateTime.now();
        long minutes = Duration.between(entryTime, endTime).toMinutes();
        return Math.max(1, (minutes + 59) / 60); // Round up to next hour
    }
    
    /**
     * Calculates the parking fee based on hours parked and hourly rate.
     */
    public double calculateFee(double hourlyRate) {
        this.fee = getParkedHours() * hourlyRate;
        return this.fee;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getSpotNumber() { return spotNumber; }
    public void setSpotNumber(int spotNumber) { this.spotNumber = spotNumber; }
    
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
}