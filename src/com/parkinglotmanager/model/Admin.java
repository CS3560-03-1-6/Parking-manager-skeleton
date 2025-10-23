package com.parkinglotmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<String> actions = new ArrayList<>();
        actions.add("VIEW_LOTS");
        actions.add("VIEW_AVAILABILITY");
        actions.add("MANAGE_LOTS");
        actions.add("MANAGE_SLOTS");
        actions.add("MANAGE_PRICING");
        actions.add("VIEW_REPORTS");
        actions.add("MANAGE_USERS");
        actions.add("MANAGE_SENSORS");
        actions.add("VIEW_ANALYTICS");
        actions.add("SYSTEM_CONFIGURATION");
        return actions;
    }

    @Override
    public String toString() {
        return String.format("Admin{id=%d, name='%s', email='%s'}",
                getId(), getFullName(), getEmail());
    }
}
