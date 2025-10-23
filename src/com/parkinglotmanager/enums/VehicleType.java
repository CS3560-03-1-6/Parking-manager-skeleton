package com.parkinglotmanager.enums;

/**
 * Enum representing different types of vehicles in the parking lot system.
 * Used for classification and slot assignment.
 */
public enum VehicleType {
    /**
     * Standard passenger car
     */
    CAR("Car", "Standard passenger vehicle"),

    /**
     * Motorcycle or scooter
     */
    MOTORCYCLE("Motorcycle", "Two-wheeled vehicle"),

    /**
     * Electric vehicle requiring charging
     */
    EV("Electric Vehicle", "Electric or plug-in hybrid vehicle"),

    /**
     * Truck or large vehicle
     */
    TRUCK("Truck", "Pickup truck or large vehicle");

    private final String displayName;
    private final String description;

    VehicleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
