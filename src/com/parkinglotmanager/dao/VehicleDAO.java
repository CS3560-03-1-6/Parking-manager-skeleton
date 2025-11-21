package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.Vehicle; // You might need to create a simple Vehicle POJO if not exists
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
    public List<String> getVehiclePlatesByUserId(int userId) {
        List<String> plates = new ArrayList<>();
        String sql = "SELECT plate FROM Vehicle WHERE userID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                plates.add(rs.getString("plate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plates;
    }
}