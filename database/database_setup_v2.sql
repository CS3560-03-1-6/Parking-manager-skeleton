-- Drop existing database if exists and create new one
CREATE DATABASE IF NOT EXISTS parking_lot_manager_db;
USE parking_lot_manager_db;


-- TABLE: User
-- System users (registered + moderators)
CREATE TABLE IF NOT EXISTS User (
    userID       INT NOT NULL AUTO_INCREMENT,
    userName     VARCHAR(50)  NOT NULL,
    userEmail    VARCHAR(100) NOT NULL,
    passwordHash VARCHAR(200) NOT NULL,
    privilege    VARCHAR(20)  NOT NULL,   -- e.g. 'registered', 'moderator'
    modID        INT NULL,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_user_email (userEmail)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Lot
-- Parking lots
CREATE TABLE IF NOT EXISTS Lot (
    lotID    INT NOT NULL AUTO_INCREMENT,
    lotName  VARCHAR(50) NOT NULL,
    capacity INT         NOT NULL,
    status   VARCHAR(20) NOT NULL,   -- e.g. 'Available', 'Full', 'Closed'
    PRIMARY KEY (lotID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Vehicle
-- Vehicles owned by users
CREATE TABLE IF NOT EXISTS Vehicle (
    vehicleID INT NOT NULL AUTO_INCREMENT,
    userID    INT NOT NULL,
    plate     VARCHAR(20) NOT NULL,
    make      VARCHAR(30),
    model     VARCHAR(30),
    color     VARCHAR(20),
    PRIMARY KEY (vehicleID),
    CONSTRAINT fk_vehicle_user
        FOREIGN KEY (userID) REFERENCES `User`(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: ParkingReport
-- Crowdsourced fullness reports per lot
CREATE TABLE IF NOT EXISTS ParkingReport (
    parkingReportID INT NOT NULL AUTO_INCREMENT,
    lotID           INT NOT NULL,
    userID          INT NOT NULL,
    fullness        INT NOT NULL,      -- 0–100
    reportTime      DATETIME NOT NULL,
    PRIMARY KEY (parkingReportID),
    CONSTRAINT fk_report_lot
        FOREIGN KEY (lotID) REFERENCES Lot(lotID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_report_user
        FOREIGN KEY (userID) REFERENCES `User`(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_report_lot_time (lotID, reportTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE: Notification
-- Messages sent to users
CREATE TABLE IF NOT EXISTS Notification (
    notificationID   INT NOT NULL AUTO_INCREMENT,
    recipientID      INT NOT NULL,
    message          VARCHAR(255) NOT NULL,
    notificationType VARCHAR(30)  NOT NULL,  -- e.g. 'FullReport', 'Alert'
    isRead           TINYINT(1)   NOT NULL DEFAULT 0,
    sentTime         DATETIME     NOT NULL,
    PRIMARY KEY (notificationID),
    CONSTRAINT fk_notification_user
        FOREIGN KEY (recipientID) REFERENCES `User`(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_notification_recipient (recipientID, isRead)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: ModerationAction
-- Actions taken by moderators on reports/lots
CREATE TABLE IF NOT EXISTS ModerationAction (
    moderationActionID INT NOT NULL AUTO_INCREMENT,
    modID              INT NOT NULL,
    actionType         VARCHAR(30)  NOT NULL,
    message            VARCHAR(255) NOT NULL,
    affectedReportID   INT NULL,
    affectedLotID      INT NULL,
    PRIMARY KEY (moderationActionID),
    -- FIX: Reference the User table, not the non-existent Moderator table
    CONSTRAINT fk_action_mod
        FOREIGN KEY (modID) REFERENCES `User`(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_action_report
        FOREIGN KEY (affectedReportID) REFERENCES ParkingReport(parkingReportID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_action_lot
        FOREIGN KEY (affectedLotID) REFERENCES Lot(lotID)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE: Log
-- System log entries
CREATE TABLE IF NOT EXISTS Log (
    logID             INT NOT NULL AUTO_INCREMENT,
    logType           VARCHAR(30)  NOT NULL,   -- e.g. 'login', 'moderation', 'report'
    logTime           DATETIME     NOT NULL,
    logMessage        VARCHAR(255) NOT NULL,
    userID            INT NULL,
    lotID             INT NULL,
    parkingReportID   INT NULL,
    moderationActionID INT NULL,
    notificationID    INT NULL,
    PRIMARY KEY (logID),
    CONSTRAINT fk_log_user
        FOREIGN KEY (userID) REFERENCES `User`(userID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_log_lot
        FOREIGN KEY (lotID) REFERENCES Lot(lotID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_log_report
        FOREIGN KEY (parkingReportID) REFERENCES ParkingReport(parkingReportID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_log_action
        FOREIGN KEY (moderationActionID) REFERENCES ModerationAction(moderationActionID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_log_notification
        FOREIGN KEY (notificationID) REFERENCES Notification(notificationID)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- SAMPLE DATA 
-- Users
-- Only run this sql once, multiple times will error out duplicate data
INSERT INTO User (userID, userName, userEmail, passwordHash, privilege)
VALUES
    (1, 'Gavin', 'gavin@cpp.edu', '(password)', 'registered'),
    (2, 'Alex',  'alex@cpp.edu',  '(password)', 'moderator');


-- Lot
INSERT INTO Lot (lotID, lotName, capacity, status) VALUES
    (1,'Structure1', 500, 'Available'),
    (2,'Structure2', 500, 'Available'),
    (3,'Lot B', 200, 'Available'),
    (4,'Lot E1', 150, 'Available'),
    (5,'Lot E2', 150, 'Available'),
    (6,'Lot F1', 120, 'Available'),
    (7,'Lot F10', 120, 'Available'),
    (8,'Lot F3', 120, 'Available'),
    (9,'Lot F5', 120, 'Available'),
    (10,'Lot F9', 120, 'Available'),
    (11,'Lot J', 220, 'Available'),
    (12,'Lot K', 180, 'Available'),
    (13,'Lot M', 180, 'Available'),
    (14,'Lot N', 180, 'Available'),
    (15,'Lot R', 160, 'Available'),
    (16,'Lot T', 160, 'Available'),
    (17,'Lot U', 140, 'Available');

-- Vehicle
INSERT INTO Vehicle (vehicleID, userID, plate, make, model, color)
VALUES
    (1, 1, '242DJF1048', 'Porche', '911', 'Red');

-- ParkingReport
INSERT INTO ParkingReport (parkingReportID, lotID, userID, fullness, reportTime)
VALUES
    (1, 1, 1, 80, '2025-11-20 09:32:12');

-- Notification
INSERT INTO Notification (notificationID, recipientID, message, notificationType, isRead, sentTime)
VALUES
    (1, 1, 'Lot F is 90 percent full...', 'FullReport', 1, '2025-11-20 09:33:34');

-- ModerationAction
INSERT INTO ModerationAction (moderationActionID, modID, actionType, message, affectedReportID, affectedLotID)
VALUES
    (1, 1, 'delete', 'Wrong report', NULL, NULL);

-- Log
INSERT INTO Log (logID, logType, logTime, logMessage, userID)
VALUES
    (1, 'login', '2025-11-20 08:25:57', 'userID 1 has logged in', 1);

SHOW DATABASES LIKE 'parking_lot_manager_db';
USE parking_lot_manager_db;
SHOW TABLES;
SELECT COUNT(*) FROM User;
SELECT COUNT(*) FROM ModerationAction;

-- ========================================
-- END – simple status message
-- ========================================
SELECT 'created successfully' AS Status;
