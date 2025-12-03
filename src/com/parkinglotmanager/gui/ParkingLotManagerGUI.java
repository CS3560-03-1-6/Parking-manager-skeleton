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
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.parkinglotmanager.dao.UserPreferenceDAO;
import com.parkinglotmanager.enums.LotType;
import com.parkinglotmanager.enums.SlotType;
import com.parkinglotmanager.enums.VehicleMake;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.ParkingSlot;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.model.UserReport;
import com.parkinglotmanager.model.VehicleSession;
import com.parkinglotmanager.model.UserPreference;

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
        // Create a demo client user, handling the checked Exception from Client constructor
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
    }

    /**
     * Initialize sample data for demonstration
     */
    private void initializeData() {
        parkingLots = new ArrayList<>();
        activeSessions = new ArrayList<>();

        // Create a sample parking lot
        ParkingLot structure1 = new ParkingLot("LOT-001", "Structure 1", "East of Voorhis Alumni Park and West of Police and Parking Services (Bldg. 109)", LotType.STRUCTURE);
        ParkingLot structure2 = new ParkingLot("LOT-002", "Structure 2", "SourthEast of iPoly High School (Bldg. 128)", LotType.STRUCTURE);
        ParkingLot lotB = new ParkingLot("LOT-003", "Lot B", "East of Structure 2", LotType.STRUCTURE);
        ParkingLot lotE1 = new ParkingLot("LOT-004", "Lot E1", "NortheEast of Lot E2", LotType.STRUCTURE);
        ParkingLot lotE2 = new ParkingLot("LOT-005", "Lot E2", "NorthWest of Interim Design Center (Bldg. 89)", LotType.STRUCTURE);
        ParkingLot lotF1 = new ParkingLot("LOT-006", "Lot F1", "SouthEast of Residence Hall, Aliso (Bldg. 23)", LotType.STRUCTURE);
        ParkingLot lotF10 = new ParkingLot("LOT-007", "Lot F10", "East f Police and Parking Services (Bldg. 109)", LotType.STRUCTURE);
        ParkingLot lotF3 = new ParkingLot("LOT-008", "Lot F3", "North of Lot F4 and South of Recreation/Maintenance (Bldg. 71)", LotType.STRUCTURE);
        ParkingLot lotF5 = new ParkingLot("LOT-009", "Lot F5", "NorthEast of Structure 1 (Building 106)", LotType.STRUCTURE);
        ParkingLot lotF9 = new ParkingLot("LOT-010", "Lot F9", "South of Structure 1 (Bldg. 106)", LotType.STRUCTURE);
        ParkingLot lotJ = new ParkingLot("LOT-011", "Lot J", "SouthWest of College of of Env (Bldg. 7)", LotType.STRUCTURE);
        ParkingLot lotK = new ParkingLot("LOT-012", "Lot K", "Adjacent to Building 128: iPoly High School", LotType.STRUCTURE);
        ParkingLot lotM = new ParkingLot("LOT-013", "Lot M", "South of Parking Lot J and South West of College of Environmental Design (Bldg. 7)", LotType.STRUCTURE);
        ParkingLot lotN = new ParkingLot("LOT-014", "Lot N", "On Bronco Way Across from the Child Care Center (Building 116)", LotType.STRUCTURE);
        ParkingLot lotR = new ParkingLot("LOT-015", "Lot R", "NorthWest of Kellog House Pomona (Bldg. 112)", LotType.STRUCTURE);
        ParkingLot lotT = new ParkingLot("LOT-016", "Lot T", "Adjacent to Building 28: Fruit and Crops Unit", LotType.STRUCTURE);
        ParkingLot lotU = new ParkingLot("LOT-017", "Lot U", "West of University Village (Bldg. 200)", LotType.STRUCTURE);
        

        // Add some parking slots
        structure1.addSlot(new ParkingSlot("SLOT-001", "LOT-001", 1, SlotType.CAR));
        structure1.addSlot(new ParkingSlot("SLOT-002", "LOT-001", 2, SlotType.CAR));
        structure1.addSlot(new ParkingSlot("SLOT-003", "LOT-001", 3, SlotType.MOTORCYCLE));
        structure1.addSlot(new ParkingSlot("SLOT-004", "LOT-001", 4, SlotType.EV));
        structure1.addSlot(new ParkingSlot("SLOT-005", "LOT-001", 5, SlotType.HANDICAPPED));
        structure1.addSlot(new ParkingSlot("SLOT-006", "LOT-001", 6, SlotType.CAR));
        structure1.addSlot(new ParkingSlot("SLOT-007", "LOT-001", 7, SlotType.CAR));
        structure1.addSlot(new ParkingSlot("SLOT-008", "LOT-001", 8, SlotType.COMPACT));
        structure1.addSlot(new ParkingSlot("SLOT-009", "LOT-001", 9, SlotType.EV));
        structure1.addSlot(new ParkingSlot("SLOT-010", "LOT-001", 10, SlotType.MOTORCYCLE));


        structure2.addSlot(new ParkingSlot("SLOT-001", "LOT-002", 1, SlotType.CAR));
        structure2.addSlot(new ParkingSlot("SLOT-002", "LOT-002", 2, SlotType.CAR));
        structure2.addSlot(new ParkingSlot("SLOT-003", "LOT-002", 3, SlotType.MOTORCYCLE));
        structure2.addSlot(new ParkingSlot("SLOT-004", "LOT-002", 4, SlotType.EV));
        structure2.addSlot(new ParkingSlot("SLOT-005", "LOT-002", 5, SlotType.HANDICAPPED));
        structure2.addSlot(new ParkingSlot("SLOT-006", "LOT-002", 6, SlotType.CAR));
        structure2.addSlot(new ParkingSlot("SLOT-007", "LOT-002", 7, SlotType.CAR));
        structure2.addSlot(new ParkingSlot("SLOT-008", "LOT-002", 8, SlotType.COMPACT));
        structure2.addSlot(new ParkingSlot("SLOT-009", "LOT-002", 9, SlotType.EV));
        structure2.addSlot(new ParkingSlot("SLOT-010", "LOT-002", 10, SlotType.MOTORCYCLE));


        lotB.addSlot(new ParkingSlot("SLOT-001", "LOT-003", 1, SlotType.CAR));
        lotB.addSlot(new ParkingSlot("SLOT-002", "LOT-003", 2, SlotType.CAR));
        lotB.addSlot(new ParkingSlot("SLOT-003", "LOT-003", 3, SlotType.MOTORCYCLE));
        lotB.addSlot(new ParkingSlot("SLOT-004", "LOT-003", 4, SlotType.EV));
        lotB.addSlot(new ParkingSlot("SLOT-005", "LOT-003", 5, SlotType.HANDICAPPED));
        lotB.addSlot(new ParkingSlot("SLOT-006", "LOT-003", 6, SlotType.CAR));
        lotB.addSlot(new ParkingSlot("SLOT-007", "LOT-003", 7, SlotType.CAR));
        lotB.addSlot(new ParkingSlot("SLOT-008", "LOT-003", 8, SlotType.COMPACT));
        lotB.addSlot(new ParkingSlot("SLOT-009", "LOT-003", 9, SlotType.EV));
        lotB.addSlot(new ParkingSlot("SLOT-010", "LOT-003", 10, SlotType.MOTORCYCLE));


        lotE1.addSlot(new ParkingSlot("SLOT-001", "LOT-004", 1, SlotType.CAR));
        lotE1.addSlot(new ParkingSlot("SLOT-002", "LOT-004", 2, SlotType.CAR));
        lotE1.addSlot(new ParkingSlot("SLOT-003", "LOT-004", 3, SlotType.MOTORCYCLE));
        lotE1.addSlot(new ParkingSlot("SLOT-004", "LOT-004", 4, SlotType.EV));
        lotE1.addSlot(new ParkingSlot("SLOT-005", "LOT-004", 5, SlotType.HANDICAPPED));
        lotE1.addSlot(new ParkingSlot("SLOT-006", "LOT-004", 6, SlotType.CAR));
        lotE1.addSlot(new ParkingSlot("SLOT-007", "LOT-004", 7, SlotType.CAR));
        lotE1.addSlot(new ParkingSlot("SLOT-008", "LOT-004", 8, SlotType.COMPACT));
        lotE1.addSlot(new ParkingSlot("SLOT-009", "LOT-004", 9, SlotType.EV));
        lotE1.addSlot(new ParkingSlot("SLOT-010", "LOT-004", 10, SlotType.MOTORCYCLE));

        lotE2.addSlot(new ParkingSlot("SLOT-001", "LOT-005", 1, SlotType.CAR));
        lotE2.addSlot(new ParkingSlot("SLOT-002", "LOT-005", 2, SlotType.CAR));
        lotE2.addSlot(new ParkingSlot("SLOT-003", "LOT-005", 3, SlotType.MOTORCYCLE));
        lotE2.addSlot(new ParkingSlot("SLOT-004", "LOT-005", 4, SlotType.EV));
        lotE2.addSlot(new ParkingSlot("SLOT-005", "LOT-005", 5, SlotType.HANDICAPPED));
        lotE2.addSlot(new ParkingSlot("SLOT-006", "LOT-005", 6, SlotType.CAR));
        lotE2.addSlot(new ParkingSlot("SLOT-007", "LOT-005", 7, SlotType.CAR));
        lotE2.addSlot(new ParkingSlot("SLOT-008", "LOT-005", 8, SlotType.COMPACT));
        lotE2.addSlot(new ParkingSlot("SLOT-009", "LOT-005", 9, SlotType.EV));
        lotE2.addSlot(new ParkingSlot("SLOT-010", "LOT-005", 10, SlotType.MOTORCYCLE));

        lotF1.addSlot(new ParkingSlot("SLOT-001", "LOT-006", 1, SlotType.CAR));
        lotF1.addSlot(new ParkingSlot("SLOT-002", "LOT-006", 2, SlotType.CAR));
        lotF1.addSlot(new ParkingSlot("SLOT-003", "LOT-006", 3, SlotType.MOTORCYCLE));
        lotF1.addSlot(new ParkingSlot("SLOT-004", "LOT-006", 4, SlotType.EV));
        lotF1.addSlot(new ParkingSlot("SLOT-005", "LOT-006", 5, SlotType.HANDICAPPED));
        lotF1.addSlot(new ParkingSlot("SLOT-006", "LOT-006", 6, SlotType.CAR));
        lotF1.addSlot(new ParkingSlot("SLOT-007", "LOT-006", 7, SlotType.CAR));
        lotF1.addSlot(new ParkingSlot("SLOT-008", "LOT-006", 8, SlotType.COMPACT));
        lotF1.addSlot(new ParkingSlot("SLOT-009", "LOT-006", 9, SlotType.EV));
        lotF1.addSlot(new ParkingSlot("SLOT-010", "LOT-006", 10, SlotType.MOTORCYCLE));


        lotF10.addSlot(new ParkingSlot("SLOT-001", "LOT-007", 1, SlotType.CAR));
        lotF10.addSlot(new ParkingSlot("SLOT-002", "LOT-007", 2, SlotType.CAR));
        lotF10.addSlot(new ParkingSlot("SLOT-003", "LOT-007", 3, SlotType.MOTORCYCLE));
        lotF10.addSlot(new ParkingSlot("SLOT-004", "LOT-007", 4, SlotType.EV));
        lotF10.addSlot(new ParkingSlot("SLOT-005", "LOT-007", 5, SlotType.HANDICAPPED));
        lotF10.addSlot(new ParkingSlot("SLOT-006", "LOT-007", 6, SlotType.CAR));
        lotF10.addSlot(new ParkingSlot("SLOT-007", "LOT-007", 7, SlotType.CAR));
        lotF10.addSlot(new ParkingSlot("SLOT-008", "LOT-007", 8, SlotType.COMPACT));
        lotF10.addSlot(new ParkingSlot("SLOT-009", "LOT-007", 9, SlotType.EV));
        lotF10.addSlot(new ParkingSlot("SLOT-010", "LOT-007", 10, SlotType.MOTORCYCLE));


        lotF3.addSlot(new ParkingSlot("SLOT-001", "LOT-008", 1, SlotType.CAR));
        lotF3.addSlot(new ParkingSlot("SLOT-002", "LOT-008", 2, SlotType.CAR));
        lotF3.addSlot(new ParkingSlot("SLOT-003", "LOT-008", 3, SlotType.MOTORCYCLE));
        lotF3.addSlot(new ParkingSlot("SLOT-004", "LOT-008", 4, SlotType.EV));
        lotF3.addSlot(new ParkingSlot("SLOT-005", "LOT-008", 5, SlotType.HANDICAPPED));
        lotF3.addSlot(new ParkingSlot("SLOT-006", "LOT-008", 6, SlotType.CAR));
        lotF3.addSlot(new ParkingSlot("SLOT-007", "LOT-008", 7, SlotType.CAR));
        lotF3.addSlot(new ParkingSlot("SLOT-008", "LOT-008", 8, SlotType.COMPACT));
        lotF3.addSlot(new ParkingSlot("SLOT-009", "LOT-008", 9, SlotType.EV));
        lotF3.addSlot(new ParkingSlot("SLOT-010", "LOT-008", 10, SlotType.MOTORCYCLE));
        

        lotF5.addSlot(new ParkingSlot("SLOT-001", "LOT-009", 1, SlotType.CAR));
        lotF5.addSlot(new ParkingSlot("SLOT-002", "LOT-009", 2, SlotType.CAR));
        lotF5.addSlot(new ParkingSlot("SLOT-003", "LOT-009", 3, SlotType.MOTORCYCLE));
        lotF5.addSlot(new ParkingSlot("SLOT-004", "LOT-009", 4, SlotType.EV));
        lotF5.addSlot(new ParkingSlot("SLOT-005", "LOT-009", 5, SlotType.HANDICAPPED));
        lotF5.addSlot(new ParkingSlot("SLOT-006", "LOT-009", 6, SlotType.CAR));
        lotF5.addSlot(new ParkingSlot("SLOT-007", "LOT-009", 7, SlotType.CAR));
        lotF5.addSlot(new ParkingSlot("SLOT-008", "LOT-009", 8, SlotType.COMPACT));
        lotF5.addSlot(new ParkingSlot("SLOT-009", "LOT-009", 9, SlotType.EV));
        lotF5.addSlot(new ParkingSlot("SLOT-010", "LOT-009", 10, SlotType.MOTORCYCLE));


        lotF9.addSlot(new ParkingSlot("SLOT-001", "LOT-010", 1, SlotType.CAR));
        lotF9.addSlot(new ParkingSlot("SLOT-002", "LOT-010", 2, SlotType.CAR));
        lotF9.addSlot(new ParkingSlot("SLOT-003", "LOT-010", 3, SlotType.MOTORCYCLE));
        lotF9.addSlot(new ParkingSlot("SLOT-004", "LOT-010", 4, SlotType.EV));
        lotF9.addSlot(new ParkingSlot("SLOT-005", "LOT-010", 5, SlotType.HANDICAPPED));
        lotF9.addSlot(new ParkingSlot("SLOT-006", "LOT-010", 6, SlotType.CAR));
        lotF9.addSlot(new ParkingSlot("SLOT-007", "LOT-010", 7, SlotType.CAR));
        lotF9.addSlot(new ParkingSlot("SLOT-008", "LOT-010", 8, SlotType.COMPACT));
        lotF9.addSlot(new ParkingSlot("SLOT-009", "LOT-010", 9, SlotType.EV));
        lotF9.addSlot(new ParkingSlot("SLOT-010", "LOT-010", 10, SlotType.MOTORCYCLE));


        lotJ.addSlot(new ParkingSlot("SLOT-001", "LOT-011", 1, SlotType.CAR));
        lotJ.addSlot(new ParkingSlot("SLOT-002", "LOT-011", 2, SlotType.CAR));
        lotJ.addSlot(new ParkingSlot("SLOT-003", "LOT-011", 3, SlotType.MOTORCYCLE));
        lotJ.addSlot(new ParkingSlot("SLOT-004", "LOT-011", 4, SlotType.EV));
        lotJ.addSlot(new ParkingSlot("SLOT-005", "LOT-011", 5, SlotType.HANDICAPPED));
        lotJ.addSlot(new ParkingSlot("SLOT-006", "LOT-011", 6, SlotType.CAR));
        lotJ.addSlot(new ParkingSlot("SLOT-007", "LOT-011", 7, SlotType.CAR));
        lotJ.addSlot(new ParkingSlot("SLOT-008", "LOT-011", 8, SlotType.COMPACT));
        lotJ.addSlot(new ParkingSlot("SLOT-009", "LOT-011", 9, SlotType.EV));
        lotJ.addSlot(new ParkingSlot("SLOT-010", "LOT-011", 10, SlotType.MOTORCYCLE));


        lotK.addSlot(new ParkingSlot("SLOT-001", "LOT-012", 1, SlotType.CAR));
        lotK.addSlot(new ParkingSlot("SLOT-002", "LOT-012", 2, SlotType.CAR));
        lotK.addSlot(new ParkingSlot("SLOT-003", "LOT-012", 3, SlotType.MOTORCYCLE));
        lotK.addSlot(new ParkingSlot("SLOT-004", "LOT-012", 4, SlotType.EV));
        lotK.addSlot(new ParkingSlot("SLOT-005", "LOT-012", 5, SlotType.HANDICAPPED));
        lotK.addSlot(new ParkingSlot("SLOT-006", "LOT-012", 6, SlotType.CAR));
        lotK.addSlot(new ParkingSlot("SLOT-007", "LOT-012", 7, SlotType.CAR));
        lotK.addSlot(new ParkingSlot("SLOT-008", "LOT-012", 8, SlotType.COMPACT));
        lotK.addSlot(new ParkingSlot("SLOT-009", "LOT-012", 9, SlotType.EV));
        lotK.addSlot(new ParkingSlot("SLOT-010", "LOT-012", 10, SlotType.MOTORCYCLE));


        lotM.addSlot(new ParkingSlot("SLOT-001", "LOT-013", 1, SlotType.CAR));
        lotM.addSlot(new ParkingSlot("SLOT-002", "LOT-013", 2, SlotType.CAR));
        lotM.addSlot(new ParkingSlot("SLOT-003", "LOT-013", 3, SlotType.MOTORCYCLE));
        lotM.addSlot(new ParkingSlot("SLOT-004", "LOT-013", 4, SlotType.EV));
        lotM.addSlot(new ParkingSlot("SLOT-005", "LOT-013", 5, SlotType.HANDICAPPED));
        lotM.addSlot(new ParkingSlot("SLOT-006", "LOT-013", 6, SlotType.CAR));
        lotM.addSlot(new ParkingSlot("SLOT-007", "LOT-013", 7, SlotType.CAR));
        lotM.addSlot(new ParkingSlot("SLOT-008", "LOT-013", 8, SlotType.COMPACT));
        lotM.addSlot(new ParkingSlot("SLOT-009", "LOT-013", 9, SlotType.EV));
        lotM.addSlot(new ParkingSlot("SLOT-010", "LOT-013", 10, SlotType.MOTORCYCLE));


        lotN.addSlot(new ParkingSlot("SLOT-001", "LOT-014", 1, SlotType.CAR));
        lotN.addSlot(new ParkingSlot("SLOT-002", "LOT-014", 2, SlotType.CAR));
        lotN.addSlot(new ParkingSlot("SLOT-003", "LOT-014", 3, SlotType.MOTORCYCLE));
        lotN.addSlot(new ParkingSlot("SLOT-004", "LOT-014", 4, SlotType.EV));
        lotN.addSlot(new ParkingSlot("SLOT-005", "LOT-014", 5, SlotType.HANDICAPPED));
        lotN.addSlot(new ParkingSlot("SLOT-006", "LOT-014", 6, SlotType.CAR));
        lotN.addSlot(new ParkingSlot("SLOT-007", "LOT-014", 7, SlotType.CAR));
        lotN.addSlot(new ParkingSlot("SLOT-008", "LOT-014", 8, SlotType.COMPACT));
        lotN.addSlot(new ParkingSlot("SLOT-009", "LOT-014", 9, SlotType.EV));
        lotN.addSlot(new ParkingSlot("SLOT-010", "LOT-014", 10, SlotType.MOTORCYCLE));


        lotR.addSlot(new ParkingSlot("SLOT-001", "LOT-015", 1, SlotType.CAR));
        lotR.addSlot(new ParkingSlot("SLOT-002", "LOT-015", 2, SlotType.CAR));
        lotR.addSlot(new ParkingSlot("SLOT-003", "LOT-015", 3, SlotType.MOTORCYCLE));
        lotR.addSlot(new ParkingSlot("SLOT-004", "LOT-015", 4, SlotType.EV));
        lotR.addSlot(new ParkingSlot("SLOT-005", "LOT-015", 5, SlotType.HANDICAPPED));
        lotR.addSlot(new ParkingSlot("SLOT-06", "LOT-015", 6, SlotType.CAR));
        lotR.addSlot(new ParkingSlot("SLOT-007", "LOT-015", 7, SlotType.CAR));
        lotR.addSlot(new ParkingSlot("SLOT-008", "LOT-015", 8, SlotType.COMPACT));
        lotR.addSlot(new ParkingSlot("SLOT-009", "LOT-015", 9, SlotType.EV));
        lotR.addSlot(new ParkingSlot("SLOT-010", "LOT-015", 10, SlotType.MOTORCYCLE));

        lotT.addSlot(new ParkingSlot("SLOT-001", "LOT-016", 1, SlotType.CAR));
        lotT.addSlot(new ParkingSlot("SLOT-002", "LOT-016", 2, SlotType.CAR));
        lotT.addSlot(new ParkingSlot("SLOT-003", "LOT-016", 3, SlotType.MOTORCYCLE));
        lotT.addSlot(new ParkingSlot("SLOT-004", "LOT-016", 4, SlotType.EV));
        lotT.addSlot(new ParkingSlot("SLOT-005", "LOT-016", 5, SlotType.HANDICAPPED));
        lotT.addSlot(new ParkingSlot("SLOT-06", "LOT-016", 6, SlotType.CAR));
        lotT.addSlot(new ParkingSlot("SLOT-007", "LOT-016", 7, SlotType.CAR));
        lotT.addSlot(new ParkingSlot("SLOT-008", "LOT-016", 8, SlotType.COMPACT));
        lotT.addSlot(new ParkingSlot("SLOT-009", "LOT-016", 9, SlotType.EV));
        lotT.addSlot(new ParkingSlot("SLOT-010", "LOT-016", 10, SlotType.MOTORCYCLE));


        lotU.addSlot(new ParkingSlot("SLOT-001", "LOT-017", 1, SlotType.CAR));
        lotU.addSlot(new ParkingSlot("SLOT-002", "LOT-017", 2, SlotType.CAR));
        lotU.addSlot(new ParkingSlot("SLOT-003", "LOT-017", 3, SlotType.MOTORCYCLE));
        lotU.addSlot(new ParkingSlot("SLOT-004", "LOT-017", 4, SlotType.EV));
        lotU.addSlot(new ParkingSlot("SLOT-005", "LOT-017", 5, SlotType.HANDICAPPED));
        lotU.addSlot(new ParkingSlot("SLOT-06", "LOT-017", 6, SlotType.CAR));
        lotU.addSlot(new ParkingSlot("SLOT-007", "LOT-017", 7, SlotType.CAR));
        lotU.addSlot(new ParkingSlot("SLOT-008", "LOT-017", 8, SlotType.COMPACT));
        lotU.addSlot(new ParkingSlot("SLOT-009", "LOT-017", 9, SlotType.EV));
        lotU.addSlot(new ParkingSlot("SLOT-010", "LOT-017", 10, SlotType.MOTORCYCLE));

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
        JLabel userLabel;
        if (currentUser != null) {
            userLabel = new JLabel("Logged in as: " + currentUser.getUsername() +
                    " (" + (isAdmin ? "Administrator" : "Client") + ")");
        } else {
            userLabel = new JLabel("Logged in as: (no user)");
        }
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

        JButton preferencesButton = new JButton("User Preferences");
        preferencesButton.setOpaque(true);
        preferencesButton.setBorderPainted(true);
        preferencesButton.setFont(new Font("Arial", Font.PLAIN, 12));
        preferencesButton.addActionListener(e -> openUserPreferences());

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
        panel.add(preferencesButton);
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
                    currentUser != null ? currentUser.getId() : -1, assignedSlot.getSlotId());
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
                    currentUser != null ? currentUser.getId() : -1,
                    currentLot.getLotId(), available, confidence, notes);

            JOptionPane.showMessageDialog(this,
                    "Thank you for your report!\n" + report,
                    "Report Submitted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Open the User Preferences dialog for the current user.
     * Loads existing preferences from the DB (if any) and saves changes back.
     */
    private void openUserPreferences() {
        // Safety check: make sure there is a logged-in user
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "No logged-in user. Cannot edit preferences.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // DAO for loading/saving preferences
        UserPreferenceDAO prefDAO = new UserPreferenceDAO();

        // Load existing preference for this user (may return null)
        UserPreference existingPref = prefDAO.getPreferenceByUserId(currentUser.getId());

        // Open the dialog, passing the frame, lots list, and existing preference
        UserPreferenceDialog dialog = new UserPreferenceDialog(
                this,
                parkingLots,
                existingPref
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);  // blocks until user closes dialog

        // Get back whatever the dialog ended up with (null if user cancelled)
        UserPreference updatedPref = dialog.getUserPreference();
        if (updatedPref != null) {
            // Make sure the preference is tied to this user
            updatedPref.setUserID(currentUser.getId());

            boolean ok = prefDAO.saveOrUpdatePreference(updatedPref);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Preferences saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not save preferences. Check logs/console for details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
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
