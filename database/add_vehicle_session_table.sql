-- Add VehicleSession table
CREATE TABLE IF NOT EXISTS VehicleSession (
    sessionID      INT NOT NULL AUTO_INCREMENT,
    licensePlate   VARCHAR(20) NOT NULL,
    vehicleType    VARCHAR(20),
    vehicleMake    VARCHAR(50),
    userID         INT NOT NULL,
    slotID         VARCHAR(20),
    entryTime      DATETIME NOT NULL,
    exitTime       DATETIME NULL,
    fee            DECIMAL(10,2) DEFAULT 0.00,
    paymentStatus  VARCHAR(20) DEFAULT 'PENDING',
    PRIMARY KEY (sessionID),
    CONSTRAINT fk_session_user
        FOREIGN KEY (userID) REFERENCES `User`(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_session_user (userID),
    INDEX idx_session_active (exitTime),
    INDEX idx_session_entry (entryTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
