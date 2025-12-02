package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.UserPreference;
import com.parkinglotmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPreferenceDAO {

    // Load preference for a given user.
    // Returns null if no row exists yet.
    public UserPreference getPreferenceByUserId(int userID) {
        String sql = "SELECT userID, preferredLotID, preferredArrivalTime, classLocationDescription " +
                     "FROM UserPreference WHERE userID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer preferredLotID = (Integer) rs.getObject("preferredLotID");
                    String arrival = rs.getString("preferredArrivalTime");
                    String notes = rs.getString("classLocationDescription");

                    return new UserPreference(userID, preferredLotID, arrival, notes);
                } else {
                    // No row yet → dialog will start empty
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Insert or update preference for a user (thanks to UNIQUE(userID))
    public boolean saveOrUpdatePreference(UserPreference pref) {
        String sql = "INSERT INTO UserPreference " +
                     "  (userID, preferredLotID, preferredArrivalTime, classLocationDescription) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "  preferredLotID = VALUES(preferredLotID), " +
                     "  preferredArrivalTime = VALUES(preferredArrivalTime), " +
                     "  classLocationDescription = VALUES(classLocationDescription)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // userID (must already be set on the object)
            stmt.setInt(1, pref.getUserID());

            // preferredLotID can be null
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
