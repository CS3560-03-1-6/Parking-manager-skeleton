package com.parkinglotmanager.gui;

import com.parkinglotmanager.enums.*;
import com.parkinglotmanager.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI application for the Parking Lot Management System.
 * Provides a user interface for managing parking lots, slots, and sessions.
 */
public class ParkingLotManagerGUI extends JFrame {
    private List<ParkingLot> parkingLots;
    private List<VehicleSession> activeSessions;
    private DefaultTableModel slotTableModel;
    private DefaultTableModel sessionTableModel;
    private JLabel totalSlotsLabel;
    private JLabel availableSlotsLabel;
    private JLabel occupiedSlotsLabel;
    private JComboBox<String> lotSelector;
    private User currentUser;
    private boolean isAdmin;

    public ParkingLotManagerGUI(User user) {
        this.currentUser = user;
        this.isAdmin = (user instanceof Admin);
        initializeData();
        setupUI();
        refreshData();
    }

    public ParkingLotManagerGUI() {
        // Default constructor for backward compatibility
        this.currentUser = new Client("demo", "Demo", "User", "demo@parking.com", "hashed_demo");
        this.isAdmin = false;
        initializeData();
        setupUI();
        refreshData();
    }

    /**
     * Initialize sample data for demonstration
     */
    private void initializeData() {
        parkingLots = new ArrayList<>();
        activeSessions = new ArrayList<>();

        // Create a sample parking lot
        ParkingLot downtown = new ParkingLot("LOT-001", "Downtown Garage", "123 Main St", LotType.STRUCTURE);

        // Add some parking slots
        downtown.addSlot(new ParkingSlot("SLOT-001", "LOT-001", 1, SlotType.CAR));
        downtown.addSlot(new ParkingSlot("SLOT-002", "LOT-001", 2, SlotType.CAR));
        downtown.addSlot(new ParkingSlot("SLOT-003", "LOT-001", 3, SlotType.MOTORCYCLE));
        downtown.addSlot(new ParkingSlot("SLOT-004", "LOT-001", 4, SlotType.EV));
        downtown.addSlot(new ParkingSlot("SLOT-005", "LOT-001", 5, SlotType.HANDICAPPED));
        downtown.addSlot(new ParkingSlot("SLOT-006", "LOT-001", 6, SlotType.CAR));
        downtown.addSlot(new ParkingSlot("SLOT-007", "LOT-001", 7, SlotType.CAR));
        downtown.addSlot(new ParkingSlot("SLOT-008", "LOT-001", 8, SlotType.COMPACT));
        downtown.addSlot(new ParkingSlot("SLOT-009", "LOT-001", 9, SlotType.EV));
        downtown.addSlot(new ParkingSlot("SLOT-010", "LOT-001", 10, SlotType.MOTORCYCLE));

        parkingLots.add(downtown);

        // Remove sample user creation - will be set via constructor
        if (currentUser == null) {
            currentUser = new Client("demo", "Demo", "User", "demo@parking.com", "hashed_password");
        }
    }

    /**
     * Sets up the main user interface
     */
    private void setupUI() {
        setTitle("Parking Lot Manager System - Java + MySQL Edition");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Set Java icon for the application window
        try {
            java.awt.image.BufferedImage iconImage = javax.imageio.ImageIO.read(new java.io.File("Resources/java-icon.png"));
            setIconImage(iconImage);
        } catch (java.io.IOException e) {
            // If icon loading fails, continue without icon
            System.out.println("Could not load Java icon: " + e.getMessage());
        }

        // Add panels
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);

