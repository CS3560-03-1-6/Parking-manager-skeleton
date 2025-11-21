package com.parkinglotmanager.dao;

import com.parkinglotmanager.util.DatabaseConnection;
import java.sql.*;

public class LogDAO {

    // --- SETTER (Write Log) ---
    public void logAction(String type, String message, Integer userId) {
        String sql = "INSERT INTO Log (logType, logTime, logMessage, userID) VALUES (?, NOW(), ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            stmt.setString(2, message);
            
            if (userId != null) {
                stmt.setInt(3, userId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Don't throw error for logging, just print it
            System.err.println("Logging failed: " + e.getMessage());
        }
    }
}