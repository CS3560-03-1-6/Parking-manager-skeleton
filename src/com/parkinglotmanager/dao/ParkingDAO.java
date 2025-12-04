package com.parkinglotmanager.dao;

import java.util.ArrayList;
import java.util.List;

import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.ParkingSlot;
import com.parkinglotmanager.model.VehicleSession;

/**
 * Encapsulates parking-related business logic and persistence.
 * Lives in the dao package to avoid adding a new folder.
 */
public class ParkingDAO {

    private final VehicleSessionDAO sessionDAO;
    private final VehicleDAO vehicleDAO;

    public ParkingDAO(VehicleSessionDAO sessionDAO, VehicleDAO vehicleDAO) {
        this.sessionDAO = sessionDAO;
        this.vehicleDAO = vehicleDAO;
    }

    /**
     * Get all active sessions from the database.
     */
    public List<VehicleSession> getActiveSessions() {
        return sessionDAO.getActiveSessions();
    }

    /**
     * Return all available slots in a lot.
     */
    public List<ParkingSlot> getAvailableSlots(ParkingLot lot) {
        List<ParkingSlot> available = new ArrayList<>();
        for (ParkingSlot slot : lot.getSlots()) {
            if (!slot.isOccupied()) {
                available.add(slot);
            }
        }
        return available;
    }

    /**
     * Filter available slots to only those compatible with a vehicle type.
     */
    public List<ParkingSlot> getCompatibleAvailableSlots(ParkingLot lot, VehicleType vehicleType) {
        List<ParkingSlot> available = getAvailableSlots(lot);
        List<ParkingSlot> compatible = new ArrayList<>();
        for (ParkingSlot slot : available) {
            if (slot.getSlotType().isCompatibleWith(vehicleType)) {
                compatible.add(slot);
            }
        }
        return compatible;
    }

    /**
     * Create a parking session and mark the slot occupied.
     * Returns the created VehicleSession (with ID) or null on failure.
     */
    public VehicleSession parkVehicle(
            ParkingLot lot,
            ParkingSlot slot,
            String plate,
            VehicleType vehicleType,
            VehicleMake vehicleMake,
            int userId) {

        // Optionally add vehicle to vehicle table if needed
        if (vehicleMake != null && plate != null && !plate.isEmpty()) {
            String model = "Model-Unknown";
            String color = "Unknown";
            vehicleDAO.addVehicle(userId, plate, vehicleMake.toString(), model, color);
        }

        // Create new session object
        VehicleSession session = new VehicleSession(
                plate,
                vehicleType,
                vehicleMake,
                userId,
                slot.getSlotId());

        // Persist it
        int sessionId = sessionDAO.insertSession(session);
        if (sessionId <= 0) {
            return null;
        }

        // Set generated ID & mark slot occupied in memory
        session.setId(sessionId);
        slot.setOccupied(true, vehicleType);

        return session;
    }

    /**
     * Complete a session and free the slot in memory.
     */
    public boolean exitVehicle(
            VehicleSession session,
            double hourlyRate,
            List<ParkingLot> lots) {

        if (session == null || session.getId() <= 0) {
            return false;
        }

        double fee = session.calculateFee(hourlyRate);

        // Free slot in memory
        for (ParkingLot lot : lots) {
            for (ParkingSlot slot : lot.getSlots()) {
                if (slot.getSlotId().equals(session.getSlotId())) {
                    slot.setOccupied(false, null);
                    break;
                }
            }
        }

        // Complete in DB
        return sessionDAO.completeSession(session.getId(), fee);
    }
}