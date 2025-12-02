package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.UserPreference;
import com.parkinglotmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access for UserPreference.
 * Allows reading and saving preferences per user.
 */
public class UserPreferenceDAO {

    // Read preferences for a given userID
    public UserPreference getByUserID(int userID) {
        String sql = "SELECT * FROM UserPreference WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserPreference pref = new UserPreference();
                pref.setUserID(rs.getInt("userID"));

                int lotId = rs.getInt("preferredLotID");
                if (!rs.wasNull()) {
                    pref.setPreferredLotID(lotId);
                }

                pref.setPreferredArrivalTime(rs.getString("preferredArrivalTime"));
                pref.setClassLocationDescription(rs.getString("classLocationDescription"));

                return pref;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // no preferences yet for this user
    }

    // Inserts or updates preferences for a user
    public boolean saveOrUpdate(UserPreference pref) {
        // Use INSERT ... ON DUPLICATE KEY UPDATE for simple upsert
        String sql = "INSERT INTO UserPreference " +
                "(userID, preferredLotID, preferredArrivalTime, classLocationDescription) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "preferredLotID = VALUES(preferredLotID), " +
                "preferredArrivalTime = VALUES(preferredArrivalTime), " +
                "classLocationDescription = VALUES(classLocationDescription)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pref.getUserID());

            if (pref.getPreferredLotID() != null) {
                stmt.setInt(2, pref.getPreferredLotID());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, pref.getPreferredArrivalTime());
            stmt.setString(4, pref.getClassLocationDescription());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
