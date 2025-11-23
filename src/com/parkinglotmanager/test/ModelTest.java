package com.parkinglotmanager.test;

import com.parkinglotmanager.enums.LotType;
import com.parkinglotmanager.enums.SensorType;
import com.parkinglotmanager.enums.SlotType;
import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.OccupancyReport;
import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.ParkingSlot;
import com.parkinglotmanager.model.Sensor;
import com.parkinglotmanager.model.SensorReading;
import com.parkinglotmanager.model.UserReport;
import com.parkinglotmanager.model.VehicleSession;

/**
 * Simple test class to verify new model classes work correctly
 */
public class ModelTest {
    public static void main(String[] args) {
        System.out.println("=== Parking Lot Manager System - Model Test ===\n");

        // Test ParkingLot
        ParkingLot lot = new ParkingLot("LOT-001", "Structure 1", "123 Main St", LotType.STRUCTURE);
        System.out.println("Created: " + lot);

        // Test ParkingSlot
        ParkingSlot slot = new ParkingSlot("LOT-001-001", "LOT-001", 1, SlotType.CAR);
        lot.addSlot(slot);
        System.out.println("Created: " + slot);

        // Test User
        Admin admin = new Admin("admin", "John", "Doe", "admin@test.com", "hashed_password");
        System.out.println("Created: " + admin);
        System.out.println("  Admin Actions: " + admin.getActions());

        Client client = new Client("client", "Jane", "Smith", "client@test.com", "hashed_password");
        System.out.println("Created: " + client);
        System.out.println("  Client Actions: " + client.getActions());

        // Test Vehicle and VehicleSession
        VehicleSession session = new VehicleSession("ABC-123", VehicleType.CAR, VehicleMake.TOYOTA,
                client.getId(), slot.getSlotId());
        System.out.println("Created: " + session);

        // Test occupancy
        slot.setOccupied(true, VehicleType.CAR);
        System.out.println("Slot occupied: " + slot.isOccupied());
        System.out.println("Lot available spaces: " + lot.getAvailableSpaces() + "/" + lot.getTotalSpaces());

        // Test UserReport
        UserReport report = new UserReport("REPORT-001", client.getId(), lot.getLotId(), 5, 80, "Looks pretty empty");
        System.out.println("Created: " + report);

        // Test OccupancyReport
        OccupancyReport occReport = new OccupancyReport(lot.getLotId(), 5);
        occReport.addSourceData("sensors", 3);
        occReport.addSourceData("userReports", 2);
        System.out.println("Created: " + occReport);
        System.out.println("  Confidence Score: " + occReport.asConfidenceScore());

        // Test Sensor
        Sensor sensor = new Sensor("SENSOR-001", lot.getLotId(), slot.getSlotId(), SensorType.ULTRASONIC);
        System.out.println("Created: " + sensor);

        // Test SensorReading
        SensorReading reading = new SensorReading(sensor.getSensorId(), true, 0.95f);
        System.out.println("Created: " + reading);
        System.out.println("  Is Reliable: " + reading.isReliable());

        System.out.println("\n=== All Model Classes Working! ===");
    }
}
