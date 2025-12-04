package com.parkinglotmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.parkinglotmanager.dao.UserDAO;
import com.parkinglotmanager.model.User;
import com.parkinglotmanager.util.DatabaseConnection;

public class LoginGUI extends JFrame {

    private static final Color HEADER_BLUE = new Color(167, 199, 231);   // soft header blue
    private static final Color TEXT_COLOR = new Color(106, 123, 162);
    private static final Color FIELD_BORDER = new Color(180, 180, 180);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private User loggedInUser;

    public LoginGUI() {
        setupWindow();
        buildUI();
    }

    private void setupWindow() {
        setTitle("Parking Manager");
        setSize(600, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        try {
            BufferedImage icon = ImageIO.read(new File("Resources/java-icon.png"));
            setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Icon failed: " + e.getMessage());
        }

        DatabaseConnection.testConnection();
    }

    private void buildUI() {

        //header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        JLabel title = new JLabel("Parking Lot Manager System", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // login card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        usernameField = createField();
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        passwordField.addActionListener(e -> handleLogin());

        addFormRow(card, gbc, 0, "Username:", usernameField);
        addFormRow(card, gbc, 1, "Password:", passwordField);

        // Buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonRow.setOpaque(false);

        JButton loginBtn = createButton("Login");
        loginBtn.addActionListener(e -> handleLogin());

        JButton signupBtn = createButton("Sign Up");
        signupBtn.addActionListener(e -> handleSignup());

        JButton exitBtn = createButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));

        buttonRow.add(loginBtn);
        buttonRow.add(signupBtn);
        buttonRow.add(exitBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(buttonRow, gbc);

        add(card, BorderLayout.CENTER);

        // footer
        JLabel hint = new JLabel(
            "Default: admin/admin123 or client/client123",
            SwingConstants.CENTER
        );
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_COLOR);
        hint.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        add(hint, BorderLayout.SOUTH);
    }
    
    //helpers
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                            String labelText, JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private JTextField createField() {
        JTextField field = new JTextField(20);
        styleTextField(field);
        return field;
    }

    private void styleTextField(JComponent comp) {
        comp.setFont(FONT_FIELD);
        comp.setForeground(TEXT_COLOR);
        comp.setBackground(Color.WHITE);

        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER_BLUE, 2),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        // hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(225, 235, 245));
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        try {

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password.");
                return;
            }

            if (user == null) {
                showError("Account not found! Please sign up first.");
                return;
            }

            if (!user.authenticate(password)) {
                showError("Invalid password!");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        loggedInUser = user;

        JOptionPane.showMessageDialog(this,
                "Welcome back, " + user.getUsername() + "!");

        SwingUtilities.invokeLater(() -> {
            new ParkingLotManagerGUI(loggedInUser).setVisible(true);
            dispose();
        });
    }

    private void handleSignup() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 9, 9, 9);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField userTxt = new JTextField(14);
        JTextField emailTxt = new JTextField(14);
        JPasswordField passTxt = new JPasswordField(14);
        JPasswordField confTxt = new JPasswordField(14);

        styleTextField(userTxt);
        styleTextField(emailTxt);
        styleTextField(passTxt);
        styleTextField(confTxt);

        JLabel uLabel = new JLabel("Username:");
        JLabel eLabel = new JLabel("Email:");
        JLabel pLabel = new JLabel("Password:");
        JLabel cLabel = new JLabel("Confirm Password:");

        uLabel.setFont(FONT_LABEL);
        eLabel.setFont(FONT_LABEL);
        pLabel.setFont(FONT_LABEL);
        cLabel.setFont(FONT_LABEL);

        uLabel.setForeground(TEXT_COLOR);
        eLabel.setForeground(TEXT_COLOR);
        pLabel.setForeground(TEXT_COLOR);
        cLabel.setForeground(TEXT_COLOR);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(uLabel, gbc);
        gbc.gridx = 1; panel.add(userTxt, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(eLabel, gbc);
        gbc.gridx = 1; panel.add(emailTxt, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(pLabel, gbc);
        gbc.gridx = 1; panel.add(passTxt, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(cLabel, gbc);
        gbc.gridx = 1; panel.add(confTxt, gbc);

        panel.setPreferredSize(new Dimension(500, 220));

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Create Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            String username = userTxt.getText().trim();
            String email = emailTxt.getText().trim();
            String pass = new String(passTxt.getPassword());
            String conf = new String(confTxt.getPassword());

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }

            if (!pass.equals(conf)) {
                showError("Passwords do not match.");
                return;
            }

            UserDAO dao = new UserDAO();

            if (dao.getUserByUsername(username) != null) {
                showError("Username already exists.");
                return;
            }

            try {
                User temp = new User(username, "", "", email, pass);

                int newId = dao.registerUser(
                        temp.getUsername(),
                        temp.getEmail(),
                        temp.getPasswordHash(),
                        false
                );

                if (newId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Account created! You may now log in.");
                } else {
                    showError("Could not create the account.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error.");
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
