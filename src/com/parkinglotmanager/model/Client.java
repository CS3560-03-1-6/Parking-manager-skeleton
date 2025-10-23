package com.parkinglotmanager.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Client user representing regular parking lot customers.
 * Can view availability, submit reports, and park vehicles.
 */
public class Client extends User {

    /**
     * Constructor for creating a new client (before saving to database)
     */
    public Client(String username, String firstName, String lastName, String email, String passwordHash) {
        super(username, firstName, lastName, email, passwordHash);
    }

    /**
     * Constructor for loading client from database
     */
    public Client(int id, String username, String firstName, String lastName, String email,
            String passwordHash, LocalDateTime createdAt) {
        super(id, username, firstName, lastName, email, passwordHash, createdAt);
    }

    /**
     * Get list of actions available to client users
     */
    @Override
    public List<String> getActions() {
        return List.of(
                "VIEW_LOTS",
                "VIEW_AVAILABILITY",
                "PARK_VEHICLE",
                "SUBMIT_REPORT",
                "VIEW_MY_SESSIONS",
                "MAKE_PAYMENT");
    }

    @Override
    public String toString() {
        return String.format("Client{id=%d, name='%s', email='%s'}",
                getId(), getFullName(), getEmail());
    }
}
