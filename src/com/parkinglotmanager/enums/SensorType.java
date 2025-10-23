package com.parkinglotmanager.enums;

/**
 * Enum representing different types of IoT sensors used for parking detection.
 */
public enum SensorType {
    /**
     * Ultrasonic distance sensor
     */
    ULTRASONIC("Ultrasonic Sensor", "Measures distance using sound waves"),

    /**
     * Camera-based vision system
     */
    CAMERA("Camera Sensor", "Computer vision-based detection"),

    /**
     * Inductive loop sensor embedded in pavement
     */
    LOOP("Inductive Loop", "Electromagnetic sensor in pavement");

    private final String displayName;
    private final String description;

    SensorType(String displayName, String description) {
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
     * Get typical accuracy rate for this sensor type
     */
    public double getTypicalAccuracy() {
        switch (this) {
            case CAMERA:
                return 0.95; // 95% accuracy
            case ULTRASONIC:
                return 0.90; // 90% accuracy
            case LOOP:
                return 0.98; // 98% accuracy
            default:
                return 0.85; // default fallback
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
