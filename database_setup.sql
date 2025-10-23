-- Database setup script for Parking Management System
-- Run this script in MySQL to create the required database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS parking_db;
USE parking_db;

-- Create parking_spots table
CREATE TABLE IF NOT EXISTS parking_spots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    number INT NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    rate DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    spot_number INT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    fee DECIMAL(10,2) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (spot_number) REFERENCES parking_spots(number)
);

-- Create indexes for better performance
CREATE INDEX idx_license_plate ON vehicles(license_plate);
CREATE INDEX idx_spot_number ON vehicles(spot_number);
CREATE INDEX idx_entry_time ON vehicles(entry_time);
CREATE INDEX idx_available ON parking_spots(available);

-- Insert sample data (optional)
-- This will be created automatically by the application if no spots exist
/*
INSERT INTO parking_spots (number, type, rate) VALUES
(1, 'Regular', 5.00), (2, 'Regular', 5.00), (3, 'Regular', 5.00), (4, 'Regular', 5.00), (5, 'Regular', 5.00),
(6, 'Regular', 5.00), (7, 'Regular', 5.00), (8, 'Regular', 5.00), (9, 'Regular', 5.00), (10, 'Regular', 5.00),
(11, 'Premium', 8.00), (12, 'Premium', 8.00), (13, 'Premium', 8.00), (14, 'Premium', 8.00), (15, 'Premium', 8.00),
(16, 'Premium', 8.00), (17, 'Premium', 8.00), (18, 'Premium', 8.00), (19, 'Premium', 8.00), (20, 'Premium', 8.00);
*/