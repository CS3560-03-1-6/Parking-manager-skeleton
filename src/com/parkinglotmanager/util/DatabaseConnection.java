package com.parkinglotmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    
    private static final String CONFIG_FILE = "config.properties";
    // Defaults
    private static String dbUrl = "jdbc:mysql://localhost:3306/parking_lot_manager_db";
    private static String dbUser = "root";
    private static String dbPassword = "";
    private static String dbName = "parking_lot_manager_db";
    private static String dbHost = "localhost";
    private static String dbPort = "3306";

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        // FIX 1: Use ClassLoader to find file in src/main/resources
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Warning: " + CONFIG_FILE + " not found in classpath. Using defaults.");
                return;
            }
            props.load(input);

            dbHost = props.getProperty("DB_HOST", "localhost");
            dbPort = props.getProperty("DB_PORT", "3306");
            dbName = props.getProperty("DB_NAME", "parking_lot_manager_db");
            dbUser = props.getProperty("DB_USER", "root");
            dbPassword = props.getProperty("DB_PASSWORD", "");

            // Construct URL dynamically
            dbUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                    dbHost, dbPort, dbName);

        } catch (IOException e) {
            System.err.println("Error loading config. Using defaults.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        // FIX 3: Removed Class.forName (unnecessary)
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static boolean initializeDatabase() {
        try {
            // FIX 2: Use the loaded variables, not hardcoded localhost
            String baseUrl = String.format("jdbc:mysql://%s:%s/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                    dbHost, dbPort);
            
            try (Connection baseConn = DriverManager.getConnection(baseUrl, dbUser, dbPassword);
                 Statement stmt = baseConn.createStatement()) {
                 
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                System.out.println("Database check/creation successful for: " + dbName);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    // Lightweight test using standard JDBC 4 method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2); // Timeout in 2 seconds
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
}