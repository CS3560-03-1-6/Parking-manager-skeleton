
-- Drop & recreate the database for re-run

DROP DATABASE IF EXISTS parking_lot_manager_db;
CREATE DATABASE parking_lot_manager_db;
USE parking_lot_manager_db;


-- TABLE: User
-- Stores system users 

CREATE TABLE User (
    userID       INT NOT NULL AUTO_INCREMENT,
    userName     VARCHAR(50)  NOT NULL,
    userEmail    VARCHAR(100) NOT NULL,
    passwordHash VARCHAR(200) NOT NULL,
    privilege    VARCHAR(20)  NOT NULL,   -- 'registered' or 'moderator'
    modID        INT NULL,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_user_email (userEmail)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Lot
-- Stores parking lot info 

CREATE TABLE Lot (
    lotID    INT NOT NULL AUTO_INCREMENT,
    lotName  VARCHAR(50) NOT NULL,
    capacity INT         NOT NULL,
    status   VARCHAR(20) NOT NULL,
    PRIMARY KEY (lotID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Vehicle
-- Stores vehicles owned by users

CREATE TABLE Vehicle (
    vehicleID INT NOT NULL AUTO_INCREMENT,
    userID    INT NOT NULL,
    plate     VARCHAR(20) NOT NULL,
    make      VARCHAR(30),
    model     VARCHAR(30),
    color     VARCHAR(20),
    PRIMARY KEY (vehicleID),
    CONSTRAINT fk_vehicle_user
        FOREIGN KEY (userID) REFERENCES User(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: ParkingReport
-- Stores crowd-sourced fullness reports

CREATE TABLE ParkingReport (
    parkingReportID INT NOT NULL AUTO_INCREMENT,
    lotID           INT NOT NULL,
    userID          INT NOT NULL,
    fullness        INT NOT NULL,     -- 0–100 percentage
    reportTime      DATETIME NOT NULL,
    PRIMARY KEY (parkingReportID),
    CONSTRAINT fk_report_lot
        FOREIGN KEY (lotID) REFERENCES Lot(lotID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_report_user
        FOREIGN KEY (userID) REFERENCES User(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_report_lot_time (lotID, reportTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Notification
-- Stores notifications sent to users

CREATE TABLE Notification (
    notificationID   INT NOT NULL AUTO_INCREMENT,
    recipientID      INT NOT NULL,
    message          VARCHAR(255) NOT NULL,
    notificationType VARCHAR(30)  NOT NULL,
    isRead           TINYINT(1)   NOT NULL DEFAULT 0,
    sentTime         DATETIME     NOT NULL,
    PRIMARY KEY (notificationID),
    CONSTRAINT fk_notification_user
        FOREIGN KEY (recipientID) REFERENCES User(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_notification_recipient (recipientID, isRead)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: ModerationAction
-- Stores moderator actions on reports/lots

CREATE TABLE ModerationAction (
    moderationActionID INT NOT NULL AUTO_INCREMENT,
    modID              INT NOT NULL,
    actionType         VARCHAR(30) NOT NULL,
    message            VARCHAR(255) NOT NULL,
    affectedReportID   INT NULL,
    affectedLotID      INT NULL,
    PRIMARY KEY (moderationActionID),
    CONSTRAINT fk_action_mod
        FOREIGN KEY (modID) REFERENCES User(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_action_report
        FOREIGN KEY (affectedReportID) REFERENCES ParkingReport(parkingReportID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_action_lot
        FOREIGN KEY (affectedLotID) REFERENCES Lot(lotID)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- TABLE: Log
-- Stores log entries for system actions

CREATE TABLE Log (
    logID              INT NOT NULL AUTO_INCREMENT,
    logType            VARCHAR(30)  NOT NULL,
    logTime            DATETIME     NOT NULL,
    logMessage         VARCHAR(255) NOT NULL,
    userID             INT NULL,
    lotID              INT NULL,
    parkingReportID    INT NULL,
    moderationActionID INT NULL,
    notificationID     INT NULL,
    PRIMARY KEY (logID),
    CONSTRAINT fk_log_user
        FOREIGN KEY (userID) REFERENCES User(userID)
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



--safe to rerun, Preloads the database with test accounts and parking lots


-- Users (Gavin = userID 1, Alex = userID 2)
INSERT INTO User (userName, userEmail, passwordHash, privilege) VALUES
    ('Gavin', 'gavin@cpp.edu', '(password)', 'registered'),
    ('Alex',  'alex@cpp.edu',  '(password)', 'moderator');

-- Parking lots
INSERT INTO Lot (lotName, capacity, status) VALUES
    ('Structure1', 500, 'Available'),
    ('Structure2', 500, 'Available'),
    ('Lot B', 200, 'Available'),
    ('Lot E1', 150, 'Available'),
    ('Lot E2', 150, 'Available'),
    ('Lot F1', 120, 'Available'),
    ('Lot F10', 120, 'Available'),
    ('Lot F3', 120, 'Available'),
    ('Lot F5', 120, 'Available'),
    ('Lot F9', 120, 'Available'),
    ('Lot J', 220, 'Available'),
    ('Lot K', 180, 'Available'),
    ('Lot M', 180, 'Available'),
    ('Lot N', 180, 'Available'),
    ('Lot R', 160, 'Available'),
    ('Lot T', 160, 'Available'),
    ('Lot U', 140, 'Available');

-- Vehicle for Gavin
INSERT INTO Vehicle (userID, plate, make, model, color) VALUES
    (1, '242DJF1048', 'Porche', '911', 'Red');

-- Parking report sample
INSERT INTO ParkingReport (lotID, userID, fullness, reportTime) VALUES
    (1, 1, 80, '2025-11-20 09:32:12');

-- Notification sample
INSERT INTO Notification (recipientID, message, notificationType, isRead, sentTime) VALUES
    (1, 'Lot F is 90 percent full...', 'FullReport', 1, '2025-11-20 09:33:34');

-- Moderator action by Alex
INSERT INTO ModerationAction (modID, actionType, message, affectedReportID, affectedLotID) VALUES
    (2, 'delete', 'Wrong report', NULL, NULL);

-- Log entry sample
INSERT INTO Log (logType, logTime, logMessage, userID) VALUES
    ('login', '2025-11-20 08:25:57', 'userID 1 has logged in', 1);


-- Status checks
SHOW DATABASES LIKE 'parking_lot_manager_db';
SHOW TABLES;
SELECT COUNT(*) AS user_count FROM User;
SELECT COUNT(*) AS moderation_action_count FROM ModerationAction;

SELECT 'created successfully' AS Status;
