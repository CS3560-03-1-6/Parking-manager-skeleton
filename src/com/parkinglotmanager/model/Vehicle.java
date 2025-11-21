package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.enums.VehicleMake;

/**
 * Abstract base class representing a vehicle in the parking system.
 * Contains essential vehicle identification and type information.
 */
public class Vehicle {
    
    private int vehicleID;
    private int userID;
    private String licensePlate;
    private VehicleMake make;
    private String model;
    private String color;
    /**
     * Full constructor
     */
    public Vehicle(int vehicleID, int userID, String licensePlate, VehicleMake make, String model, String color) {
        this.vehicleID = vehicleID;
        this.userID = userID;
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.color = color;
    }

    
    // Getters
    public int getVehicleID() {
        return vehicleID;
    }

    public int getUserID() {
        return userID;
    }

    public VehicleMake getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getColor() {
        return color;
    }

    // Setters

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setMake(VehicleMake make) {
        this.make = make;
    }
    
    public void setModel(String model) {
        this.model = model;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public void setColor(String color) {
        this.color = color;
    }

    // /**
    //  * Get vehicle display name
    //  */
    // public abstract String getDisplayName();

    @Override
    public String toString() {
        return String.format("%s{VehicleID=%d, UserID=%d, make=%s, model=%s, plate='%s', color='%s'}",
                getClass().getSimpleName(), vehicleID, userID, make, model, licensePlate, color);
    }
}
