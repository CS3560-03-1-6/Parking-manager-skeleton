package com.parkinglotmanager.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.parkinglotmanager.dao.UserDAO;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.util.DatabaseConnection;

public class LoginGUI extends JFrame {

    // --- Constants for Styling ---
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);   // Blue
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);   // Green
    private static final Color COLOR_DANGER  = new Color(231, 76, 60);    // Red
    private static final Font  FONT_HEADER   = new Font("Arial", Font.BOLD, 24);
    private static final Font  FONT_NORMAL   = new Font("Arial", Font.BOLD, 12);

    // --- State ---
    private static final String ADMIN_USERNAME = "admin";
    private User loggedInUser;

    // --- UI Components ---
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setupWindow();
        buildUI();
    }

    // ==========================================
    // 1. INITIALIZATION & SETUP
    // ==========================================
    


    private void setupWindow() {
        setTitle("Parking Lot Manager");
        setSize(450, 400); // Slightly taller for breathing room
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center screen
        
        try {
            BufferedImage icon = ImageIO.read(new File("Resources/java-icon.png"));
            setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Icon load failed: " + e.getMessage());
        }

        // Startup DB Check
        DatabaseConnection.testConnection(); 
    }

    private void buildUI() {
        // Top
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center (Form)
        add(createMainForm(), BorderLayout.CENTER);
        
        // Bottom (Info)
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // ==========================================
    // 2. UI BUILDER METHODS
    // ==========================================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel titleLabel = new JLabel("Parking Manager System");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        return panel;
    }

    private JPanel createMainForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Initialize Fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        // Allow "Enter" key to trigger login
        passwordField.addActionListener(e -> handleLogin());

        // --- Row 0: Username ---
        addComponent(panel, new JLabel("Username:"), 0, 0, 1);
        addComponent(panel, usernameField, 1, 0, 2);

        // --- Row 1: Password ---
        addComponent(panel, new JLabel("Password:"), 0, 1, 1);
        addComponent(panel, passwordField, 1, 1, 2);

        // --- Row 2: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(createStyledButton("Login", COLOR_SUCCESS, e -> handleLogin()));
        buttonPanel.add(createStyledButton("Sign Up", COLOR_PRIMARY, e -> handleSignup()));
        buttonPanel.add(createStyledButton("Exit", COLOR_DANGER, e -> System.exit(0)));

        // Add button panel spanning 3 columns
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JLabel infoLabel = new JLabel("<html><center>Default: admin/admin123 or client/client123</center></html>");
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel);
        return panel;
    }

    /**
     * Helper to reduce GridBagLayout clutter
     */
    private void addComponent(JPanel panel, Component comp, int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(comp, gbc);
    }

    /**
     * Helper to create consistent flat buttons
     */
    private JButton createStyledButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(FONT_NORMAL);
        btn.addActionListener(action);
        return btn;
    }

    // ==========================================
    // 3. LOGIC & HANDLERS
    // ==========================================

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        if (user == null) {
            showError("Account not found! Please sign up first.");
            return;
        }

        if (!user.authenticate(hashPassword(password))) {
            showError("Invalid password!");
            return;
        }

        // Success
        loggedInUser = user;
        JOptionPane.showMessageDialog(this, "Welcome back, " + user.getUsername() + "!");
        
        SwingUtilities.invokeLater(() -> {
            new ParkingLotManagerGUI(loggedInUser).setVisible(true);
            dispose();
        });
    }

    private void handleSignup() {
        // Create form fields for the popup
        JTextField userTxt = new JTextField();
        JTextField firstTxt = new JTextField();
        JTextField lastTxt = new JTextField();
        JTextField emailTxt = new JTextField();
        JPasswordField passTxt = new JPasswordField();
        JPasswordField confTxt = new JPasswordField();

        Object[] message = {
            "Username:", userTxt,
            "First Name:", firstTxt,
            "Last Name:", lastTxt,
            "Email:", emailTxt,
            "Password:", passTxt,
            "Confirm Password:", confTxt
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create Account", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = userTxt.getText().trim();
            String password = new String(passTxt.getPassword());

            UserDAO userDAO = new UserDAO();
            // Basic Validation
            if (username.isEmpty() || password.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            if (!password.equals(new String(confTxt.getPassword()))) {
                showError("Passwords do not match.");
                return;
            }
            // Check if exists in DB
            if (userDAO.getUserByUsername(username) != null) {
                showError("Username taken.");
                return;
            }
            if (ADMIN_USERNAME.equalsIgnoreCase(username)) {
                showError("Cannot create admin account.");
                return;
            }

            // Register and Save to DB
            boolean success = userDAO.registerUser(username, 
                                                    emailTxt.getText(), 
                                                    hashPassword(password), 
                                                    false // Default is not admin
                                                );

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created! You may now login.");
            } else {
                showError("Database error: Could not create account.");
            }
            usernameField.setText(username);
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String hashPassword(String password) {
        return "hashed_" + password; // Demo hash
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}