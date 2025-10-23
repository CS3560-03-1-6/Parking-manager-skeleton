package com.parkinglotmanager.enums;

/**
 * Enum representing vehicle manufacturers/makes.
 * Used for vehicle registration and reporting.
 */
public enum VehicleMake {
    // American Brands
    FORD("Ford"),
    CHEVROLET("Chevrolet"),
    GMC("GMC"),
    TESLA("Tesla"),
    JEEP("Jeep"),

    // European Brands
    MERCEDES("Mercedes-Benz"),
    BMW("BMW"),
    AUDI("Audi"),
    VOLKSWAGEN("Volkswagen"),
    VOLVO("Volvo"),

    // Asian Brands
    TOYOTA("Toyota"),
    HONDA("Honda"),
    NISSAN("Nissan"),
    HYUNDAI("Hyundai"),
    KIA("Kia"),
    MAZDA("Mazda"),
    SUBARU("Subaru"),

    // Motorcycle Brands
    HARLEY_DAVIDSON("Harley-Davidson"),
    YAMAHA("Yamaha"),
    KAWASAKI("Kawasaki"),
    SUZUKI("Suzuki"),
    DUCATI("Ducati"),

    // Other
    OTHER("Other");

    private final String displayName;

    VehicleMake(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Get VehicleMake from string (case-insensitive)
     */
    public static VehicleMake fromString(String text) {
        for (VehicleMake make : VehicleMake.values()) {
            if (make.displayName.equalsIgnoreCase(text) || make.name().equalsIgnoreCase(text)) {
                return make;
            }
        }
        return OTHER;
    }
}
