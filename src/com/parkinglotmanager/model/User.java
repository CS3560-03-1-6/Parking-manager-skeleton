
package com.parkinglotmanager.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Base User class representing system users in the parking lot management
 * system.
 * Can be extended by Admin and Client subclasses.
 */
public class User {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    /**
     * Constructor for creating a new user (before saving to database)
     */
    public User(String username, String firstName, String lastName, String email, String password) throws Exception {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setpassword(password);
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor for loading user from database
     */
    public User(int id, String username, String firstName, String lastName, String email, String password,
            LocalDateTime createdAt) throws Exception {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = password;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get full name (first + last)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setpassword(String password) throws Exception {
        this.passwordHash = hashingPassword(password);
    }

    private String hashingPassword(String password) throws Exception {
        int iterations = 65536;
        int keyLength = 256;

        byte[] salt = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Authenticate user with provided password
     * Note: In production, use proper password hashing (BCrypt, Argon2, etc.)
     */
    public boolean authenticate(String password) throws Exception {
        try {
            String[] parts = passwordHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            int iterations = 65536;
            int keyLength = 256;

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] verifyHash = skf.generateSecret(spec).getEncoded();

            return Arrays.equals(storedHash, verifyHash);
        } catch (Exception e) {
            System.err.println("error");
            return false;
        }
    }

    /**
     * Get list of actions available to this user type
     * Override in subclasses for specific permissions
     */
    public java.util.List<String> getActions() {
        java.util.List<String> actions = new java.util.ArrayList<>();
        actions.add("VIEW_LOTS");
        actions.add("VIEW_AVAILABILITY");
        return actions;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s'}", id, getFullName(), email);
    }
}
