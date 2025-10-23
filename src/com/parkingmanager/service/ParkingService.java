package com.parkingmanager.service;

import com.parkingmanager.dao.ParkingSpotDAO;
import com.parkingmanager.dao.VehicleDAO;
import com.parkingmanager.model.ParkingSpot;
import com.parkingmanager.model.Vehicle;

import java.util.List;

/**
 * Service layer for parking operations.
 * Contains business logic and coordinates between DAOs.
 */
public class ParkingService {
    private ParkingSpotDAO spotDAO;
    private VehicleDAO vehicleDAO;
    
    public ParkingService() {
        this.spotDAO = new ParkingSpotDAO();
        this.vehicleDAO = new VehicleDAO();
    }
    
    /**
     * Parks a vehicle in the specified spot.
     * @param licensePlate Vehicle's license plate
     * @param type Vehicle type
     * @param spotNumber Parking spot number
     * @return true if successful, false otherwise
     */
    public boolean parkVehicle(String licensePlate, String type, int spotNumber) {
        ParkingSpot spot = spotDAO.getByNumber(spotNumber);
        if (spot == null || !spot.isAvailable()) {
            return false;
        }
        
        Vehicle vehicle = new Vehicle(licensePlate, type, spotNumber);
        if (vehicleDAO.create(vehicle)) {
            return spotDAO.updateAvailability(spotNumber, false);
        }
        return false;
    }
    
    /**
     * Removes a vehicle from parking and calculates the fee.
     * @param licensePlate Vehicle's license plate
     * @return Fee amount, or -1 if vehicle not found or operation failed
     */
    public double removeVehicle(String licensePlate) {
        Vehicle vehicle = vehicleDAO.getByLicensePlate(licensePlate);
        if (vehicle == null) {
            return -1;
        }
        
        ParkingSpot spot = spotDAO.getByNumber(vehicle.getSpotNumber());
        double fee = vehicle.calculateFee(spot.getRate());
        
        if (vehicleDAO.checkout(vehicle.getId(), fee)) {
            spotDAO.updateAvailability(vehicle.getSpotNumber(), true);
            return fee;
        }
        return -1;
    }
    
    /**
     * Gets all parking spots.
     */
    public List<ParkingSpot> getAllSpots() {
        return spotDAO.getAll();
    }
    
    /**
     * Gets all currently parked vehicles.
     */
    public List<Vehicle> getAllParkedVehicles() {
        return vehicleDAO.getAllActive();
    }
    
    /**
     * Gets the number of available parking spots.
     */
    public long getAvailableSpots() {
        return spotDAO.getAvailableCount();
    }
    
    /**
     * Gets the total revenue generated.
     */
    public double getTotalRevenue() {
        return vehicleDAO.getTotalRevenue();
    }
    
    /**
     * Initializes parking spots if none exist.
     * Creates 20 spots: 10 regular ($5/hr) and 10 premium ($8/hr).
     */
    public void initializeSpots() {
        List<ParkingSpot> existing = spotDAO.getAll();
        if (existing.isEmpty()) {
            for (int i = 1; i <= 20; i++) {
                String type = i <= 10 ? "Regular" : "Premium";
                double rate = i <= 10 ? 5.0 : 8.0;
                spotDAO.create(new ParkingSpot(i, type, rate));
            }
        }
    }
}