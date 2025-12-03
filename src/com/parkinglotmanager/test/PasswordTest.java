package com.parkinglotmanager.test;

import com.parkinglotmanager.model.User;
import com.parkinglotmanager.dao.UserDAO;

/**
 * Test password hashing and authentication
 */
public class PasswordTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== Password Hashing Test ===\n");

            // Test 1: Generate password hashes
            System.out.println("1. Generating password hashes:");
            System.out.println("-------------------------------");

            User adminTest = new User("test", "", "", "test@test.com", "admin123");
            String adminHash = adminTest.getPasswordHash();
            System.out.println("Password: admin123");
            System.out.println("Hash: " + adminHash);
            System.out.println();

            User clientTest = new User("test2", "", "", "test2@test.com", "client123");
            String clientHash = clientTest.getPasswordHash();
            System.out.println("Password: client123");
            System.out.println("Hash: " + clientHash);
            System.out.println();

            // Test 2: Verify authentication works
            System.out.println("2. Testing authentication:");
            System.out.println("--------------------------");
            boolean adminAuth = adminTest.authenticate("admin123");
            System.out.println("Admin password 'admin123' validates: " + adminAuth);

            boolean clientAuth = clientTest.authenticate("client123");
            System.out.println("Client password 'client123' validates: " + clientAuth);
            System.out.println();

            // Test 3: Check database users
            System.out.println("3. Checking database users:");
            System.out.println("---------------------------");
            UserDAO dao = new UserDAO();

            User dbAdmin = dao.getUserByUsername("admin");
            if (dbAdmin != null) {
                System.out.println("Found 'admin' in database");
                System.out.println("  Email: " + dbAdmin.getEmail());
                System.out.println("  Hash (first 50 chars): "
                        + dbAdmin.getPasswordHash().substring(0, Math.min(50, dbAdmin.getPasswordHash().length())));

                // Try authenticating with different passwords
                System.out.println("\n  Testing passwords against DB admin:");
                testPassword(dbAdmin, "admin123");
                testPassword(dbAdmin, "admin");
                testPassword(dbAdmin, "password");
            } else {
                System.out.println("[WARN] User 'admin' not found in database");
            }

            System.out.println();

            User dbClient = dao.getUserByUsername("client");
            if (dbClient != null) {
                System.out.println("Found 'client' in database");
                System.out.println("  Email: " + dbClient.getEmail());
                System.out.println("  Hash (first 50 chars): "
                        + dbClient.getPasswordHash().substring(0, Math.min(50, dbClient.getPasswordHash().length())));

                // Try authenticating with different passwords
                System.out.println("\n  Testing passwords against DB client:");
                testPassword(dbClient, "client123");
                testPassword(dbClient, "client");
                testPassword(dbClient, "password");
            } else {
                System.out.println("[WARN] User 'client' not found in database");
            }

            System.out.println("\n=== Test Complete ===");
        } catch (Exception e) {
            System.err.println("Error during password test:");
            e.printStackTrace();
        }
    }

    private static void testPassword(User user, String password) {
        try {
            boolean result = user.authenticate(password);
            System.out.println("    Password '" + password + "': " + (result ? "[VALID]" : "[INVALID]"));
        } catch (Exception e) {
            System.out.println("    Password '" + password + "': [ERROR] " + e.getMessage());
        }
    }
}
