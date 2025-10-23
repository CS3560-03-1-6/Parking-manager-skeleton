-- ========================================
-- Parking Lot Manager System - Database Schema V2
-- Java 21 Compatible
-- Date: October 22, 2025
-- ========================================

-- Drop existing database if exists and create new one
DROP DATABASE IF EXISTS parking_lot_manager_db;
CREATE DATABASE parking_lot_manager_db;
USE parking_lot_manager_db;

-- ========================================
-- TABLE: parking_lots
-- Represents individual parking lot locations
-- ========================================
CREATE TABLE parking_lots (
    lot_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    lot_type ENUM('SURFACE', 'STRUCTURE') NOT NULL DEFAULT 'SURFACE',
    total_capacity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE',
    INDEX idx_lot_type (lot_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: parking_slots
-- Individual parking spaces within lots
-- ========================================
CREATE TABLE parking_slots (
    slot_id VARCHAR(50) PRIMARY KEY,
    lot_id VARCHAR(50) NOT NULL,
    slot_number INT NOT NULL,
    slot_type ENUM('CAR', 'MOTORCYCLE', 'HANDICAPPED', 'EV', 'COMPACT') NOT NULL DEFAULT 'CAR',
    occupied BOOLEAN DEFAULT FALSE,
    vehicle_type ENUM('CAR', 'MOTORCYCLE', 'EV', 'TRUCK') NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED', 'OUT_OF_SERVICE') DEFAULT 'AVAILABLE',
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    UNIQUE KEY unique_slot_per_lot (lot_id, slot_number),
    INDEX idx_lot_occupied (lot_id, occupied),
    INDEX idx_slot_type (slot_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: users
-- System users (Admin and Client)
-- ========================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_type ENUM('ADMIN', 'CLIENT') NOT NULL DEFAULT 'CLIENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: vehicles
-- Vehicle information and parking sessions
-- ========================================
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    vehicle_type ENUM('CAR', 'MOTORCYCLE', 'EV', 'TRUCK') NOT NULL,
    vehicle_make VARCHAR(50) NULL,
    user_id INT NULL,
    slot_id VARCHAR(50) NULL,
    entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exit_time TIMESTAMP NULL,
    fee DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('PENDING', 'PAID', 'REFUNDED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id) ON DELETE SET NULL,
    INDEX idx_license_plate (license_plate),
    INDEX idx_slot_id (slot_id),
    INDEX idx_entry_time (entry_time),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: user_reports
-- Crowdsourced parking availability reports
-- ========================================
CREATE TABLE user_reports (
    report_id VARCHAR(50) PRIMARY KEY,
    user_id INT NOT NULL,
    lot_id VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reported_available INT NOT NULL,
    confidence INT NOT NULL CHECK (confidence BETWEEN 0 AND 100),
    comments TEXT,
    verified BOOLEAN DEFAULT FALSE,
    verified_by INT NULL,
    verified_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    FOREIGN KEY (verified_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_lot_timestamp (lot_id, timestamp),
    INDEX idx_user_id (user_id),
    INDEX idx_verified (verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: sensors
-- IoT sensors deployed in parking lots
-- ========================================
CREATE TABLE sensors (
    sensor_id VARCHAR(50) PRIMARY KEY,
    lot_id VARCHAR(50) NOT NULL,
    slot_id VARCHAR(50) NULL,
    sensor_type ENUM('ULTRASONIC', 'CAMERA', 'LOOP') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'ERROR') DEFAULT 'ACTIVE',
    installed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_reading_at TIMESTAMP NULL,
    firmware_version VARCHAR(20) NULL,
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id) ON DELETE SET NULL,
    INDEX idx_lot_id (lot_id),
    INDEX idx_slot_id (slot_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: sensor_readings
-- Real-time data from parking sensors
-- ========================================
CREATE TABLE sensor_readings (
    reading_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    occupied BOOLEAN NOT NULL,
    confidence FLOAT DEFAULT 1.0 CHECK (confidence BETWEEN 0.0 AND 1.0),
    raw_data JSON NULL,
    FOREIGN KEY (sensor_id) REFERENCES sensors(sensor_id) ON DELETE CASCADE,
    INDEX idx_sensor_timestamp (sensor_id, timestamp),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: occupancy_reports
-- Aggregated occupancy data for lots
-- ========================================
CREATE TABLE occupancy_reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_available INT NOT NULL,
    total_spaces INT NOT NULL,
    confidence_score FLOAT DEFAULT 1.0 CHECK (confidence_score BETWEEN 0.0 AND 1.0),
    sensor_count INT DEFAULT 0,
    user_report_count INT DEFAULT 0,
    source_breakdown JSON NULL,
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    INDEX idx_lot_timestamp (lot_id, timestamp),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLE: pricing_rules
-- Dynamic pricing based on slot type and time
-- ========================================
CREATE TABLE pricing_rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,
    lot_id VARCHAR(50) NOT NULL,
    slot_type ENUM('CAR', 'MOTORCYCLE', 'HANDICAPPED', 'EV', 'COMPACT') NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    daily_max DECIMAL(10, 2) NULL,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    effective_until TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    INDEX idx_lot_type (lot_id, slot_type),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- SAMPLE DATA
-- Insert initial test data
-- ========================================

-- Insert parking lots
INSERT INTO parking_lots (lot_id, name, location, lot_type, total_capacity) VALUES
('LOT-001', 'Downtown Parking Structure', '123 Main St, Downtown', 'STRUCTURE', 200),
('LOT-002', 'Campus North Surface Lot', '456 University Ave', 'SURFACE', 150),
('LOT-003', 'Shopping Center Garage', '789 Mall Blvd', 'STRUCTURE', 500);

-- Insert parking slots for LOT-001
INSERT INTO parking_slots (slot_id, lot_id, slot_number, slot_type) VALUES
('LOT-001-001', 'LOT-001', 1, 'CAR'),
('LOT-001-002', 'LOT-001', 2, 'CAR'),
('LOT-001-003', 'LOT-001', 3, 'HANDICAPPED'),
('LOT-001-004', 'LOT-001', 4, 'EV'),
('LOT-001-005', 'LOT-001', 5, 'COMPACT'),
('LOT-001-006', 'LOT-001', 6, 'MOTORCYCLE'),
('LOT-001-007', 'LOT-001', 7, 'CAR'),
('LOT-001-008', 'LOT-001', 8, 'CAR'),
('LOT-001-009', 'LOT-001', 9, 'EV'),
('LOT-001-010', 'LOT-001', 10, 'CAR');

-- Insert parking slots for LOT-002
INSERT INTO parking_slots (slot_id, lot_id, slot_number, slot_type) VALUES
('LOT-002-001', 'LOT-002', 1, 'CAR'),
('LOT-002-002', 'LOT-002', 2, 'CAR'),
('LOT-002-003', 'LOT-002', 3, 'MOTORCYCLE'),
('LOT-002-004', 'LOT-002', 4, 'HANDICAPPED'),
('LOT-002-005', 'LOT-002', 5, 'CAR');

-- Insert admin user (password: admin123)
INSERT INTO users (first_name, last_name, email, password_hash, user_type) VALUES
('Admin', 'User', 'admin@parkinglot.com', '$2a$10$example_hash_admin123', 'ADMIN');

-- Insert client users (password: client123)
INSERT INTO users (first_name, last_name, email, password_hash, user_type) VALUES
('John', 'Doe', 'john.doe@email.com', '$2a$10$example_hash_client123', 'CLIENT'),
('Jane', 'Smith', 'jane.smith@email.com', '$2a$10$example_hash_client123', 'CLIENT');

-- Insert pricing rules
INSERT INTO pricing_rules (lot_id, slot_type, hourly_rate, daily_max) VALUES
('LOT-001', 'CAR', 5.00, 30.00),
('LOT-001', 'MOTORCYCLE', 3.00, 15.00),
('LOT-001', 'HANDICAPPED', 0.00, 0.00),
('LOT-001', 'EV', 4.00, 25.00),
('LOT-001', 'COMPACT', 4.50, 27.00),
('LOT-002', 'CAR', 3.00, 20.00),
('LOT-002', 'MOTORCYCLE', 2.00, 10.00),
('LOT-002', 'HANDICAPPED', 0.00, 0.00);

-- Insert sample sensors
INSERT INTO sensors (sensor_id, lot_id, slot_id, sensor_type) VALUES
('SENSOR-001', 'LOT-001', 'LOT-001-001', 'ULTRASONIC'),
('SENSOR-002', 'LOT-001', 'LOT-001-002', 'ULTRASONIC'),
('SENSOR-003', 'LOT-001', 'LOT-001-004', 'CAMERA'),
('SENSOR-004', 'LOT-002', NULL, 'CAMERA');

-- ========================================
-- VIEWS
-- Convenient views for common queries
-- ========================================

-- View: Current lot occupancy
CREATE VIEW v_lot_occupancy AS
SELECT 
    pl.lot_id,
    pl.name AS lot_name,
    pl.total_capacity,
    COUNT(ps.slot_id) AS total_slots,
    SUM(CASE WHEN ps.occupied = TRUE THEN 1 ELSE 0 END) AS occupied_slots,
    SUM(CASE WHEN ps.occupied = FALSE AND ps.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_slots,
    ROUND((SUM(CASE WHEN ps.occupied = TRUE THEN 1 ELSE 0 END) / COUNT(ps.slot_id)) * 100, 2) AS occupancy_percentage
FROM parking_lots pl
LEFT JOIN parking_slots ps ON pl.lot_id = ps.lot_id
WHERE pl.status = 'ACTIVE'
GROUP BY pl.lot_id, pl.name, pl.total_capacity;

-- View: Active parking sessions
CREATE VIEW v_active_sessions AS
SELECT 
    v.id,
    v.license_plate,
    v.vehicle_type,
    v.entry_time,
    TIMESTAMPDIFF(HOUR, v.entry_time, NOW()) AS hours_parked,
    ps.slot_id,
    ps.slot_number,
    ps.slot_type,
    pl.lot_id,
    pl.name AS lot_name,
    pr.hourly_rate,
    CEIL(TIMESTAMPDIFF(MINUTE, v.entry_time, NOW()) / 60.0) * pr.hourly_rate AS current_fee
FROM vehicles v
JOIN parking_slots ps ON v.slot_id = ps.slot_id
JOIN parking_lots pl ON ps.lot_id = pl.lot_id
LEFT JOIN pricing_rules pr ON pl.lot_id = pr.lot_id AND ps.slot_type = pr.slot_type AND pr.is_active = TRUE
WHERE v.exit_time IS NULL;

-- View: Slot availability by type
CREATE VIEW v_slot_availability_by_type AS
SELECT 
    pl.lot_id,
    pl.name AS lot_name,
    ps.slot_type,
    COUNT(ps.slot_id) AS total_slots,
    SUM(CASE WHEN ps.occupied = FALSE AND ps.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_slots
FROM parking_lots pl
JOIN parking_slots ps ON pl.lot_id = ps.lot_id
WHERE pl.status = 'ACTIVE'
GROUP BY pl.lot_id, pl.name, ps.slot_type;

-- ========================================
-- STORED PROCEDURES
-- Business logic procedures
-- ========================================

DELIMITER //

-- Procedure: Park a vehicle
CREATE PROCEDURE sp_park_vehicle(
    IN p_license_plate VARCHAR(20),
    IN p_vehicle_type VARCHAR(20),
    IN p_slot_id VARCHAR(50),
    IN p_user_id INT,
    OUT p_vehicle_id INT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_slot_occupied BOOLEAN;
    DECLARE v_slot_status VARCHAR(20);
    
    -- Check if slot is available
    SELECT occupied, status INTO v_slot_occupied, v_slot_status
    FROM parking_slots
    WHERE slot_id = p_slot_id;
    
    IF v_slot_occupied = TRUE OR v_slot_status != 'AVAILABLE' THEN
        SET p_result = 'ERROR: Slot is not available';
        SET p_vehicle_id = NULL;
    ELSE
        -- Insert vehicle record
        INSERT INTO vehicles (license_plate, vehicle_type, user_id, slot_id, entry_time)
        VALUES (p_license_plate, p_vehicle_type, p_user_id, p_slot_id, NOW());
        
        SET p_vehicle_id = LAST_INSERT_ID();
        
        -- Update slot status
        UPDATE parking_slots
        SET occupied = TRUE, vehicle_type = p_vehicle_type, status = 'OCCUPIED'
        WHERE slot_id = p_slot_id;
        
        SET p_result = 'SUCCESS';
    END IF;
END //

-- Procedure: Checkout vehicle
CREATE PROCEDURE sp_checkout_vehicle(
    IN p_vehicle_id INT,
    OUT p_fee DECIMAL(10, 2),
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_slot_id VARCHAR(50);
    DECLARE v_entry_time TIMESTAMP;
    DECLARE v_slot_type VARCHAR(20);
    DECLARE v_lot_id VARCHAR(50);
    DECLARE v_hourly_rate DECIMAL(10, 2);
    DECLARE v_hours_parked INT;
    
    -- Get vehicle and slot info
    SELECT v.slot_id, v.entry_time, ps.slot_type, ps.lot_id
    INTO v_slot_id, v_entry_time, v_slot_type, v_lot_id
    FROM vehicles v
    JOIN parking_slots ps ON v.slot_id = ps.slot_id
    WHERE v.id = p_vehicle_id AND v.exit_time IS NULL;
    
    IF v_slot_id IS NULL THEN
        SET p_result = 'ERROR: Vehicle not found or already checked out';
        SET p_fee = 0;
    ELSE
        -- Get pricing
        SELECT hourly_rate INTO v_hourly_rate
        FROM pricing_rules
        WHERE lot_id = v_lot_id AND slot_type = v_slot_type AND is_active = TRUE
        LIMIT 1;
        
        -- Calculate hours (minimum 1 hour)
        SET v_hours_parked = GREATEST(1, CEIL(TIMESTAMPDIFF(MINUTE, v_entry_time, NOW()) / 60.0));
        SET p_fee = v_hours_parked * COALESCE(v_hourly_rate, 5.00);
        
        -- Update vehicle record
        UPDATE vehicles
        SET exit_time = NOW(), fee = p_fee
        WHERE id = p_vehicle_id;
        
        -- Free up the slot
        UPDATE parking_slots
        SET occupied = FALSE, vehicle_type = NULL, status = 'AVAILABLE'
        WHERE slot_id = v_slot_id;
        
        SET p_result = 'SUCCESS';
    END IF;
END //

DELIMITER ;

-- ========================================
-- TRIGGERS
-- Automatic data maintenance
-- ========================================

DELIMITER //

-- Trigger: Update lot capacity when slots are added/removed
CREATE TRIGGER trg_update_lot_capacity_insert
AFTER INSERT ON parking_slots
FOR EACH ROW
BEGIN
    UPDATE parking_lots
    SET total_capacity = (
        SELECT COUNT(*) FROM parking_slots WHERE lot_id = NEW.lot_id
    )
    WHERE lot_id = NEW.lot_id;
END //

CREATE TRIGGER trg_update_lot_capacity_delete
AFTER DELETE ON parking_slots
FOR EACH ROW
BEGIN
    UPDATE parking_lots
    SET total_capacity = (
        SELECT COUNT(*) FROM parking_slots WHERE lot_id = OLD.lot_id
    )
    WHERE lot_id = OLD.lot_id;
END //

DELIMITER ;

-- ========================================
-- INDEXES for Performance
-- ========================================

-- Additional composite indexes for common queries
CREATE INDEX idx_vehicles_active ON vehicles(exit_time, slot_id) WHERE exit_time IS NULL;
CREATE INDEX idx_slots_available ON parking_slots(lot_id, occupied, status);

-- ========================================
-- GRANTS (Optional - for specific user)
-- ========================================

-- CREATE USER IF NOT EXISTS 'parking_app'@'localhost' IDENTIFIED BY 'secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON parking_lot_manager_db.* TO 'parking_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE parking_lot_manager_db.sp_park_vehicle TO 'parking_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE parking_lot_manager_db.sp_checkout_vehicle TO 'parking_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ========================================
-- END OF DATABASE SCHEMA
-- ========================================

SELECT 'Database parking_lot_manager_db created successfully!' AS Status;
SELECT * FROM v_lot_occupancy;
