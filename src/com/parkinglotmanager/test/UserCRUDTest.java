package com.parkinglotmanager.test;

import com.parkinglotmanager.dao.UserDAO;
import com.parkinglotmanager.model.User;
import java.util.List;

/**
 * Demonstrates full CRUD operations for User with MySQL database
 * This test shows:
 * - CREATE: Register new users
 * - READ: Get users by username, ID, or get all users
 * - UPDATE: Modify user information, password, or privilege
 * - DELETE: Remove users from database
 */
public class UserCRUDTest {

    public static void main(String[] args) {
        System.out.println("=== User CRUD Operations Test ===\n");

        UserDAO userDAO = new UserDAO();

        try {
            // ==========================================
            // CREATE - Register new users
            // ==========================================
            System.out.println("1. CREATE Operations:");
            System.out.println("---------------------");

            // Create a test user with hashed password
            User testUser = new User("testuser", "Test", "User", "test@example.com", "password123");
            String hashedPassword = testUser.getPasswordHash();

            int userId = userDAO.registerUser("testuser", "test@example.com", hashedPassword, false);
            if (userId > 0) {
                System.out.println("[OK] Created user 'testuser' with ID: " + userId);
            } else {
                System.out.println("[FAIL] Failed to create user (may already exist)");
            }

            // Check if username exists
            boolean exists = userDAO.usernameExists("testuser");
            System.out.println("[OK] Username 'testuser' exists: " + exists);
            System.out.println();

            // ==========================================
            // READ - Retrieve users
            // ==========================================
            System.out.println("2. READ Operations:");
            System.out.println("-------------------");

            // Get user by username
            User foundUser = userDAO.getUserByUsername("testuser");
            if (foundUser != null) {
                System.out.println("[OK] Found user by username: " + foundUser.getUsername());
                System.out.println("  - ID: " + foundUser.getId());
                System.out.println("  - Email: " + foundUser.getEmail());
                System.out.println("  - Type: " + foundUser.getClass().getSimpleName());
            }

            // Get user by ID
            if (userId > 0) {
                User userById = userDAO.getUserById(userId);
                if (userById != null) {
                    System.out.println("[OK] Found user by ID " + userId + ": " + userById.getUsername());
                }
            }

            // Get all users
            List<User> allUsers = userDAO.getAllUsers();
            System.out.println("[OK] Total users in database: " + allUsers.size()); // Get users by privilege
            List<User> clients = userDAO.getUsersByPrivilege("registered");
            List<User> admins = userDAO.getUsersByPrivilege("moderator");
            System.out.println("  - Registered users: " + clients.size());
            System.out.println("  - Moderators: " + admins.size());

            System.out.println();

            // ==========================================
            // UPDATE - Modify user data
            // ==========================================
            System.out.println("3. UPDATE Operations:");
            System.out.println("---------------------");

            if (userId > 0) {
                // Update user information
                boolean updated = userDAO.updateUser(userId, "testuser", "newemail@example.com", "registered");
                if (updated) {
                    System.out.println("✓ Updated user email to: newemail@example.com");
                }

                // Update password
                User newPassUser = new User("temp", "", "", "temp@test.com", "newpassword456");
                String newHash = newPassUser.getPasswordHash();
                boolean passUpdated = userDAO.updateUserPassword(userId, newHash);
                if (passUpdated) {
                    System.out.println("✓ Updated user password");
                }

                // Verify password change
                User verifyUser = userDAO.getUserById(userId);
                if (verifyUser != null && verifyUser.authenticate("newpassword456")) {
                    System.out.println("✓ Password authentication successful with new password");
                }

                // Update privilege to moderator
                boolean privUpdated = userDAO.updateUserPrivilege(userId, true);
                if (privUpdated) {
                    System.out.println("✓ Updated user privilege to moderator");
                }
            }

            System.out.println();

            // ==========================================
            // Display all users
            // ==========================================
            System.out.println("4. Current Users in Database:");
            System.out.println("-----------------------------");
            allUsers = userDAO.getAllUsers();
            for (User u : allUsers) {
                System.out.printf("  [%d] %s <%s> - %s\n",
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getClass().getSimpleName());
            }

            System.out.println();

            // ==========================================
            // DELETE - Remove user (optional - uncomment to test)
            // ==========================================
            System.out.println("5. DELETE Operation:");
            System.out.println("--------------------");
            System.out.println("⚠ Delete operation commented out to preserve test data");
            System.out.println("  Uncomment the code below to test user deletion");

            /*
             * if (userId > 0) {
             * boolean deleted = userDAO.deleteUser(userId);
             * if (deleted) {
             * System.out.println("✓ Deleted user with ID: " + userId);
             * 
             * // Verify deletion
             * User deletedUser = userDAO.getUserById(userId);
             * if (deletedUser == null) {
             * System.out.println("✓ Confirmed: User no longer exists in database");
             * }
             * }
             * }
             */

            System.out.println("\n=== CRUD Test Complete ===");

        } catch (Exception e) {
            System.err.println("Error during CRUD test:");
            e.printStackTrace();
        }
    }
}