        // Center the window
        setLocationRelativeTo(null);
    }

    /**
     * Creates the header panel with lot selector and stats
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Parking Lot Manager System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Lot selector and stats
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel selectLotLabel = new JLabel("Select Lot:");
        selectLotLabel.setForeground(Color.BLACK);
        controlPanel.add(selectLotLabel);

        lotSelector = new JComboBox<>();
        for (ParkingLot lot : parkingLots) {
            lotSelector.addItem(lot.getName() + " (" + lot.getLotId() + ")");
        }
        lotSelector.addActionListener(e -> refreshData());
        controlPanel.add(lotSelector);

        // Stats labels
        totalSlotsLabel = new JLabel("Total: 0");
        availableSlotsLabel = new JLabel("Available: 0");
        occupiedSlotsLabel = new JLabel("Occupied: 0");

        totalSlotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalSlotsLabel.setForeground(Color.BLACK);
        availableSlotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        availableSlotsLabel.setForeground(new Color(0, 150, 0));
        occupiedSlotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        occupiedSlotsLabel.setForeground(new Color(200, 0, 0));

        controlPanel.add(Box.createHorizontalStrut(30));
        controlPanel.add(totalSlotsLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(availableSlotsLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(occupiedSlotsLabel);

        panel.add(controlPanel, BorderLayout.CENTER);

        // User info and database status
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getFullName() +
                " (" + (isAdmin ? "Administrator" : "Client") + ")");
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setForeground(Color.BLACK);
        
        // Database connection status
        JLabel dbStatusLabel = new JLabel();
        try {
            com.parkinglotmanager.util.DatabaseConnection.testConnection();
            dbStatusLabel.setText("🟢 MySQL Connected");
            dbStatusLabel.setForeground(new Color(0, 150, 0));
        } catch (Exception e) {
            dbStatusLabel.setText("🔴 MySQL Disconnected");
            dbStatusLabel.setForeground(Color.RED);
        }
        dbStatusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        infoPanel.add(dbStatusLabel, BorderLayout.WEST);
        infoPanel.add(userLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the main panel with slots and sessions tables
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Slots panel
        panel.add(createSlotsPanel());

        // Sessions panel
        panel.add(createSessionsPanel());

        return panel;
    }

    /**
     * Creates the parking slots panel
     */
    private JPanel createSlotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Parking Slots"));

        // Table
        String[] columnNames = { "Slot #", "Type", "Status", "Vehicle" };
        slotTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(slotTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the active sessions panel
     */
    private JPanel createSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Active Parking Sessions"));

        // Table
        String[] columnNames = { "Session ID", "License Plate", "Vehicle Type", "Slot", "Duration" };
        sessionTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(sessionTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the actions panel with buttons
     */
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton parkButton = new JButton("Park Vehicle");
        parkButton.setOpaque(true);
        parkButton.setBorderPainted(true);
        parkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        parkButton.addActionListener(e -> parkVehicle());

        JButton exitButton = new JButton("Exit Vehicle");
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(true);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exitButton.addActionListener(e -> exitVehicle());

        JButton reportButton = new JButton("Submit Availability Report");
        reportButton.setOpaque(true);
        reportButton.setBorderPainted(true);
        reportButton.setFont(new Font("Arial", Font.PLAIN, 12));
        reportButton.addActionListener(e -> submitReport());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(true);
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshData());

        // Admin-only buttons
        JButton manageSlotsButton = new JButton("Manage Slots");
        manageSlotsButton.addActionListener(e -> manageSlots());
        manageSlotsButton.setBackground(new Color(230, 126, 34));
        manageSlotsButton.setForeground(Color.WHITE);
        manageSlotsButton.setOpaque(true);
        manageSlotsButton.setBorderPainted(false);
        manageSlotsButton.setFont(new Font("Arial", Font.BOLD, 12));
        manageSlotsButton.setFocusPainted(false);

        JButton viewReportsButton = new JButton("View Reports");
        viewReportsButton.addActionListener(e -> viewAllReports());
        viewReportsButton.setBackground(new Color(155, 89, 182));
        viewReportsButton.setForeground(Color.WHITE);
        viewReportsButton.setOpaque(true);
        viewReportsButton.setBorderPainted(false);
        viewReportsButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewReportsButton.setFocusPainted(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);

        panel.add(parkButton);
        panel.add(exitButton);
        panel.add(reportButton);

        // Only show admin buttons for admin users
        if (isAdmin) {
            panel.add(manageSlotsButton);
            panel.add(viewReportsButton);
        }

        panel.add(refreshButton);
        panel.add(logoutButton);

        return panel;
    }

    /**
     * Park a vehicle in an available slot
     */
    private void parkVehicle() {
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;

        // Find available slots
        List<ParkingSlot> availableSlots = new ArrayList<>();
        for (ParkingSlot slot : currentLot.getSlots()) {
            if (!slot.isOccupied()) {
                availableSlots.add(slot);
            }
        }

        if (availableSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available slots in this lot!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show dialog to enter vehicle info
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField plateField = new JTextField();
        JComboBox<VehicleType> typeCombo = new JComboBox<>(VehicleType.values());
        JComboBox<VehicleMake> makeCombo = new JComboBox<>(VehicleMake.values());

        inputPanel.add(new JLabel("License Plate:"));
        inputPanel.add(plateField);
        inputPanel.add(new JLabel("Vehicle Type:"));
        inputPanel.add(typeCombo);
        inputPanel.add(new JLabel("Vehicle Make:"));
        inputPanel.add(makeCombo);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Park Vehicle", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String plate = plateField.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "License plate cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            VehicleType vehicleType = (VehicleType) typeCombo.getSelectedItem();
            VehicleMake vehicleMake = (VehicleMake) makeCombo.getSelectedItem();

            // Find compatible slot
            ParkingSlot assignedSlot = null;
            for (ParkingSlot slot : availableSlots) {
                if (slot.getSlotType().isCompatibleWith(vehicleType)) {
                    assignedSlot = slot;
                    break;
                }
            }

            if (assignedSlot == null) {
                JOptionPane.showMessageDialog(this,
                        "No compatible slots available for " + vehicleType + "!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create session and occupy slot
            VehicleSession session = new VehicleSession(plate, vehicleType, vehicleMake,
                    currentUser.getId(), assignedSlot.getSlotId());
            activeSessions.add(session);
            assignedSlot.setOccupied(true, vehicleType);

            JOptionPane.showMessageDialog(this,
                    "Vehicle parked successfully in slot #" + assignedSlot.getSlotNumber() + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshData();
        }
    }

    /**
     * Exit a vehicle from a slot
     */
    private void exitVehicle() {
        if (activeSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active parking sessions!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show list of active sessions
        String[] sessionList = new String[activeSessions.size()];
        for (int i = 0; i < activeSessions.size(); i++) {
            VehicleSession session = activeSessions.get(i);
            sessionList[i] = session.getLicensePlate() + " - Slot " + session.getSlotId() +
                    " - " + session.getParkedHours() + " hours";
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select vehicle to exit:", "Exit Vehicle",
                JOptionPane.QUESTION_MESSAGE, null, sessionList, sessionList[0]);

        if (selected != null) {
            int index = java.util.Arrays.asList(sessionList).indexOf(selected);
            VehicleSession session = activeSessions.get(index);

            // Calculate fee
            double fee = session.calculateFee(5.0); // $5/hour rate

            // Find and free the slot
            ParkingLot currentLot = getCurrentLot();
            if (currentLot != null) {
                for (ParkingSlot slot : currentLot.getSlots()) {
                    if (slot.getSlotId().equals(session.getSlotId())) {
                        slot.setOccupied(false, null);
                        break;
                    }
                }
            }

            activeSessions.remove(index);

            JOptionPane.showMessageDialog(this,
                    String.format("Vehicle %s exited.\nParked for %d hours.\nTotal fee: $%.2f",
                            session.getLicensePlate(), session.getParkedHours(), fee),
                    "Payment", JOptionPane.INFORMATION_MESSAGE);

            refreshData();
        }
    }

    /**
     * Submit an availability report
     */
    private void submitReport() {
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JSpinner availableSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSlider confidenceSlider = new JSlider(0, 100, 80);
        confidenceSlider.setMajorTickSpacing(20);
        confidenceSlider.setPaintTicks(true);
        confidenceSlider.setPaintLabels(true);
        JTextArea notesArea = new JTextArea(2, 20);

        inputPanel.add(new JLabel("Available Spaces:"));
        inputPanel.add(availableSpinner);
        inputPanel.add(new JLabel("Confidence (%):"));
        inputPanel.add(confidenceSlider);
        inputPanel.add(new JLabel("Notes:"));
        inputPanel.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Submit Availability Report", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int available = (Integer) availableSpinner.getValue();
            int confidence = confidenceSlider.getValue();
            String notes = notesArea.getText().trim();

            UserReport report = new UserReport("REPORT-" + System.currentTimeMillis(),
                    currentUser.getId(), currentLot.getLotId(), available, confidence, notes);

            JOptionPane.showMessageDialog(this,
                    "Thank you for your report!\n" + report,
                    "Report Submitted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Get the currently selected parking lot
     */
    private ParkingLot getCurrentLot() {
        int index = lotSelector.getSelectedIndex();
        if (index >= 0 && index < parkingLots.size()) {
            return parkingLots.get(index);
        }
        return null;
    }

    /**
     * Refresh all data displays
     */
    private void refreshData() {
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;

        // Update stats
        totalSlotsLabel.setText("Total: " + currentLot.getTotalSpaces());
        availableSlotsLabel.setText("Available: " + currentLot.getAvailableSpaces());
        occupiedSlotsLabel.setText("Occupied: " +
                (currentLot.getTotalSpaces() - currentLot.getAvailableSpaces()));

        // Update slots table
        slotTableModel.setRowCount(0);
        for (ParkingSlot slot : currentLot.getSlots()) {
            Object[] row = {
                    slot.getSlotNumber(),
                    slot.getSlotType().getDisplayName(),
                    slot.getStatus(),
                    slot.isOccupied() ? slot.getVehicleType() : "-"
            };
            slotTableModel.addRow(row);
        }

        // Update sessions table
        sessionTableModel.setRowCount(0);
        for (VehicleSession session : activeSessions) {
            Object[] row = {
                    session.getId(),
                    session.getLicensePlate(),
                    session.getVehicleType().getDisplayName(),
                    session.getSlotId(),
                    session.getParkedHours() + " hours"
            };
            sessionTableModel.addRow(row);
        }
    }

    /**
     * Admin feature: Manage parking slots
     */
    private void manageSlots() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "This feature is only available to administrators!",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;

        String[] options = { "Add Slot", "Remove Slot", "View Slot Details", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                "Select an action:",
                "Manage Slots - " + currentLot.getName(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            // Add slot
            JPanel addPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            JSpinner slotNumberSpinner = new JSpinner(new SpinnerNumberModel(
                    currentLot.getTotalSpaces() + 1, 1, 1000, 1));
            JComboBox<SlotType> slotTypeCombo = new JComboBox<>(SlotType.values());

            addPanel.add(new JLabel("Slot Number:"));
            addPanel.add(slotNumberSpinner);
            addPanel.add(new JLabel("Slot Type:"));
            addPanel.add(slotTypeCombo);

            int result = JOptionPane.showConfirmDialog(this, addPanel,
                    "Add New Slot", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                int slotNumber = (Integer) slotNumberSpinner.getValue();
                SlotType slotType = (SlotType) slotTypeCombo.getSelectedItem();
                String slotId = String.format("SLOT-%03d", slotNumber);

                ParkingSlot newSlot = new ParkingSlot(slotId, currentLot.getLotId(),
                        slotNumber, slotType);
                currentLot.addSlot(newSlot);

                JOptionPane.showMessageDialog(this,
                        "Slot #" + slotNumber + " added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } else if (choice == 1) {
            // Remove slot
            if (currentLot.getSlots().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No slots to remove!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] slotList = new String[currentLot.getSlots().size()];
            for (int i = 0; i < currentLot.getSlots().size(); i++) {
                ParkingSlot slot = currentLot.getSlots().get(i);
                slotList[i] = "Slot #" + slot.getSlotNumber() + " - " +
                        slot.getSlotType().getDisplayName() +
                        (slot.isOccupied() ? " (OCCUPIED)" : "");
            }

            String selected = (String) JOptionPane.showInputDialog(this,
                    "Select slot to remove:",
                    "Remove Slot",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    slotList,
                    slotList[0]);

            if (selected != null) {
                int index = java.util.Arrays.asList(slotList).indexOf(selected);
                ParkingSlot slotToRemove = currentLot.getSlots().get(index);

                if (slotToRemove.isOccupied()) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot remove an occupied slot!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                currentLot.getSlots().remove(index);
                JOptionPane.showMessageDialog(this,
                        "Slot removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } else if (choice == 2) {
            // View slot details
            JOptionPane.showMessageDialog(this,
                    String.format("Lot: %s\nTotal Slots: %d\nAvailable: %d\nOccupied: %d",
                            currentLot.getName(),
                            currentLot.getTotalSpaces(),
                            currentLot.getAvailableSpaces(),
                            currentLot.getTotalSpaces() - currentLot.getAvailableSpaces()),
                    "Slot Statistics",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Admin feature: View all reports
     */
    private void viewAllReports() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "This feature is only available to administrators!",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder reportText = new StringBuilder();
        reportText.append("=== PARKING LOT REPORTS ===\n\n");

        for (ParkingLot lot : parkingLots) {
            reportText.append(String.format("Lot: %s (%s)\n", lot.getName(), lot.getLotId()));
            reportText.append(String.format("Location: %s\n", lot.getLocation()));
            reportText.append(String.format("Type: %s\n", lot.getLotType()));
            reportText.append(String.format("Total Spaces: %d\n", lot.getTotalSpaces()));
            reportText.append(String.format("Available: %d\n", lot.getAvailableSpaces()));
            reportText.append(String.format("Occupancy Rate: %.1f%%\n\n",
                    (1.0 - (double) lot.getAvailableSpaces() / lot.getTotalSpaces()) * 100));
        }

        reportText.append(String.format("\n=== ACTIVE SESSIONS ===\n"));
        reportText.append(String.format("Total Active: %d\n\n", activeSessions.size()));

        for (VehicleSession session : activeSessions) {
            reportText.append(String.format("Session #%d: %s (%s)\n",
                    session.getId(),
                    session.getLicensePlate(),
                    session.getVehicleType()));
            reportText.append(String.format("  Duration: %d hours\n",
                    session.getParkedHours()));
            reportText.append(String.format("  Estimated Fee: $%.2f\n\n",
                    session.calculateFee(5.0)));
        }

        JTextArea textArea = new JTextArea(reportText.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "System Reports",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Logout and return to login screen
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new LoginGUI().setVisible(true);
                    dispose();
                }
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ParkingLotManagerGUI().setVisible(true);
            }
        });
    }
}
