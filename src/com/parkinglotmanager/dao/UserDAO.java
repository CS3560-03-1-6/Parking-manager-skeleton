package com.parkinglotmanager.dao;

import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Full CRUD operations for User table
 * CREATE: registerUser()
 * READ: getUserByUsername(), getUserById(), getAllUsers()
 * UPDATE: updateUser(), updateUserPassword()
 * DELETE: deleteUser()
 */
public class UserDAO {

    // ==========================================
    // CREATE Operations
    // ==========================================

    /**
     * Saves a new user to MySQL (CREATE)
     * Returns the generated user ID, or -1 if failed
     */
    public int registerUser(String username, String email, String passwordHash, boolean isAdmin) {
        String sql = "INSERT INTO User (userName, userEmail, passwordHash, privilege) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, isAdmin ? "moderator" : "registered");

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the auto-generated user ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Failed
    }

    // ==========================================
    // READ Operations
    // ==========================================

    /**
     * Finds a user in MySQL by username (READ)
     * Returns null if not found
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE userName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a user in MySQL by ID (READ)
     * Returns null if not found
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM User WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all users from database (READ)
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User ORDER BY userID";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Gets all users by privilege level (READ)
     */
    public List<User> getUsersByPrivilege(String privilege) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE privilege = ? ORDER BY userID";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, privilege);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users by privilege: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // ==========================================
    // UPDATE Operations
    // ==========================================

    /**
     * Updates user information (UPDATE)
     * Does not update password - use updateUserPassword() for that
     */
    public boolean updateUser(int userId, String username, String email, String privilege) {
        String sql = "UPDATE User SET userName = ?, userEmail = ?, privilege = ? WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, privilege);
            stmt.setInt(4, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Update user failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user password only (UPDATE)
     */
    public boolean updateUserPassword(int userId, String newPasswordHash) {
        String sql = "UPDATE User SET passwordHash = ? WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Update password failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user privilege/role (UPDATE)
     */
    public boolean updateUserPrivilege(int userId, boolean isAdmin) {
        String sql = "UPDATE User SET privilege = ? WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isAdmin ? "moderator" : "registered");
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Update privilege failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // DELETE Operations
    // ==========================================

    /**
     * Deletes a user from database (DELETE)
     * Warning: This will cascade delete related records
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM User WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Delete user failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a username already exists (utility method)
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM User WHERE userName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if an email already exists (utility method)
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM User WHERE userEmail = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    /**
     * Maps ResultSet row to User object
     */
    private User mapResultSetToUser(ResultSet rs) {
        try {
            String role = rs.getString("privilege");
            User user;

            // Use the constructor that takes passwordHash directly (not the one that hashes
            // it)
            if ("moderator".equalsIgnoreCase(role)) {
                user = new Admin(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        "Admin", "User",
                        rs.getString("userEmail"),
                        rs.getString("passwordHash"), // This is already hashed
                        null); // createdAt - we don't have this in current DB schema
            } else {
                user = new Client(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        "Client", "User",
                        rs.getString("userEmail"),
                        rs.getString("passwordHash"), // This is already hashed
                        null); // createdAt
            }
            // ID is already set by the constructor above
            return user;

        } catch (Exception e) {
            System.err.println("Error mapping user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
