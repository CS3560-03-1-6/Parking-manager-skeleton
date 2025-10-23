package com.parkinglotmanager.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database connection utility class for MySQL integration.
 * Handles database connection management and initialization.
 */
public class DatabaseConnection {
    private static final String CONFIG_FILE = "config.properties";
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_NAME;
    
    static {
        loadConfiguration();
    }
    
    /**
     * Load database configuration from properties file
     */
    private static void loadConfiguration() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            
            String host = props.getProperty("DB_HOST", "localhost");
            String port = props.getProperty("DB_PORT", "3306");
            DB_NAME = props.getProperty("DB_NAME", "parking_lot_manager_db");
            DB_USER = props.getProperty("DB_USER", "root");
            DB_PASSWORD = props.getProperty("DB_PASSWORD", "");
            
            DB_URL = "jdbc:mysql://" + host + ":" + port + "/" + DB_NAME + 
                    "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties. Using defaults.");
            // Default values
            DB_URL = "jdbc:mysql://localhost:3306/parking_lot_manager_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            DB_USER = "root";
            DB_PASSWORD = "";
            DB_NAME = "parking_lot_manager_db";
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database: " + DB_NAME);
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Make sure mysql-connector-j-*.jar is in classpath.", e);
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Test with a simple query
                Statement stmt = conn.createStatement();
                stmt.executeQuery("SELECT 1").close();
                stmt.close();
                conn.close();
                System.out.println("Database connection test successful!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database credentials in config.properties are correct");
            System.err.println("3. Database '" + DB_NAME + "' exists");
        }
        return false;
    }
    
    /**
     * Initialize database (create database if not exists)
     * @return true if successful, false otherwise
     */
    public static boolean initializeDatabase() {
        try {
            // First connect without specifying database
            String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            Connection baseConn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
            
            // Create database if not exists
            Statement stmt = baseConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.close();
            baseConn.close();
            
            System.out.println("Database initialization successful!");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get database configuration info
     * @return configuration string
     */
    public static String getConnectionInfo() {
        return "Database: " + DB_NAME + " | User: " + DB_USER + " | URL: " + DB_URL;
    }
}