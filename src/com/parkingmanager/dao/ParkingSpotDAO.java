package com.parkingmanager.dao;

import com.parkingmanager.model.ParkingSpot;
import com.parkingmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ParkingSpot entity.
 * Handles all database operations related to parking spots.
 */
public class ParkingSpotDAO {
    
    /**
     * Creates a new parking spot in the database.
     */
    public boolean create(ParkingSpot spot) {
        String sql = "INSERT INTO parking_spots (number, type, available, rate) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, spot.getNumber());
            stmt.setString(2, spot.getType());
            stmt.setBoolean(3, spot.isAvailable());
            stmt.setDouble(4, spot.getRate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all parking spots from the database.
     */
    public List<ParkingSpot> getAll() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots ORDER BY number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                spots.add(new ParkingSpot(
                    rs.getInt("id"),
                    rs.getInt("number"),
                    rs.getString("type"),
                    rs.getBoolean("available"),
                    rs.getDouble("rate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spots;
    }
    
    /**
     * Retrieves a parking spot by its number.
     */
    public ParkingSpot getByNumber(int number) {
        String sql = "SELECT * FROM parking_spots WHERE number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ParkingSpot(
                    rs.getInt("id"),
                    rs.getInt("number"),
                    rs.getString("type"),
                    rs.getBoolean("available"),
                    rs.getDouble("rate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the availability status of a parking spot.
     */
    public boolean updateAvailability(int number, boolean available) {
        String sql = "UPDATE parking_spots SET available = ? WHERE number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, number);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the count of available parking spots.
     */
    public long getAvailableCount() {
        String sql = "SELECT COUNT(*) FROM parking_spots WHERE available = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}