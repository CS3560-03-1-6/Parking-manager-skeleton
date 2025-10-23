package com.parkinglotmanager.model;

import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.enums.VehicleMake;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Represents an active parking session for a vehicle.
 * Tracks entry/exit times and calculates fees.
 */
public class VehicleSession {
    private int id;
    private String licensePlate;
    private VehicleType vehicleType;
    private VehicleMake vehicleMake;
    private int userId;
    private String slotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    private String paymentStatus; // PENDING, PAID, REFUNDED

    /**
     * Constructor for creating a new parking session
     */
    public VehicleSession(String licensePlate, VehicleType vehicleType, String slotId) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.slotId = slotId;
        this.entryTime = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }

    /**
     * Constructor with make information
     */
    public VehicleSession(String licensePlate, VehicleType vehicleType, VehicleMake vehicleMake,
            int userId, String slotId) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.vehicleMake = vehicleMake;
        this.userId = userId;
        this.slotId = slotId;
        this.entryTime = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }

    /**
     * Constructor for loading from database
     */
    public VehicleSession(int id, String licensePlate, VehicleType vehicleType, VehicleMake vehicleMake,
            int userId, String slotId, LocalDateTime entryTime, LocalDateTime exitTime,
            double fee, String paymentStatus) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.vehicleMake = vehicleMake;
        this.userId = userId;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.fee = fee;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public VehicleMake getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(VehicleMake vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Calculate the number of hours parked (minimum 1 hour)
     */
    public long getParkedHours() {
        LocalDateTime endTime = exitTime != null ? exitTime : LocalDateTime.now();
        long minutes = Duration.between(entryTime, endTime).toMinutes();
        return Math.max(1, (minutes + 59) / 60); // Round up to next hour
    }

    /**
     * Calculate parking fee based on hourly rate
     */
    public double calculateFee(double hourlyRate) {
        this.fee = getParkedHours() * hourlyRate;
        return this.fee;
    }

    /**
     * Check if session is active (not yet checked out)
     */
    public boolean isActive() {
        return exitTime == null;
    }

    /**
     * Complete the parking session
     */
    public void checkout(double calculatedFee) {
        this.exitTime = LocalDateTime.now();
        this.fee = calculatedFee;
    }

    @Override
    public String toString() {
        return String.format("VehicleSession{id=%d, plate='%s', type=%s, hours=%d, fee=$%.2f}",
                id, licensePlate, vehicleType, getParkedHours(), fee);
    }
}
