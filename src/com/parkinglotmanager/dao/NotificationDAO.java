package com.parkinglotmanager.dao;

import com.parkinglotmanager.util.DatabaseConnection;
import com.parkinglotmanager.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // --- SETTER (Create Notification) ---
    public boolean createNotification(int recipientId, String message, String type) {
        String sql = "INSERT INTO Notification (recipientID, message, notificationType, isRead, sentTime) " +
                     "VALUES (?, ?, ?, 0, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recipientId);
            stmt.setString(2, message);
            stmt.setString(3, type);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- SETTER (Update Status) ---
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notification SET isRead = 1 WHERE notificationID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- GETTER (Read Unread Messages) ---
    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> messages = new ArrayList<>();
        String sql = "SELECT message, sentTime, notificationID FROM Notification " +
                     "WHERE recipientID = ? AND isRead = 0 ORDER BY sentTime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                messages.add(new Notification(
                    rs.getInt("notificationID"),
                    userId,
                    rs.getString("message"),
                    rs.getString("notificationType"),
                    false,
                    rs.getTimestamp("sentTime").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // --- GETTER (Read All Messages) ---
    public List<Notification> getAllNotifications(int userId) {
        List<Notification> messages = new ArrayList<>();
        String sql = "SELECT message, sentTime, isRead, notificationID FROM Notification " +
                     "WHERE recipientID = ? ORDER BY sentTime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                messages.add(new Notification(
                    rs.getInt("notificationID"),
                    userId,
                    rs.getString("message"),
                    rs.getString("notificationType"),
                    rs.getBoolean("isRead"),
                    rs.getTimestamp("sentTime").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    //Read notification by ID
    public Notification getNotificationById(int notificationId) {
        String sql = "SELECT * FROM Notification WHERE notificationID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Notification(
                    rs.getInt("notificationID"),
                    rs.getInt("recipientID"),
                    rs.getString("message"),
                    rs.getString("notificationType"),
                    rs.getBoolean("isRead"),
                    rs.getTimestamp("sentTime").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null; // Notification not found
    }
}