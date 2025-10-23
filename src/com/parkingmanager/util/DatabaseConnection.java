package com.parkingmanager.util;

import java.sql.*;

/**
 * Database connection utility class for the parking management system.
 * Handles MySQL database connections using singleton pattern.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Jimmyhanh99!";
    private static Connection connection = null;
    
    /**
     * Gets a database connection. Creates a new one if none exists or if the current one is closed.
     * @return Database connection or null if connection fails
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Closes the database connection if it exists and is open.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}