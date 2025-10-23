package com.parkinglotmanager.gui;

import com.parkinglotmanager.model.Admin;
import com.parkinglotmanager.model.Client;
import com.parkinglotmanager.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Login and registration GUI for the Parking Lot Management System.
 * Handles user authentication and account creation.
 */
public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, User> userDatabase;
    private User loggedInUser;

    // The one and only admin username
    private static final String ADMIN_USERNAME = "admin";

    public LoginGUI() {
        initializeUserDatabase();
        setupUI();
    }

    /**
     * Initialize the user database with default accounts
     */
    private void initializeUserDatabase() {
        userDatabase = new HashMap<>();

        // Create default admin account
        Admin admin = new Admin("admin", "Admin", "User", "admin@parking.com", hashPassword("admin123"));
        admin.setId(1);
        userDatabase.put("admin", admin);

        // Create default client account
        Client client = new Client("client", "Demo", "Client", "client@parking.com", hashPassword("client123"));
        client.setId(2);
        userDatabase.put("client", client);
    }

    /**
     * Simple password hashing (for demonstration - in production use proper
     * hashing)
     */
    private String hashPassword(String password) {
        // Simple hash for demo purposes
        return "hashed_" + password;
    }

    /**
     * Sets up the login/signup user interface
     */
    private void setupUI() {
        setTitle("Parking Lot Manager - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("Parking Lot Manager System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 5, 5, 5);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        mainPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(52, 152, 219));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setOpaque(true);
        signupButton.setBorderPainted(false);
        signupButton.setFont(new Font("Arial", Font.BOLD, 12));
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        mainPanel.add(signupButton, gbc);

        gbc.gridx = 2;
        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(false);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mainPanel.add(exitButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JLabel infoLabel = new JLabel("<html><center><b>Default Accounts:</b><br>" +
                "Admin: admin / admin123<br>" +
                "Client: client / client123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        // Enter key listener
        passwordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Center the window
        setLocationRelativeTo(null);
    }

    /**
     * Handle user login
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDatabase.get(username);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Account not found! Please sign up first.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(password);
        if (!user.authenticate(hashedPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Successful login
        loggedInUser = user;
        JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getFullName() + "!\nAccount Type: " +
                        (user instanceof Admin ? "Administrator" : "Client"),
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

        // Open main application
        openMainApplication();
    }

    /**
     * Handle user signup
     */
    private void handleSignup() {
        JPanel signupPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        JTextField usernameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        signupPanel.add(new JLabel("Username:"));
        signupPanel.add(usernameField);
        signupPanel.add(new JLabel("First Name:"));
        signupPanel.add(firstNameField);
        signupPanel.add(new JLabel("Last Name:"));
        signupPanel.add(lastNameField);
        signupPanel.add(new JLabel("Email:"));
        signupPanel.add(emailField);
        signupPanel.add(new JLabel("Password:"));
        signupPanel.add(passwordField);
        signupPanel.add(new JLabel("Confirm Password:"));
        signupPanel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, signupPanel,
                "Create New Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validate input
            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All fields are required!",
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!",
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username already registered! Please login instead.",
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 6 characters long!",
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new user account
            String hashedPassword = hashPassword(password);
            User newUser;

            // Only the predefined admin username can be an admin
            // All other users are automatically clients
            if (ADMIN_USERNAME.equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot create account with admin username!",
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                newUser = new Client(username, firstName, lastName, email, hashedPassword);
            }

            newUser.setId(userDatabase.size() + 1);
            userDatabase.put(username, newUser);

            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now login with your username and password.",
                    "Signup Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Pre-fill username field
            this.usernameField.setText(username);
            this.passwordField.setText("");
        }
    }

    /**
     * Open the main application window
     */
    private void openMainApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ParkingLotManagerGUI mainGUI = new ParkingLotManagerGUI(loggedInUser);
                mainGUI.setVisible(true);
                dispose(); // Close login window
            }
        });
    }

    /**
     * Get the logged in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Main method to launch the login GUI
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}
