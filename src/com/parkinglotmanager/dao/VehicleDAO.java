package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.Vehicle;
import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // --- SETTER (Create) ---
    public boolean addVehicle(int userId, String plate, String make, String model, String color) {
        String sql = "INSERT INTO Vehicle (userID, plate, make, model, color) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, plate);
            stmt.setString(3, make);
            stmt.setString(4, model);
            stmt.setString(5, color);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- GETTER (Read by User) ---
    // Useful for "My Vehicles" list
    public List<Vehicle> getVehicleByUserId(int userId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE userID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("vehicleID"),
                    rs.getInt("userID"),
                    rs.getString("plate"),
                    VehicleMake.valueOf(rs.getString("make")),
                    rs.getString("model"),
                    rs.getString("color")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Read by Vehicle ID
    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT * FROM Vehicle WHERE vehicleID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Vehicle(
                    rs.getInt("vehicleID"),
                    rs.getInt("userID"),
                    rs.getString("plate"),
                    VehicleMake.valueOf(rs.getString("make")),
                    rs.getString("model"),
                    rs.getString("color")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Vehicle not found
    }
}