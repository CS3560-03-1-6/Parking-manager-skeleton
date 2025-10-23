package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.LotType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a parking lot location with multiple parking slots and sensors.
 */
public class ParkingLot {
    private String lotId;
    private String name;
    private String location;
    private LotType lotType;
    private List<ParkingSlot> slots;
    private List<Sensor> sensors;

    /**
     * Constructor for creating a new parking lot
     */
    public ParkingLot(String lotId, String name, String location, LotType lotType) {
        this.lotId = lotId;
        this.name = name;
        this.location = location;
        this.lotType = lotType;
        this.slots = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    // Getters and Setters
    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Alternative name for UML compatibility
     */
    public String getLotName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LotType getLotType() {
        return lotType;
    }

    public void setLotType(LotType lotType) {
        this.lotType = lotType;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<ParkingSlot> slots) {
        this.slots = slots;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * Get total number of parking spaces in this lot
     */
    public int getTotalSpaces() {
        return slots.size();
    }

    /**
     * Get number of available parking spaces
     */
    public int getAvailableSpaces() {
        return (int) slots.stream()
                .filter(ParkingSlot::isAvailable)
                .count();
    }

    /**
     * Get number of spaces by slot type
     */
    public int getSpacesByType(com.parkinglotmanager.enums.SlotType type) {
        return (int) slots.stream()
                .filter(slot -> slot.getSlotType() == type)
                .count();
    }

    /**
     * Check if lot has available spaces
     */
    public boolean isAvailable() {
        return getAvailableSpaces() > 0;
    }

    /**
     * Add a parking slot to this lot
     */
    public void addSlot(ParkingSlot slot) {
        if (!slots.contains(slot)) {
            slots.add(slot);
        }
    }

    /**
     * Add a sensor to this lot
     */
    public void addSensor(Sensor sensor) {
        if (!sensors.contains(sensor)) {
            sensors.add(sensor);
        }
    }

    /**
     * Get available slots of a specific type
     */
    public List<ParkingSlot> getAvailableSlotsByType(com.parkinglotmanager.enums.SlotType type) {
        return slots.stream()
                .filter(slot -> slot.getSlotType() == type && slot.isAvailable())
                .collect(Collectors.toList());
    }

    /**
     * Get occupancy percentage
     */
    public double getOccupancyPercentage() {
        int total = getTotalSpaces();
        if (total == 0)
            return 0.0;
        return ((total - getAvailableSpaces()) / (double) total) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("ParkingLot{id='%s', name='%s', location='%s', type=%s, spaces=%d/%d}",
                lotId, name, location, lotType, getTotalSpaces() - getAvailableSpaces(), getTotalSpaces());
    }
}
