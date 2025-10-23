package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.enums.VehicleMake;

/**
 * Abstract base class representing a vehicle in the parking system.
 * Contains essential vehicle identification and type information.
 */
public abstract class Vehicle {
    private VehicleType type;
    private VehicleMake make;
    private String licensePlate;

    /**
     * Constructor for creating a vehicle
     */
    public Vehicle(VehicleType type) {
        this.type = type;
    }

    /**
     * Constructor with make information
     */
    public Vehicle(VehicleType type, VehicleMake make, String licensePlate) {
        this.type = type;
        this.make = make;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters
    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public VehicleMake getMake() {
        return make;
    }

    public void setMake(VehicleMake make) {
        this.make = make;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * Get vehicle display name
     */
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return String.format("%s{type=%s, make=%s, plate='%s'}",
                getClass().getSimpleName(), type, make, licensePlate);
    }
}
