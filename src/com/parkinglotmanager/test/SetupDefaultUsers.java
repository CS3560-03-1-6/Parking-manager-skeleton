package com.parkinglotmanager.test;

import com.parkinglotmanager.model.User;
import com.parkinglotmanager.dao.UserDAO;

/**
 * Setup default admin and client users with correct password hashing
 */
public class SetupDefaultUsers {

    public static void main(String[] args) {
        try {
            System.out.println("=== Setting up default users ===\n");

            UserDAO dao = new UserDAO();

            // Create admin user with password: admin123
            System.out.println("Creating admin user...");
            User adminUser = new User("admin", "Admin", "User", "admin@cpp.edu", "admin123");
            String adminHash = adminUser.getPasswordHash();

            int adminId = dao.registerUser("admin", "admin@cpp.edu", adminHash, true);
            if (adminId > 0) {
                System.out.println("[OK] Admin user created successfully");
                System.out.println("  Username: admin");
                System.out.println("  Password: admin123");
                System.out.println("  Email: admin@cpp.edu");
                System.out.println("  ID: " + adminId);
            } else {
                System.out.println("[FAIL] Failed to create admin user");
            }

            System.out.println();

            // Create client user with password: client123
            System.out.println("Creating client user...");
            User clientUser = new User("client", "Client", "User", "client@cpp.edu", "client123");
            String clientHash = clientUser.getPasswordHash();

            int clientId = dao.registerUser("client", "client@cpp.edu", clientHash, false);
            if (clientId > 0) {
                System.out.println("[OK] Client user created successfully");
                System.out.println("  Username: client");
                System.out.println("  Password: client123");
                System.out.println("  Email: client@cpp.edu");
                System.out.println("  ID: " + clientId);
            } else {
                System.out.println("[FAIL] Failed to create client user");
            }

            System.out.println();

            // Verify by testing login
            System.out.println("Verifying authentication...");
            User verifyAdmin = dao.getUserByUsername("admin");
            if (verifyAdmin != null && verifyAdmin.authenticate("admin123")) {
                System.out.println("✓ Admin login test: SUCCESS");
            } else {
                System.out.println("✗ Admin login test: FAILED");
            }

            User verifyClient = dao.getUserByUsername("client");
            if (verifyClient != null && verifyClient.authenticate("client123")) {
                System.out.println("✓ Client login test: SUCCESS");
            } else {
                System.out.println("✗ Client login test: FAILED");
            }

            System.out.println("\n=== Setup Complete ===");
            System.out.println("\nYou can now login with:");
            System.out.println("  Admin:  username=admin,  password=admin123");
            System.out.println("  Client: username=client, password=client123");

        } catch (Exception e) {
            System.err.println("Error during setup:");
            e.printStackTrace();
        }
    }
}
