package com.parkinglotmanager.enums;

/**
 * Enum representing different types of parking slots.
 * Each slot type has specific characteristics and may have different pricing.
 */
public enum SlotType {
    /**
     * Standard car parking slot
     */
    CAR("Car Slot", "Standard parking space for cars"),

    /**
     * Motorcycle parking slot (smaller)
     */
    MOTORCYCLE("Motorcycle Slot", "Compact space for motorcycles"),

    /**
     * Handicapped-accessible parking slot
     */
    HANDICAPPED("Handicapped Slot", "ADA-compliant accessible parking"),

    /**
     * Electric vehicle charging slot
     */
    EV("EV Charging Slot", "Electric vehicle parking with charger"),

    /**
     * Compact car parking slot
     */
    COMPACT("Compact Slot", "Smaller space for compact vehicles");

    private final String displayName;
    private final String description;

    SlotType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this slot type is compatible with a vehicle type
     */
    public boolean isCompatibleWith(VehicleType vehicleType) {
        switch (this) {
            case MOTORCYCLE:
                return vehicleType == VehicleType.MOTORCYCLE;
            case EV:
                return vehicleType == VehicleType.EV;
            case HANDICAPPED:
            case CAR:
            case COMPACT:
                return vehicleType == VehicleType.CAR || vehicleType == VehicleType.EV;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
