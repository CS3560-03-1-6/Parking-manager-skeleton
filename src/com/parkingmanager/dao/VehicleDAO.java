package com.parkingmanager.dao;

import com.parkingmanager.model.Vehicle;
import com.parkingmanager.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vehicle entity.
 * Handles all database operations related to vehicles.
 */
public class VehicleDAO {
    
    /**
     * Creates a new vehicle entry in the database.
     */
    public boolean create(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (license_plate, type, spot_number, entry_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getType());
            stmt.setInt(3, vehicle.getSpotNumber());
            stmt.setTimestamp(4, Timestamp.valueOf(vehicle.getEntryTime()));
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    vehicle.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Retrieves all vehicles that are currently parked (haven't exited yet).
     */
    public List<Vehicle> getAllActive() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE exit_time IS NULL ORDER BY entry_time";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("id"),
                    rs.getString("license_plate"),
                    rs.getString("type"),
                    rs.getInt("spot_number"),
                    rs.getTimestamp("entry_time").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
    
    /**
     * Retrieves a currently parked vehicle by license plate.
     */
    public Vehicle getByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ? AND exit_time IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vehicle(
                    rs.getInt("id"),
                    rs.getString("license_plate"),
                    rs.getString("type"),
                    rs.getInt("spot_number"),
                    rs.getTimestamp("entry_time").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Checks out a vehicle by updating its exit time and fee.
     */
    public boolean checkout(int id, double fee) {
        String sql = "UPDATE vehicles SET exit_time = ?, fee = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(2, fee);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calculates the total revenue from all checked-out vehicles.
     */
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(fee), 0) FROM vehicles WHERE fee IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}