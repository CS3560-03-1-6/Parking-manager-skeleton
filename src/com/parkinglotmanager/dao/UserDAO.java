package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Tries to find a user in MySQL by username.
     * Returns null if not found.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE userName = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // We found the user! Now convert SQL row -> Java Object
                String role = rs.getString("privilege"); // 'registered' or 'moderator'
                User user;
                
                try {
                    if ("moderator".equalsIgnoreCase(role)) {
                    user = new Admin(
                        rs.getString("userName"),
                        "FirstMod", "LastMod", // (We didn't add First/Last to DB yet, using placeholders)
                        rs.getString("userEmail"),
                        rs.getString("passwordHash")
                    );
                    } else {
                        user = new Client(
                            rs.getString("userName"),
                            "FirstClient", "LastClient",
                            rs.getString("userEmail"),
                            rs.getString("passwordHash")
                        );
                    }
                    user.setId(rs.getInt("userID"));
                    return user;
                
                } catch (Exception e) {
                    e.printStackTrace();
                }

                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found
    }

    /**
     * Saves a new user to MySQL.
     */

    //Bruh it doesn't put in DB the name
    public boolean registerUser(String username, String email, String password, boolean isAdmin) {
        String sql = "INSERT INTO User (userName, userEmail, passwordHash, privilege) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, isAdmin ? "moderator" : "registered");
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // True if successful
            
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
}
