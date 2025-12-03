package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.VehicleSession;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleSessionDAO {

    /**
     * Insert a new parking session into the database
     * 
     * @return the generated sessionID or -1 on failure
     */
    public int insertSession(VehicleSession session) {
        String sql = "INSERT INTO VehicleSession (licensePlate, vehicleType, vehicleMake, userID, slotID, entryTime, paymentStatus) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, session.getLicensePlate());
            stmt.setString(2, session.getVehicleType() != null ? session.getVehicleType().name() : null);
            stmt.setString(3, session.getVehicleMake() != null ? session.getVehicleMake().name() : null);
            stmt.setInt(4, session.getUserId());
            stmt.setString(5, session.getSlotId());
            stmt.setTimestamp(6, Timestamp.valueOf(session.getEntryTime()));
            stmt.setString(7, session.getPaymentStatus());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int sessionID = rs.getInt(1);
                    session.setId(sessionID);
                    return sessionID;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting vehicle session: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get all active parking sessions (exitTime IS NULL)
     */
    public List<VehicleSession> getActiveSessions() {
        List<VehicleSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM VehicleSession WHERE exitTime IS NULL ORDER BY entryTime DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading active sessions: " + e.getMessage());
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Complete a parking session (set exit time and fee)
     */
    public boolean completeSession(int sessionID, double fee) {
        String sql = "UPDATE VehicleSession SET exitTime = ?, fee = ?, paymentStatus = 'PAID' WHERE sessionID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(2, fee);
            stmt.setInt(3, sessionID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error completing session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get a session by ID
     */
    public VehicleSession getSessionById(int sessionID) {
        String sql = "SELECT * FROM VehicleSession WHERE sessionID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting session: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all sessions for a specific user
     */
    public List<VehicleSession> getSessionsByUserId(int userID) {
        List<VehicleSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM VehicleSession WHERE userID = ? ORDER BY entryTime DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading user sessions: " + e.getMessage());
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Helper method to map ResultSet to VehicleSession object
     */
    private VehicleSession mapResultSetToSession(ResultSet rs) throws SQLException {
        int id = rs.getInt("sessionID");
        String licensePlate = rs.getString("licensePlate");

        // Handle nullable enum fields
        VehicleType vehicleType = null;
        String vtStr = rs.getString("vehicleType");
        if (vtStr != null && !vtStr.isEmpty()) {
            vehicleType = VehicleType.valueOf(vtStr);
        }

        VehicleMake vehicleMake = null;
        String vmStr = rs.getString("vehicleMake");
        if (vmStr != null && !vmStr.isEmpty()) {
            vehicleMake = VehicleMake.valueOf(vmStr);
        }

        int userId = rs.getInt("userID");
        String slotId = rs.getString("slotID");

        LocalDateTime entryTime = rs.getTimestamp("entryTime").toLocalDateTime();

        Timestamp exitTs = rs.getTimestamp("exitTime");
        LocalDateTime exitTime = (exitTs != null) ? exitTs.toLocalDateTime() : null;

        double fee = rs.getDouble("fee");
        String paymentStatus = rs.getString("paymentStatus");

        return new VehicleSession(id, licensePlate, vehicleType, vehicleMake, userId,
                slotId, entryTime, exitTime, fee, paymentStatus);
    }

    /**
     * Delete a session by ID (for testing/cleanup)
     */
    public boolean deleteSession(int sessionID) {
        String sql = "DELETE FROM VehicleSession WHERE sessionID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
