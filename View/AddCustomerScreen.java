package View;

import Model.Customer;

import javax.swing.*;
import java.awt.*;

public class AddCustomerScreen extends JDialog {
    private JTextField txtCustomerID, txtCNIC, txtCustomerName, txtAddress, txtPhone, txtRegularUnits, txtPeakUnits;
    private JCheckBox chkIsCommercial, chkIsThreePhase;
    private JLabel lblConnectionDate;
    private JButton btnAdd, btnCancel;
    private boolean isSubmitted = false;
    private Customer newCustomer;

    public AddCustomerScreen(Frame parent) {
        super(parent, "Add New Customer", true);
        setLayout(new GridBagLayout());
        setSize(400, 500); // Increased size for better visibility
        setLocationRelativeTo(parent);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Set padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize input fields
        txtCustomerID = new JTextField();
        txtCNIC = new JTextField();
        txtCustomerName = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        txtRegularUnits = new JTextField();
        txtPeakUnits = new JTextField();
        chkIsCommercial = new JCheckBox();
        chkIsThreePhase = new JCheckBox();

        lblConnectionDate = new JLabel("Auto-filled");

        // Add components with proper layout positions
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        add(txtCustomerID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("CNIC:"), gbc);
        gbc.gridx = 1;
        add(txtCNIC, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        add(txtCustomerName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        add(txtAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Is Commercial:"), gbc);
        gbc.gridx = 1;
        add(chkIsCommercial, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Is Three-Phase:"), gbc);
        gbc.gridx = 1;
        add(chkIsThreePhase, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Regular Units Consumed:"), gbc);
        gbc.gridx = 1;
        add(txtRegularUnits, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Peak Units Consumed:"), gbc);
        gbc.gridx = 1;
        add(txtPeakUnits, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(new JLabel("Connection Date:"), gbc);
        gbc.gridx = 1;
        add(lblConnectionDate, gbc);

        // Add buttons
        gbc.gridx = 0;
        gbc.gridy = 10;
        add(new JLabel(""), gbc); // Empty space filler
        gbc.gridx = 0;
        gbc.gridy = 11;
        add(btnAdd = new JButton("Add"), gbc);
        gbc.gridx = 1;
        add(btnCancel = new JButton("Cancel"), gbc);

        // Button action listeners
        btnAdd.addActionListener(e -> {
            if (validateInput()) {
                String customerID = txtCustomerID.getText();
                String CNIC = txtCNIC.getText();
                String customerName = txtCustomerName.getText();
                String address = txtAddress.getText();
                String phone = txtPhone.getText();
                boolean isCommercial = chkIsCommercial.isSelected();
                boolean isThreePhase = chkIsThreePhase.isSelected();
                float regularUnits = Float.parseFloat(txtRegularUnits.getText());
                float peakUnits = Float.parseFloat(txtPeakUnits.getText());
                String connectionDate = java.time.LocalDate.now().toString(); // Auto-filled with today's date

                // Create new customer object
                newCustomer = new Customer(customerID, CNIC, customerName, address, phone, isCommercial, isThreePhase, connectionDate, regularUnits, peakUnits);
                JOptionPane.showMessageDialog(this, "Customer added!");
                isSubmitted = true;
                dispose(); // Close dialog after submission
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }

    // Validate input fields before submission
    private boolean validateInput() {
        if (txtCustomerID.getText().isEmpty()) {
            showError("Customer ID cannot be empty.");
            return false;
        }
        if (txtCNIC.getText().isEmpty() || txtCNIC.getText().length() != 13) {
            showError("CNIC must be 13 digits long.");
            return false;
        }
        if (txtCustomerName.getText().isEmpty()) {
            showError("Customer Name cannot be empty.");
            return false;
        }
        if (txtRegularUnits.getText().isEmpty() || !isNumeric(txtRegularUnits.getText())) {
            showError("Regular Units must be a valid number.");
            return false;
        }
        if (txtPeakUnits.getText().isEmpty() || !isNumeric(txtPeakUnits.getText())) {
            showError("Peak Units must be a valid number.");
            return false;
        }
        return true;
    }

    // Check if a string is a valid number
    private boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Display an error message
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    // Method to return new customer
    public Customer getNewCustomer() {
        return newCustomer;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }
}
