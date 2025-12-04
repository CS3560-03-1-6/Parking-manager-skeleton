package com.parkinglotmanager.gui;

import com.parkinglotmanager.dao.VehicleSessionDAO;
import com.parkinglotmanager.dao.VehicleDAO;
import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.ParkingSlot;
import com.parkinglotmanager.model.VehicleSession;
import com.parkinglotmanager.enums.SlotType;
import com.parkinglotmanager.enums.VehicleType;
import com.parkinglotmanager.enums.VehicleMake;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Real-time visual simulation of parking lot activity
 * Shows vehicles entering, parking, and leaving in real-time
 */
public class ParkingVisualization extends JFrame {
    private ParkingLot currentLot;
    private VehicleSessionDAO sessionDAO;
    private VehicleDAO vehicleDAO;
    private VisualizationPanel visualPanel;
    private JLabel statusLabel;
    private Timer refreshTimer;
    private Timer simulationTimer;
    private boolean simulationRunning;
    private Random random;
    private int simulationUserId;
    private JButton simulationButton;

    public ParkingVisualization(ParkingLot lot) {
        this.currentLot = lot;
        this.sessionDAO = new VehicleSessionDAO();
        this.vehicleDAO = new VehicleDAO();
        this.simulationRunning = false;
        this.random = new Random();
        this.simulationUserId = 1; // Default test user ID

        setupUI();
        startAutoRefresh();
    }

    private void setupUI() {
        setTitle("Parking Lot Visualization - " + currentLot.getLotName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Real-Time Parking Visualization");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Loading...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Visualization panel
        visualPanel = new VisualizationPanel();
        JScrollPane scrollPane = new JScrollPane(visualPanel);

        // Control panel with simulation button
        JPanel controlPanel = createControlPanel();

        // Legend
        JPanel legendPanel = createLegendPanel();

        // Bottom panel combining controls and legend
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.NORTH);
        bottomPanel.add(legendPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));

        simulationButton = new JButton("Start Simulation");
        simulationButton.setFont(new Font("Arial", Font.BOLD, 14));
        simulationButton.setBackground(new Color(39, 174, 96));
        simulationButton.setForeground(Color.green);
        simulationButton.setFocusPainted(false);
        simulationButton.addActionListener(e -> toggleSimulation());

