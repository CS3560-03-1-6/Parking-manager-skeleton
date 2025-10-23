package com.parkinglotmanager.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin user with elevated privileges in the parking lot management system.
 * Can manage lots, slots, pricing, and view reports.
 */
public class Admin extends User {

    /**
     * Constructor for creating a new admin (before saving to database)
     */
    public Admin(String username, String firstName, String lastName, String email, String passwordHash) {
        super(username, firstName, lastName, email, passwordHash);
    }

    /**
     * Constructor for loading admin from database
     */
    public Admin(int id, String username, String firstName, String lastName, String email,
            String passwordHash, LocalDateTime createdAt) {
        super(id, username, firstName, lastName, email, passwordHash, createdAt);
    }

    /**
     * Get list of actions available to admin users
     */
    @Override
    public List<String> getActions() {
        return List.of(
                "VIEW_LOTS",
                "VIEW_AVAILABILITY",
                "MANAGE_LOTS",
                "MANAGE_SLOTS",
                "MANAGE_PRICING",
                "VIEW_REPORTS",
                "MANAGE_USERS",
                "MANAGE_SENSORS",
                "VIEW_ANALYTICS",
                "SYSTEM_CONFIGURATION");
    }

    @Override
    public String toString() {
        return String.format("Admin{id=%d, name='%s', email='%s'}",
                getId(), getFullName(), getEmail());
    }
}
