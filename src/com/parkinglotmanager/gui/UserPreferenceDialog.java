package com.parkinglotmanager.gui;

import com.parkinglotmanager.model.ParkingLot;
import com.parkinglotmanager.model.UserPreference;

import javax.swing.*;
import java.awt.*;
import java.util.List;


 //dialog to edit user preferences.

public class UserPreferenceDialog extends JDialog {

    private JComboBox<ParkingLot> lotComboBox;
    private JTextField arrivalField;
    private JTextField classLocationField;

    private UserPreference userPreference;
    private boolean saved = false;

    public UserPreferenceDialog(JFrame parent,
                                List<ParkingLot> parkingLots,
                                UserPreference existingPreference) {
        super(parent, "User Preferences", true);

        this.userPreference = (existingPreference != null)
                ? new UserPreference(
                        existingPreference.getUserID(),
                        existingPreference.getPreferredLotID(),
                        existingPreference.getPreferredArrivalTime(),
                        existingPreference.getClassLocationDescription())
                : new UserPreference();

        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Lot combo box
        formPanel.add(new JLabel("Preferred Lot:"));
        lotComboBox = new JComboBox<>();
        for (ParkingLot lot : parkingLots) {
            lotComboBox.addItem(lot); // uses ParkingLot.toString()
        }
        formPanel.add(lotComboBox);

        // Prefill lot if existing
        if (existingPreference != null && existingPreference.getPreferredLotID() != null) {
            String targetCode = String.format("LOT-%03d", existingPreference.getPreferredLotID());
            for (int i = 0; i < lotComboBox.getItemCount(); i++) {
                ParkingLot lot = lotComboBox.getItemAt(i);
                if (lot.getLotId().equals(targetCode)) {
                    lotComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Arrival time
        formPanel.add(new JLabel("Preferred Arrival Time:"));
        arrivalField = new JTextField();
        if (existingPreference != null) {
            arrivalField.setText(existingPreference.getPreferredArrivalTime());
        }
        formPanel.add(arrivalField);

        // Class location description
        formPanel.add(new JLabel("Class Location / Notes:"));
        classLocationField = new JTextField();
        if (existingPreference != null) {
            classLocationField.setText(existingPreference.getClassLocationDescription());
        }
        formPanel.add(classLocationField);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
    }

    private void onSave() {
        ParkingLot selectedLot = (ParkingLot) lotComboBox.getSelectedItem();
        Integer preferredLotID = null;

        if (selectedLot != null) {
            String code = selectedLot.getLotId(); // ex "LOT-007"
            try {
                preferredLotID = Integer.parseInt(code.substring(4)); // 7
            } catch (Exception ignored) {
                preferredLotID = null;
            }
        }

        userPreference.setPreferredLotID(preferredLotID);
        userPreference.setPreferredArrivalTime(arrivalField.getText().trim());
        userPreference.setClassLocationDescription(classLocationField.getText().trim());

        saved = true;
        dispose();
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public UserPreference getUserPreference() {
        return saved ? userPreference : null;
    }
}