        JLabel infoLabel = new JLabel("Click to start automatic test parking activity");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);

        panel.add(simulationButton);
        panel.add(infoLabel);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Legend"));

        panel.add(createLegendItem("Available", new Color(46, 204, 113)));
        panel.add(createLegendItem("Occupied", new Color(231, 76, 60)));
        panel.add(createLegendItem("EV Charging", new Color(52, 152, 219)));
        panel.add(createLegendItem("Motorcycle", new Color(155, 89, 182)));
        panel.add(createLegendItem("Handicapped", new Color(241, 196, 15)));

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private void startAutoRefresh() {
        // Refresh every 100ms to show ultra-fast updates
        refreshTimer = new Timer(100, e -> updateVisualization());
        refreshTimer.start();
        updateVisualization(); // Initial update
    }

    private void updateVisualization() {
        // Load active sessions from database
        List<VehicleSession> activeSessions = sessionDAO.getActiveSessions();

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

        // Update status
        int occupied = (int) currentLot.getSlots().stream().filter(ParkingSlot::isOccupied).count();
        int total = currentLot.getSlots().size();
        int available = total - occupied;
        double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0;

        statusLabel.setText(String.format("Occupied: %d | Available: %d | Total: %d | Occupancy: %.1f%%",
                occupied, available, total, occupancyRate));

        visualPanel.updateSlots(currentLot.getSlots());
        visualPanel.repaint();
    }

    private void toggleSimulation() {
        if (simulationRunning) {
            stopSimulation();
        } else {
            startSimulation();
        }
    }

    private void startSimulation() {
        simulationRunning = true;
        simulationButton.setText("Stop Simulation");
        simulationButton.setBackground(new Color(231, 76, 60));

        // Run simulation events every 5ms with batch processing (100x faster)
        simulationTimer = new Timer(5, e -> {
            // Process 5-10 events per tick for maximum speed
            int batchSize = 5 + random.nextInt(6);
            for (int i = 0; i < batchSize; i++) {
                performSimulationEvent();
            }
        });
        simulationTimer.start();

        JOptionPane.showMessageDialog(this,
                "ULTRA-FAST Simulation started!\n" +
                        "Vehicles will park/exit extremely rapidly.\n" +
                        "Processing 5-10 events every 5ms (100x faster).",
                "Simulation Started",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void stopSimulation() {
        simulationRunning = false;
        if (simulationTimer != null) {
            simulationTimer.stop();
        }
        simulationButton.setText("Start Simulation");
        simulationButton.setBackground(new Color(39, 174, 96));
    }

    private void performSimulationEvent() {
        // Randomly decide: park a new vehicle (95%) or exit an existing one (5%)
        List<VehicleSession> activeSessions = sessionDAO.getActiveSessions();
        boolean shouldPark = random.nextDouble() < 0.95 || activeSessions.isEmpty();

        if (shouldPark) {
            parkRandomVehicle();
        } else {
            exitRandomVehicle(activeSessions);
        }
    }

    private void parkRandomVehicle() {
        // Find available slots
        List<ParkingSlot> availableSlots = new ArrayList<>();
        for (ParkingSlot slot : currentLot.getSlots()) {
            if (!slot.isOccupied()) {
                availableSlots.add(slot);
            }
        }

        if (availableSlots.isEmpty()) {
            return; // Lot is full
        }

        // Pick random slot
        ParkingSlot selectedSlot = availableSlots.get(random.nextInt(availableSlots.size()));

        // Generate random vehicle
        VehicleType[] vehicleTypes = VehicleType.values();
        VehicleType vehicleType = vehicleTypes[random.nextInt(vehicleTypes.length)];

        VehicleMake[] vehicleMakes = VehicleMake.values();
        VehicleMake vehicleMake = vehicleMakes[random.nextInt(vehicleMakes.length)];

        String licensePlate = generateRandomLicensePlate();

        // Check slot compatibility
        if (!selectedSlot.getSlotType().isCompatibleWith(vehicleType)) {
            return; // Try again next time
        }

        // Add vehicle to database using VehicleDAO
        String makeStr = vehicleMake.toString();
        String model = "Model-" + random.nextInt(1000);
        String[] colors = { "Black", "White", "Silver", "Red", "Blue", "Gray" };
        String color = colors[random.nextInt(colors.length)];

        vehicleDAO.addVehicle(simulationUserId, licensePlate, makeStr, model, color);

        // Create parking session using the simpler constructor
        VehicleSession session = new VehicleSession(
                licensePlate,
                vehicleType,
                vehicleMake,
                simulationUserId,
                selectedSlot.getSlotId());

        sessionDAO.insertSession(session);
        System.out.println("[SIMULATION] Vehicle " + licensePlate + " parked in slot " + selectedSlot.getSlotNumber());
    }

    private void exitRandomVehicle(List<VehicleSession> activeSessions) {
        if (activeSessions.isEmpty()) {
            return;
        }

        // Pick random active session
        VehicleSession sessionToExit = activeSessions.get(random.nextInt(activeSessions.size()));

        // Calculate random fee between $2-$20
        double fee = 2.0 + (random.nextDouble() * 18.0);

        // Complete the session using getId() method
        sessionDAO.completeSession(sessionToExit.getId(), fee);
        System.out.println("[SIMULATION] Vehicle " + sessionToExit.getLicensePlate() + " exited. Fee: $"
                + String.format("%.2f", fee));
    }

    private String generateRandomLicensePlate() {
        // Generate format: ABC1234
        StringBuilder plate = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            plate.append((char) ('A' + random.nextInt(26)));
        }
        for (int i = 0; i < 4; i++) {
            plate.append(random.nextInt(10));
        }
        return plate.toString();
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        if (simulationTimer != null) {
            simulationTimer.stop();
        }
        super.dispose();
    }

    /**
     * Panel that renders the parking lot visualization
     */
    private class VisualizationPanel extends JPanel {
        private List<ParkingSlot> slots;
        private final int SLOT_WIDTH = 60;
        private final int SLOT_HEIGHT = 40;
        private final int SLOTS_PER_ROW = 15;
        private final int MARGIN = 20;

        public VisualizationPanel() {
            slots = new ArrayList<>();
            setBackground(new Color(236, 240, 241));
        }

        public void updateSlots(List<ParkingSlot> newSlots) {
            this.slots = new ArrayList<>(newSlots);

            // Calculate preferred size based on slots
            int rows = (int) Math.ceil(slots.size() / (double) SLOTS_PER_ROW);
            int preferredHeight = (rows * (SLOT_HEIGHT + 10)) + (MARGIN * 2) + 100;
            setPreferredSize(new Dimension(1000, preferredHeight));
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (slots.isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("No parking slots to display", 400, 300);
                return;
            }

            // Draw parking lot layout
            drawParkingLot(g2d);
        }

        private void drawParkingLot(Graphics2D g2d) {
            int x = MARGIN;
            int y = MARGIN + 40;
            int slotIndex = 0;

            // Draw title
            g2d.setColor(new Color(52, 73, 94));
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(currentLot.getLotName() + " - " + currentLot.getLocation(), MARGIN, MARGIN + 20);

            for (ParkingSlot slot : slots) {
                // Draw parking slot
                drawSlot(g2d, slot, x, y);

                // Move to next position
                slotIndex++;
                x += SLOT_WIDTH + 10;

                // New row after SLOTS_PER_ROW slots
                if (slotIndex % SLOTS_PER_ROW == 0) {
                    x = MARGIN;
                    y += SLOT_HEIGHT + 10;
                }
            }
        }

        private void drawSlot(Graphics2D g2d, ParkingSlot slot, int x, int y) {
            // Determine slot color based on status and type
            Color slotColor;
            if (slot.isOccupied()) {
                slotColor = new Color(231, 76, 60); // Red for occupied
            } else {
                // Different colors for different slot types
                switch (slot.getSlotType()) {
                    case EV:
                        slotColor = new Color(52, 152, 219); // Blue
                        break;
                    case MOTORCYCLE:
                        slotColor = new Color(155, 89, 182); // Purple
                        break;
                    case HANDICAPPED:
                        slotColor = new Color(241, 196, 15); // Yellow
                        break;
                    case COMPACT:
                        slotColor = new Color(26, 188, 156); // Teal
                        break;
                    default:
                        slotColor = new Color(46, 204, 113); // Green
                }
            }

            // Draw slot rectangle with gradient
            GradientPaint gradient = new GradientPaint(
                    x, y, slotColor,
                    x, y + SLOT_HEIGHT, slotColor.darker());
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, SLOT_WIDTH, SLOT_HEIGHT, 5, 5);

            // Draw border
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, SLOT_WIDTH, SLOT_HEIGHT, 5, 5);

            // Draw slot number
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String slotNum = String.valueOf(slot.getSlotNumber());
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (SLOT_WIDTH - fm.stringWidth(slotNum)) / 2;
            int textY = y + 15;
            g2d.drawString(slotNum, textX, textY);

            // Draw slot type icon
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            String typeIcon = getSlotTypeIcon(slot.getSlotType());
            int iconX = x + (SLOT_WIDTH - fm.stringWidth(typeIcon)) / 2;
            g2d.drawString(typeIcon, iconX, y + 28);

            // Draw vehicle if occupied
            if (slot.isOccupied()) {
                drawVehicle(g2d, slot, x + SLOT_WIDTH / 2, y + SLOT_HEIGHT / 2);
            }
        }

        private String getSlotTypeIcon(SlotType type) {
            switch (type) {
                case EV:
                    return "[EV]";
                case MOTORCYCLE:
                    return "[M]";
                case HANDICAPPED:
                    return "[H]";
                case COMPACT:
                    return "[C]";
                default:
                    return "[CAR]";
            }
        }

        private void drawVehicle(Graphics2D g2d, ParkingSlot slot, int centerX, int centerY) {
            // Draw a simple car icon
            int carWidth = 30;
            int carHeight = 20;
            int carX = centerX - carWidth / 2;
            int carY = centerY - carHeight / 2;

            // Car body
            g2d.setColor(new Color(189, 195, 199));
            g2d.fillRoundRect(carX, carY, carWidth, carHeight, 5, 5);

            // Windshield
            g2d.setColor(new Color(127, 140, 141));
            g2d.fillRect(carX + 5, carY + 3, 8, 6);
            g2d.fillRect(carX + carWidth - 13, carY + 3, 8, 6);

            // Wheels (small circles)
            g2d.setColor(Color.BLACK);
            g2d.fillOval(carX + 3, carY + carHeight - 5, 6, 6);
            g2d.fillOval(carX + carWidth - 9, carY + carHeight - 5, 6, 6);
        }
    }
}
