package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.enums.LotType;
import com.parkinglotmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LotDAO {

    // Read all lots
    public List<ParkingLot> getAllLots() {
        List<ParkingLot> lots = new ArrayList<>();
        String sql = "SELECT * FROM Lot";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Note: Creating a simplified ParkingLot object based on SQL columns
                ParkingLot lot = new ParkingLot(
                    String.valueOf(rs.getInt("lotID")), 
                    rs.getString("lotName"), 
                    "Location N/A", // SQL table missing location column, using placeholder
                    LotType.STRUCTURE // Defaulting type as SQL missing type column
                );
                // You might want to manually set capacity in your model if it exists
                // lot.setTotalSpaces(rs.getInt("capacity"));
                lots.add(lot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lots;
    }

    // Read single lot by ID
    public ParkingLot getLotById(int lotId) {
        String sql = "SELECT * FROM Lot WHERE lotID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lotId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new ParkingLot(
                    String.valueOf(rs.getInt("lotID")),
                    rs.getString("lotName"),
                    "Unknown",
                    LotType.STRUCTURE
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update lot status
    public boolean updateLotStatus(int lotId, String status) {
        String sql = "UPDATE Lot SET status = ? WHERE lotID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, lotId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}