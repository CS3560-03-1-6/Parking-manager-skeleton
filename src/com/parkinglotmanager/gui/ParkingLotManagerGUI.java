package com.parkinglotmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.parkinglotmanager.dao.ParkingDAO;
import com.parkinglotmanager.dao.UserPreferenceDAO;
import com.parkinglotmanager.dao.VehicleDAO;
import com.parkinglotmanager.dao.VehicleSessionDAO;
import com.parkinglotmanager.enums.LotType;
import com.parkinglotmanager.enums.SlotType;
import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.ParkingSlot;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.model.UserPreference;
import com.parkinglotmanager.model.UserReport;
import com.parkinglotmanager.model.VehicleSession;

/**
 * Main GUI application for the Parking Lot Management System.
 * Provides a user interface for managing parking lots, slots, and sessions.
 */
public class ParkingLotManagerGUI extends JFrame {

    //colors and stuff from strawb's UI
    private static final Color HEADER_BLUE = new Color(167, 199, 231);
    private static final Color TEXT_COLOR = new Color(106, 123, 162);
    private static final Color FIELD_BORDER = new Color(180, 180, 180);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);


    private List<ParkingLot> parkingLots;
    private List<VehicleSession> activeSessions;
    private VehicleSessionDAO sessionDAO;
    private VehicleDAO vehicleDAO;
    private ParkingDAO parkingDAO;
    private UserPreferenceDAO userPrefDAO;
    private DefaultTableModel slotTableModel;
    private DefaultTableModel sessionTableModel;
    private JLabel totalSlotsLabel;
    private JLabel availableSlotsLabel;
    private JLabel occupiedSlotsLabel;
    private JComboBox<String> lotSelector;
    private User currentUser;
    private boolean isAdmin;
    private Timer autoRefreshTimer;

    public ParkingLotManagerGUI(User user) {
        this.currentUser = user;
        this.isAdmin = (user instanceof Admin);
        initializeData();
        setupUI();
        refreshData();
        startAutoRefresh();
    }

    public ParkingLotManagerGUI() {
        // Default constructor for backward compatibility
        // Create a demo client user, handling the checked Exception from Client
        // constructor
        try {
            this.currentUser = new Client("demo", "Demo", "User", "demo@parking.com", "demo_password");
        } catch (Exception e) {
            e.printStackTrace();
            // If demo user creation fails, leave currentUser as null
            this.currentUser = null;
        }
        this.isAdmin = false;
        initializeData();
        setupUI();
        refreshData();
        startAutoRefresh();
    }

    /**
     * Initialize sample data for demonstration
     */
    private void initializeData() {
        parkingLots = new ArrayList<>();
        sessionDAO = new VehicleSessionDAO();
        vehicleDAO = new VehicleDAO();
        parkingDAO = new ParkingDAO(sessionDAO, vehicleDAO);
        userPrefDAO = new UserPreferenceDAO();

        // Load active sessions from database
        activeSessions = sessionDAO.getActiveSessions();
        System.out.println("[INFO] Loaded " + activeSessions.size() + " active parking sessions from database");

        // Cal Poly Pomona Parking Lots with Actual Capacities
        ParkingLot structure1 = new ParkingLot("LOT-001", "Structure 1",
                "East of Voorhis Alumni Park and West of Police and Parking Services (Bldg. 109)", LotType.STRUCTURE);
        ParkingLot structure2 = new ParkingLot("LOT-002", "Structure 2", "Southeast of iPoly High School (Bldg. 128)",
                LotType.STRUCTURE);
        ParkingLot lotB = new ParkingLot("LOT-003", "Lot B", "East of Structure 2", LotType.SURFACE);
        ParkingLot lotE1 = new ParkingLot("LOT-004", "Lot E1", "Northeast of Lot E2", LotType.SURFACE);
        ParkingLot lotE2 = new ParkingLot("LOT-005", "Lot E2", "Northwest of Interim Design Center (Bldg. 89)",
                LotType.SURFACE);
        ParkingLot lotF1 = new ParkingLot("LOT-006", "Lot F1", "Southeast of Residence Hall, Aliso (Bldg. 23)",
                LotType.SURFACE);
        ParkingLot lotF10 = new ParkingLot("LOT-007", "Lot F10", "East of Police and Parking Services (Bldg. 109)",
                LotType.SURFACE);
        ParkingLot lotF3 = new ParkingLot("LOT-008", "Lot F3",
                "North of Lot F4 and South of Recreation/Maintenance (Bldg. 71)", LotType.SURFACE);
        ParkingLot lotF5 = new ParkingLot("LOT-009", "Lot F5", "Northeast of Structure 1 (Building 106)",
                LotType.SURFACE);
        ParkingLot lotF9 = new ParkingLot("LOT-010", "Lot F9", "South of Structure 1 (Bldg. 106)", LotType.SURFACE);
        ParkingLot lotJ = new ParkingLot("LOT-011", "Lot J", "Southwest of College of Environmental Design (Bldg. 7)",
                LotType.SURFACE);
        ParkingLot lotK = new ParkingLot("LOT-012", "Lot K", "Adjacent to Building 128: iPoly High School",
                LotType.SURFACE);
        ParkingLot lotM = new ParkingLot("LOT-013", "Lot M",
                "South of Parking Lot J and Southwest of College of Environmental Design (Bldg. 7)", LotType.SURFACE);
        ParkingLot lotN = new ParkingLot("LOT-014", "Lot N",
                "On Bronco Way Across from the Child Care Center (Building 116)", LotType.SURFACE);
        ParkingLot lotR = new ParkingLot("LOT-015", "Lot R", "Northwest of Kellogg House Pomona (Bldg. 112)",
                LotType.SURFACE);
        ParkingLot lotT = new ParkingLot("LOT-016", "Lot T", "Adjacent to Building 28: Fruit and Crops Unit",
                LotType.SURFACE);
        ParkingLot lotU = new ParkingLot("LOT-017", "Lot U", "West of University Village (Bldg. 200)", LotType.SURFACE);

        // Generate parking slots with realistic capacities (Cal Poly Pomona actual
        // numbers)
        generateSlots(structure1, 1250); // Structure 1: 1,250 spaces
        generateSlots(structure2, 850); // Structure 2: 850 spaces
        generateSlots(lotB, 450); // Lot B: 450 spaces
        generateSlots(lotE1, 320); // Lot E1: 320 spaces
        generateSlots(lotE2, 280); // Lot E2: 280 spaces
        generateSlots(lotF1, 240); // Lot F1: 240 spaces
        generateSlots(lotF10, 180); // Lot F10: 180 spaces
        generateSlots(lotF3, 210); // Lot F3: 210 spaces
        generateSlots(lotF5, 190); // Lot F5: 190 spaces
        generateSlots(lotF9, 165); // Lot F9: 165 spaces
        generateSlots(lotJ, 380); // Lot J: 380 spaces
        generateSlots(lotK, 420); // Lot K: 420 spaces
        generateSlots(lotM, 340); // Lot M: 340 spaces
        generateSlots(lotN, 290); // Lot N: 290 spaces
        generateSlots(lotR, 260); // Lot R: 260 spaces
        generateSlots(lotT, 175); // Lot T: 175 spaces
        generateSlots(lotU, 310); // Lot U: 310 spaces

        parkingLots.add(structure1);
        parkingLots.add(structure2);
        parkingLots.add(lotB);
        parkingLots.add(lotE1);
        parkingLots.add(lotE2);
        parkingLots.add(lotF1);
        parkingLots.add(lotF10);
        parkingLots.add(lotF3);
        parkingLots.add(lotF5);
        parkingLots.add(lotF9);
        parkingLots.add(lotJ);
        parkingLots.add(lotK);
        parkingLots.add(lotM);
        parkingLots.add(lotN);
        parkingLots.add(lotR);
        parkingLots.add(lotT);
        parkingLots.add(lotU);

        // Demo user creation is now handled in the constructor (or via LoginGUI).
        // We no longer create a Client here, which avoids throwing checked exceptions.
    }

    /**
     * Helper method to generate parking slots for a lot with realistic distribution
     * 
     * @param lot      The parking lot to add slots to
     * @param capacity Total number of parking slots to generate
     */
    private void generateSlots(ParkingLot lot, int capacity) {
        // Realistic slot type distribution:
        // 75% regular CAR slots
        // 10% COMPACT slots
        // 5% EV charging slots
        // 5% HANDICAPPED slots
        // 5% MOTORCYCLE slots

        int regularSlots = (int) (capacity * 0.75);
        int compactSlots = (int) (capacity * 0.10);
        int evSlots = (int) (capacity * 0.05);
        int handicappedSlots = (int) (capacity * 0.05);
        int motorcycleSlots = capacity - regularSlots - compactSlots - evSlots - handicappedSlots;

        int slotNumber = 1;
        String lotId = lot.getLotId();

        slotNumber = addSlots(lot, lotId, slotNumber, regularSlots,     SlotType.CAR);
        slotNumber = addSlots(lot, lotId, slotNumber, compactSlots,     SlotType.COMPACT);
        slotNumber = addSlots(lot, lotId, slotNumber, evSlots,          SlotType.EV);
        slotNumber = addSlots(lot, lotId, slotNumber, handicappedSlots, SlotType.HANDICAPPED);
        slotNumber = addSlots(lot, lotId, slotNumber, motorcycleSlots,  SlotType.MOTORCYCLE);
    }


    private int addSlots(ParkingLot lot, String lotId, int startNumber, int count, SlotType type) {
        int slotNumber = startNumber;

        for (int i = 0; i < count; i++) {
            String slotId = lotId + "-" + String.format("SLOT-%03d", slotNumber);
            lot.addSlot(new ParkingSlot(slotId, lotId, slotNumber, type));
            slotNumber++;
        }

        return slotNumber;
    }

    

    /**
     * Sets up the main user interface
     */
    private void setupUI() {
        setTitle("Parking Lot Manager System - Java + MySQL Edition");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Set Java icon for the application window
        try {
            java.awt.image.BufferedImage iconImage = javax.imageio.ImageIO
                    .read(new java.io.File("Resources/java-icon.png"));
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 130));

        // Title
        JLabel title = new JLabel("Parking Lot Manager System", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        headerPanel.add(title, BorderLayout.NORTH);

        // Lot selector and stats
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(HEADER_BLUE);
        JLabel selectLotLabel = new JLabel("Select Lot:");
        selectLotLabel.setFont(FONT_LABEL);
        selectLotLabel.setForeground(TEXT_COLOR);
        controlPanel.add(selectLotLabel);

        lotSelector = new JComboBox<>();
        lotSelector.setFont(FONT_FIELD);
        lotSelector.setBackground(Color.WHITE);
        lotSelector.setForeground(TEXT_COLOR);
        lotSelector.setPreferredSize(new Dimension(300, 30));
        for (ParkingLot lot : parkingLots) {
            lotSelector.addItem(lot.getName() + " (" + lot.getLotId() + ")");
        }
        lotSelector.addActionListener(e -> refreshData());
        controlPanel.add(lotSelector);

        // Stats labels
        totalSlotsLabel = new JLabel("Total: 0");
        availableSlotsLabel = new JLabel("Available: 0");
        occupiedSlotsLabel = new JLabel("Occupied: 0");

        totalSlotsLabel.setFont(FONT_LABEL);
        totalSlotsLabel.setForeground(TEXT_COLOR);
        availableSlotsLabel.setFont(FONT_LABEL);
        availableSlotsLabel.setForeground(new Color(0, 150, 0));
        occupiedSlotsLabel.setFont(FONT_LABEL);
        occupiedSlotsLabel.setForeground(new Color(200, 0, 0));

        controlPanel.add(Box.createHorizontalStrut(30));
        controlPanel.add(totalSlotsLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(availableSlotsLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(occupiedSlotsLabel);

        headerPanel.add(controlPanel, BorderLayout.CENTER);

        // User info and database status
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(HEADER_BLUE);

        JLabel userLabel;
        if (currentUser != null) {
            userLabel = new JLabel("Logged in as: " + currentUser.getUsername() +
                    " (" + (isAdmin ? "Administrator" : "Client") + ")");
        } else {
            userLabel = new JLabel("Logged in as: (no user)");
        }
        userLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setForeground(TEXT_COLOR);

        // Database connection status
        JLabel dbStatusLabel = new JLabel();
        try {
            com.parkinglotmanager.util.DatabaseConnection.testConnection();
            dbStatusLabel.setText("[OK] MySQL Connected");
            dbStatusLabel.setForeground(new Color(0, 150, 0));
        } catch (Exception e) {
            dbStatusLabel.setText("[X] MySQL Disconnected");
            dbStatusLabel.setForeground(Color.RED);
        }
        dbStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dbStatusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        infoPanel.add(dbStatusLabel, BorderLayout.WEST);
        infoPanel.add(userLabel, BorderLayout.EAST);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        headerPanel.add(infoPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    /**
     * Creates the main panel with slots and sessions tables
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBackground(Color.WHITE);
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
        panel.setBackground(Color.WHITE);

        //createTitledBorder can take (Border, String title, int just, int pos, Font, Color)
        //shoutout to whatever extension let me see options for params lol
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1),
            "Parking Slots",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, FONT_LABEL, TEXT_COLOR));

        // Table
        String[] columnNames = { "Slot #", "Type", "Status", "Vehicle" };
        slotTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(slotTableModel);
        table.setFont(FONT_FIELD);
        table.getTableHeader().setFont(FONT_LABEL);
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
        panel.setBackground(Color.WHITE);

        //second verse, same as the first
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1),
            "Active Parking Sessions",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, FONT_LABEL, TEXT_COLOR));

        // Table
        String[] columnNames = { "Session ID", "License Plate", "Vehicle Type", "Slot", "Duration" };
        sessionTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(sessionTableModel);
        table.setFont(FONT_FIELD);
        table.getTableHeader().setFont(FONT_LABEL);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Get error message for incompatible slot type
     */
    private String getCompatibilityErrorMessage(VehicleType vehicleType) {
        switch (vehicleType) {
            case MOTORCYCLE:
                return "No motorcycle slots available!\n\n" +
                        "Motorcycles can only park in designated MOTORCYCLE slots.\n" +
                        "Please select a different parking lot.";
            case EV:
                return "No EV charging slots available!\n\n" +
                        "Electric vehicles can park in:\n" +
                        "- EV Charging Slots (preferred)\n" +
                        "- Regular Car Slots\n" +
                        "- Compact Slots\n\n" +
                        "Please select a different parking lot.";
            case CAR:
                return "No car slots available!\n\n" +
                        "Cars can park in:\n" +
                        "- Regular Car Slots\n" +
                        "- Compact Slots\n" +
                        "- Handicapped Slots\n\n" +
                        "Note: Motorcycle and EV-only slots are reserved.";
            default:
                return "No compatible slots available for " + vehicleType + "!";
        }
    }

    /**
     * Get instructions for slot selection based on vehicle type
     */
    private String getSlotSelectionInstructions(VehicleType vehicleType, int availableCount) {
        String baseMsg = "Choose your preferred parking slot:\n\n";
        switch (vehicleType) {
            case MOTORCYCLE:
                return baseMsg + "Motorcycles MUST use designated motorcycle slots.\n" +
                        availableCount + " motorcycle slot(s) available.";
            case EV:
                return baseMsg + "Electric vehicles can use EV charging slots or regular car slots.\n" +
                        "EV charging slots are recommended for charging.\n" +
                        availableCount + " compatible slot(s) available.";
            case CAR:
                return baseMsg + "Cars can use regular, compact, or handicapped slots.\n" +
                        "Motorcycle and EV-only slots are NOT available.\n" +
                        availableCount + " compatible slot(s) available.";
            default:
                return baseMsg + availableCount + " slot(s) available.";
        }
    }

    private JButton newUIButton(String text, int colorOption) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);

        Color buttonColor;
        Color hoverColor;
        Color textColor;
        
        switch (colorOption) {
            case 1: // Green
                buttonColor = new Color(39, 174, 96);
                hoverColor = new Color(46, 204, 113);
                textColor = Color.WHITE;
                break;
            case 2: // Red
                buttonColor = new Color(192, 57, 43);
                hoverColor = new Color(231, 76, 60);
                textColor = Color.WHITE;
                break;
            case 3: // Orange
                buttonColor = new Color(230, 126, 34);
                hoverColor = new Color(243, 156, 18);
                textColor = Color.WHITE;
                break;
            case 4: // Purple
                buttonColor = new Color(155, 89, 182);
                hoverColor = new Color(142, 68, 173);
                textColor = Color.WHITE;
                break;
            case 0: // Default Blue
            default:
                buttonColor = Color.WHITE;
                hoverColor = new Color(225, 235, 245);
                textColor = TEXT_COLOR;
                break;
        }
        
        btn.setForeground(textColor);
        btn.setBackground(buttonColor);
        btn.setOpaque(true);

        if (colorOption == 0) { //only default buttons have border
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HEADER_BLUE, 2),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
            ));
        } else {
            btn.setBorderPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        }


        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(buttonColor);
            }
        });

        return btn;
    }

    /**
     * Creates the actions panel with buttons
     */
    private JPanel createActionsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton parkButton = newUIButton("Park Vehicle", 0);
        parkButton.addActionListener(e -> parkVehicle());

        JButton exitButton = newUIButton("Exit Vehicle", 0);
        exitButton.addActionListener(e -> exitVehicle());

        JButton reportButton = newUIButton("Submit Availability Report", 0);
        reportButton.addActionListener(e -> submitReport());

        JButton refreshButton = newUIButton("Refresh", 0);
        refreshButton.addActionListener(e -> refreshData());

        JButton preferencesButton = newUIButton("Preferences", 0);
        preferencesButton.addActionListener(e -> openUserPreferences());

        // Admin-only buttons
        JButton manageSlotsButton = newUIButton("Manage Slots", 3);
        manageSlotsButton.addActionListener(e -> manageSlots());

        JButton viewReportsButton = newUIButton("View Reports", 4);
        viewReportsButton.addActionListener(e -> viewAllReports());

        JButton logoutButton = newUIButton("Logout", 2);
        logoutButton.addActionListener(e -> logout());


        panel.add(parkButton);
        panel.add(exitButton);
        panel.add(reportButton);

        // Only show admin buttons for admin users
        if (isAdmin) {
            JButton visualizeButton = newUIButton("Visual Simulation", 1);
            visualizeButton.addActionListener(e -> openVisualization());

            JButton exitAllButton = newUIButton("Exit All Vehicles", 2);
            exitAllButton.addActionListener(e -> exitAllVehicles());

            panel.add(visualizeButton);
            panel.add(exitAllButton);
            panel.add(manageSlotsButton);
            panel.add(viewReportsButton);

        }
        panel.add(refreshButton);
        panel.add(preferencesButton);
        panel.add(logoutButton);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(getWidth(), 80));

        outerPanel.add(scrollPane, BorderLayout.CENTER);
        return outerPanel;
    }

    /**
     * Open the real-time visual simulation window
     */
    private void openVisualization() {
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking lot first!",
                    "No Lot Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ParkingVisualization visualization = new ParkingVisualization(currentLot, isAdmin, parkingLots);
        visualization.setVisible(true);
    }

    /**
     * Park a vehicle in an available slot
     */
    private void parkVehicle() {
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;

        // For clients, check if they already have an active parking session
        if (!isAdmin) {
            int userSessionCount = 0;
            for (VehicleSession session : activeSessions) {
                if (session.getUserId() == currentUser.getId()) {
                    userSessionCount++;
                }
            }
            if (userSessionCount >= 1) {
                JOptionPane.showMessageDialog(this,
                        "You already have an active parking session!\n" +
                                "Please exit your current vehicle before parking another.",
                        "Parking Limit Reached",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Find available slots
        List<ParkingSlot> availableSlots = parkingDAO.getAvailableSlots(currentLot);

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

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "License plate cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

            VehicleType vehicleType = (VehicleType) typeCombo.getSelectedItem();
            VehicleMake vehicleMake = (VehicleMake) makeCombo.getSelectedItem();

            // Filter compatible slots for the vehicle type
            List<ParkingSlot> compatibleSlots = parkingDAO.getCompatibleAvailableSlots(currentLot, vehicleType);

            if (compatibleSlots.isEmpty()) {
                String message = getCompatibilityErrorMessage(vehicleType);
                JOptionPane.showMessageDialog(this,
                        message,
                        "No Compatible Slots", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Let user choose from available compatible slots
            String[] slotOptions = new String[compatibleSlots.size()];
            for (int i = 0; i < compatibleSlots.size(); i++) {
                ParkingSlot slot = compatibleSlots.get(i);
                slotOptions[i] = String.format("Slot #%d (%s) - %s",
                        slot.getSlotNumber(),
                        slot.getSlotId(),
                        slot.getSlotType().getDisplayName());
            }

            String instructions = getSlotSelectionInstructions(vehicleType, compatibleSlots.size());
            String selectedSlot = (String) JOptionPane.showInputDialog(this,
                    instructions,
                    "Select Parking Slot - " + compatibleSlots.size() + " Available",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    slotOptions,
                    slotOptions[0]);

            if (selectedSlot == null) {
                // User cancelled slot selection
                return;
            }

            // Find the selected slot
            int selectedIndex = java.util.Arrays.asList(slotOptions).indexOf(selectedSlot);
            ParkingSlot assignedSlot = compatibleSlots.get(selectedIndex);

            int userId = currentUser != null ? currentUser.getId() : -1;
            VehicleSession session = parkingDAO.parkVehicle(
                    currentLot,
                    assignedSlot,
                    plate,
                    vehicleType,
                    vehicleMake,
                    userId
            );

            if (session == null) {
                JOptionPane.showMessageDialog(this,
                        "Failed to save parking session to database!",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add to in-memory list of active sessions
            activeSessions.add(session);

            JOptionPane.showMessageDialog(this,
                    "Vehicle parked successfully in slot #" + assignedSlot.getSlotNumber() + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshData();
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

        // Filter sessions based on user role
        List<VehicleSession> availableSessions = new ArrayList<>();
        if (isAdmin) {
            // Admins can exit any vehicle
            availableSessions.addAll(activeSessions);
        } else {
            // Clients can only exit their own vehicles
            for (VehicleSession session : activeSessions) {
                if (session.getUserId() == currentUser.getId()) {
                    availableSessions.add(session);
                }
            }
        }

        if (availableSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You have no active parking sessions to exit!",
                    "No Sessions Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show list of available sessions
        String[] sessionList = new String[availableSessions.size()];
        for (int i = 0; i < availableSessions.size(); i++) {
            VehicleSession session = availableSessions.get(i);
            String userInfo = isAdmin ? " (User ID: " + session.getUserId() + ")" : "";
            sessionList[i] = session.getLicensePlate() + " - Slot " + session.getSlotId() +
                    " - " + session.getParkedHours() + " hours" + userInfo;
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select vehicle to exit:", "Exit Vehicle",
                JOptionPane.QUESTION_MESSAGE, null, sessionList, sessionList[0]);

        if (selected != null) {
            int index = java.util.Arrays.asList(sessionList).indexOf(selected);
            VehicleSession session = availableSessions.get(index);

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

            // Complete session in database
            if (session.getId() > 0) {
                boolean completed = sessionDAO.completeSession(session.getId(), fee);
                if (completed) {
                    System.out.println("[OK] Completed parking session #" + session.getId() + " in database");
                } else {
                    System.err.println("[FAIL] Failed to complete session in database");
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
     * Admin feature: Exit all vehicles from all parking lots
     */
    private void exitAllVehicles() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "This feature is only available to administrators!",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (activeSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No active parking sessions to exit!",
                    "No Sessions",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit ALL " + activeSessions.size() + " active parking sessions?\n" +
                        "This action cannot be undone!",
                "Confirm Exit All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int exitedCount = 0;
            double totalFees = 0.0;

            // Create a copy to avoid concurrent modification
            List<VehicleSession> sessionsToExit = new ArrayList<>(activeSessions);

            for (VehicleSession session : sessionsToExit) {
                // Calculate fee
                double fee = session.calculateFee(5.0); // $5/hour rate
                totalFees += fee;

                // Find and free the slot
                for (ParkingLot lot : parkingLots) {
                    for (ParkingSlot slot : lot.getSlots()) {
                        if (slot.getSlotId().equals(session.getSlotId())) {
                            slot.setOccupied(false, null);
                            break;
                        }
                    }
                }

                // Complete session in database
                if (session.getId() > 0) {
                    boolean completed = sessionDAO.completeSession(session.getId(), fee);
                    if (completed) {
                        exitedCount++;
                        System.out.println("[OK] Completed parking session #" + session.getId() + " - Fee: $"
                                + String.format("%.2f", fee));
                    } else {
                        System.err.println("[FAIL] Failed to complete session #" + session.getId());
                    }
                }
            }

            activeSessions.clear();

            JOptionPane.showMessageDialog(this,
                    String.format("Successfully exited %d vehicles!\nTotal fees collected: $%.2f",
                            exitedCount, totalFees),
                    "Exit All Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshData();
        }
    }
    /**
     * open user preferences dialog
     */
    private void openUserPreferences() {

        UserPreference existing = userPrefDAO.getPreferenceByUserId(currentUser.getId());
        UserPreferenceDialog dialog = new UserPreferenceDialog(this, parkingLots, existing);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        UserPreference updated = dialog.getUserPreference();
        if (updated == null) {
            return;
        }

        updated.setUserID(currentUser.getId());

        boolean checkUpdate = userPrefDAO.saveOrUpdatePreference(updated);
        if (checkUpdate) {
            JOptionPane.showMessageDialog(this, "Preferences saved.", "Preference:", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save preferences.", "Error:", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Submit an availability report
     */
    private void submitReport() {
        refreshData();
        ParkingLot currentLot = getCurrentLot();
        if (currentLot == null)
            return;
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JSpinner availableSpinner = new JSpinner(new SpinnerNumberModel(currentLot.getAvailableSpaces(), 0, currentLot.getTotalSpaces(), 1));
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
                    currentUser != null ? currentUser.getId() : -1,
                    currentLot.getLotId(), available, confidence, notes);

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

        // Reload active sessions from database
        activeSessions = sessionDAO.getActiveSessions();
        System.out.println("[REFRESH] Reloaded " + activeSessions.size() + " active parking sessions from database");

        // Update slot occupancy based on active sessions
        for (ParkingSlot slot : currentLot.getSlots()) {
            slot.setOccupied(false, null);
        }

        for (VehicleSession session : activeSessions) {
            for (ParkingSlot slot : currentLot.getSlots()) {
                if (slot.getSlotId().equals(session.getSlotId())) {
                    slot.setOccupied(true, session.getVehicleType());
                    break;
                }
            }
        }

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
     * Start auto-refresh timer to update data every 3 seconds
     */
    private void startAutoRefresh() {
        // Auto-refresh every 3 seconds
        autoRefreshTimer = new Timer(3000, e -> refreshData());
        autoRefreshTimer.start();
        System.out.println("[AUTO-REFRESH] Started - updating every 3 seconds");
    }

    /**
     * Stop auto-refresh timer
     */
    private void stopAutoRefresh() {
        if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
            autoRefreshTimer.stop();
            System.out.println("[AUTO-REFRESH] Stopped");
        }
    }

    @Override
    public void dispose() {
        stopAutoRefresh();
        super.dispose();
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

        // Get today's completed sessions for revenue calculation
        double totalDailyRevenue = sessionDAO.getTodayRevenue();
        int completedToday = sessionDAO.getTodayCompletedCount();

        StringBuilder reportText = new StringBuilder();
        reportText.append("=== DAILY REVENUE REPORT ===\n");
        reportText.append(String.format("Date: %s\n", java.time.LocalDate.now()));
        reportText.append(String.format("Total Revenue Today: $%.2f\n", totalDailyRevenue));
        reportText.append(String.format("Completed Sessions Today: %d\n", completedToday));
        reportText.append(
                String.format("Average Fee: $%.2f\n\n", completedToday > 0 ? totalDailyRevenue / completedToday : 0.0));

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