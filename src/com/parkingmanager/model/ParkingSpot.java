package com.parkingmanager.model;

/**
 * Model class representing a parking spot in the parking management system.
 * Contains spot information including number, type, availability, and hourly rate.
 */
public class ParkingSpot {
    private int id;
    private int number;
    private String type;
    private boolean available;
    private double rate;
    
    /**
     * Constructor for creating a parking spot with all details (typically from database).
     */
    public ParkingSpot(int id, int number, String type, boolean available, double rate) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.available = available;
        this.rate = rate;
    }
    
    /**
     * Constructor for creating a new parking spot (before saving to database).
     */
    public ParkingSpot(int number, String type, double rate) {
        this.number = number;
        this.type = type;
        this.available = true;
        this.rate = rate;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    
    @Override
    public String toString() {
        return "Spot #" + number + " (" + type + ") - " + (available ? "Available" : "Occupied");
    }
}