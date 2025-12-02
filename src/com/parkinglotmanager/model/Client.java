package com.parkinglotmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Client user representing regular parking lot customers.
 * Can view availability, submit reports, and park vehicles.
 */
public class Client extends User {

    /**
     * Constructor for creating a new client (before saving to database)
     */
    public Client(String username, String firstName, String lastName, String email, String password) throws Exception {
        super(username, firstName, lastName, email, password);
    }

    /**
     * Constructor for loading client from database
     */
    public Client(int id, String username, String firstName, String lastName, String email,
            String password, LocalDateTime createdAt) throws Exception{
        super(id, username, firstName, lastName, email, password, createdAt);
    }

    /**
     * Get list of actions available to client users
     */
    @Override
    public List<String> getActions() {
        List<String> actions = new ArrayList<>();
        actions.add("VIEW_LOTS");
        actions.add("VIEW_AVAILABILITY");
        actions.add("PARK_VEHICLE");
        actions.add("SUBMIT_REPORT");
        actions.add("VIEW_MY_SESSIONS");
        actions.add("MAKE_PAYMENT");
        return actions;
    }

    @Override
    public String toString() {
        return String.format("Client{id=%d, name='%s', email='%s'}",
                getId(), getFullName(), getEmail());
    }
}
