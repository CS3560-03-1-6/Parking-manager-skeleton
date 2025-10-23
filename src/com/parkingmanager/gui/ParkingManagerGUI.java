package com.parkingmanager.gui;

import com.parkingmanager.model.ParkingSpot;
import com.parkingmanager.model.Vehicle;
import com.parkingmanager.service.ParkingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Main GUI application for the Parking Management System.
 * Provides a user interface for managing parking spots and vehicles.
 */
public class ParkingManagerGUI extends JFrame {
    private ParkingService service;
    private DefaultTableModel spotTableModel;
    private DefaultTableModel vehicleTableModel;
    private JLabel availableLabel;
    private JLabel occupiedLabel;
    private JLabel revenueLabel;
    
    public ParkingManagerGUI() {
        service = new ParkingService();
        service.initializeSpots();
        setupUI();
        refreshData();
    }
    
    /**
     * Sets up the main user interface.
     */
    private void setupUI() {
        setTitle("Parking Manager System - MySQL Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createStatsPanel(), BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(createSpotsPanel());
        centerPanel.add(createVehiclesPanel());
        add(centerPanel, BorderLayout.CENTER);
        
        add(createActionsPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Creates the statistics panel showing available spots, occupied spots, and revenue.
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        availableLabel = new JLabel("Available: 0", SwingConstants.CENTER);
        availableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        availableLabel.setForeground(new Color(34, 139, 34));
        
        occupiedLabel = new JLabel("Occupied: 0", SwingConstants.CENTER);
        occupiedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        occupiedLabel.setForeground(new Color(220, 20, 60));
        
        revenueLabel = new JLabel("Revenue: $0.00", SwingConstants.CENTER);
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        revenueLabel.setForeground(new Color(25, 25, 112));
        
        panel.add(availableLabel);
        panel.add(occupiedLabel);
        panel.add(revenueLabel);
        
        return panel;
    }
    
    /**
     * Creates the parking spots table panel.
     */
    private JPanel createSpotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Parking Spots"));
        
        String[] columns = {"Spot #", "Type", "Status", "Rate/hr"};
        spotTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(spotTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the parked vehicles table panel.
     */
    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Parked Vehicles"));
        
        String[] columns = {"License", "Type", "Spot #", "Entry Time"};
        vehicleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(vehicleTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the action buttons panel.
     */
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton parkBtn = new JButton("Park Vehicle");
        parkBtn.addActionListener(e -> parkVehicle());
        
        JButton removeBtn = new JButton("Remove Vehicle");
        removeBtn.addActionListener(e -> removeVehicle());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        
        panel.add(parkBtn);
        panel.add(removeBtn);
        panel.add(refreshBtn);
        
        return panel;
    }
    
    /**
     * Handles parking a new vehicle.
     */
    private void parkVehicle() {
        JTextField licenseField = new JTextField(10);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Car", "Motorcycle", "SUV", "Truck"});
        JComboBox<Integer> spotBox = new JComboBox<>();
        
        for (ParkingSpot spot : service.getAllSpots()) {
            if (spot.isAvailable()) {
                spotBox.addItem(spot.getNumber());
            }
        }
        
        if (spotBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No available parking spots!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("License Plate:"));
        panel.add(licenseField);
        panel.add(new JLabel("Vehicle Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Spot Number:"));
        panel.add(spotBox);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Park Vehicle", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String license = licenseField.getText().trim().toUpperCase();
            if (license.isEmpty()) {
                JOptionPane.showMessageDialog(this, "License plate is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int spotNum = (Integer) spotBox.getSelectedItem();
            String type = (String) typeBox.getSelectedItem();
            
            if (service.parkVehicle(license, type, spotNum)) {
                JOptionPane.showMessageDialog(this, "Vehicle parked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to park vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handles removing a vehicle from parking.
     */
    private void removeVehicle() {
        var vehicles = service.getAllParkedVehicles();
        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No vehicles to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] licenses = vehicles.stream()
            .map(Vehicle::getLicensePlate)
            .toArray(String[]::new);
        
        String license = (String) JOptionPane.showInputDialog(this, "Select vehicle to remove:",
            "Remove Vehicle", JOptionPane.QUESTION_MESSAGE, null, licenses, licenses[0]);
        
        if (license != null) {
            double fee = service.removeVehicle(license);
            if (fee >= 0) {
                Vehicle vehicle = vehicles.stream()
                    .filter(v -> v.getLicensePlate().equals(license))
                    .findFirst()
                    .orElse(null);
                
                if (vehicle != null) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Vehicle removed!\nParked: %d hour(s)\nFee: $%.2f", 
                        vehicle.getParkedHours(), fee),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refreshes all data displayed in the GUI.
     */
    private void refreshData() {
        updateSpotTable();
        updateVehicleTable();
        updateStats();
    }
    
    /**
     * Updates the parking spots table.
     */
    private void updateSpotTable() {
        spotTableModel.setRowCount(0);
        for (ParkingSpot spot : service.getAllSpots()) {
            spotTableModel.addRow(new Object[]{
                spot.getNumber(),
                spot.getType(),
                spot.isAvailable() ? "Available" : "Occupied",
                "$" + spot.getRate()
            });
        }
    }
    
    /**
     * Updates the parked vehicles table.
     */
    private void updateVehicleTable() {
        vehicleTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        for (Vehicle vehicle : service.getAllParkedVehicles()) {
            vehicleTableModel.addRow(new Object[]{
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getSpotNumber(),
                vehicle.getEntryTime().format(formatter)
            });
        }
    }
    
    /**
     * Updates the statistics labels.
     */
    private void updateStats() {
        long available = service.getAvailableSpots();
        long total = service.getAllSpots().size();
        long occupied = total - available;
        double revenue = service.getTotalRevenue();
        
        availableLabel.setText("Available: " + available);
        occupiedLabel.setText("Occupied: " + occupied);
        revenueLabel.setText(String.format("Revenue: $%.2f", revenue));
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ParkingManagerGUI gui = new ParkingManagerGUI();
            gui.setVisible(true);
        });
    }
}