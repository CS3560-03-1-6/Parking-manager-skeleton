package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.SlotType;
import com.parkinglotmanager.enums.VehicleType;
import java.time.LocalDateTime;

/**
 * Represents an individual parking slot within a parking lot.
 * Tracks occupancy status, slot type, and last update time.
 */
public class ParkingSlot {
    private String slotId;
    private String lotId;
    private int slotNumber;
    private SlotType slotType;
    private boolean occupied;
    private VehicleType vehicleType; // Type of vehicle currently parked (if occupied)
    private LocalDateTime lastUpdated;
    private String status; // AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE

    /**
     * Constructor for creating a new parking slot
     */
    public ParkingSlot(String slotId, String lotId, int slotNumber, SlotType slotType) {
        this.slotId = slotId;
        this.lotId = lotId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.occupied = false;
        this.lastUpdated = LocalDateTime.now();
        this.status = "AVAILABLE";
    }

    /**
     * Constructor for loading from database
     */
    public ParkingSlot(String slotId, String lotId, int slotNumber, SlotType slotType,
            boolean occupied, VehicleType vehicleType, LocalDateTime lastUpdated, String status) {
        this.slotId = slotId;
        this.lotId = lotId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.occupied = occupied;
        this.vehicleType = vehicleType;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }

    // Getters and Setters
    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    /**
     * Alternative method name for UML compatibility
     */
    public SlotType getType() {
        return slotType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        this.lastUpdated = LocalDateTime.now();
        this.status = occupied ? "OCCUPIED" : "AVAILABLE";
    }

    /**
     * Set occupancy with vehicle type information
     * UML-compatible method signature
     */
    public void setOccupied(boolean isOccupied, VehicleType vehicleType) {
        this.occupied = isOccupied;
        this.vehicleType = isOccupied ? vehicleType : null;
        this.lastUpdated = LocalDateTime.now();
        this.status = isOccupied ? "OCCUPIED" : "AVAILABLE";
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Check if slot is available for parking
     */
    public boolean isAvailable() {
        return !occupied && "AVAILABLE".equals(status);
    }

    /**
     * Check if slot type is compatible with vehicle type
     */
    public boolean isCompatibleWith(VehicleType vehicleType) {
        return slotType.isCompatibleWith(vehicleType);
    }

    /**
     * Release the slot (mark as available)
     */
    public void release() {
        this.occupied = false;
        this.vehicleType = null;
        this.status = "AVAILABLE";
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Reserve the slot
     */
    public void reserve() {
        this.status = "RESERVED";
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("ParkingSlot{id='%s', number=%d, type=%s, occupied=%b, status='%s'}",
                slotId, slotNumber, slotType, occupied, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ParkingSlot that = (ParkingSlot) obj;
        return slotId != null && slotId.equals(that.slotId);
    }

    @Override
    public int hashCode() {
        return slotId != null ? slotId.hashCode() : 0;
    }
}
