package com.parkinglotmanager.enums;

/**
 * Enum representing different types of parking lot structures.
 */
public enum LotType {
    /**
     * Open-air surface parking lot
     */
    SURFACE("Surface Lot", "Open-air ground-level parking"),

    /**
     * Multi-level parking structure/garage
     */
    STRUCTURE("Parking Structure", "Multi-level covered parking garage");

    private final String displayName;
    private final String description;

    LotType(String displayName, String description) {
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
