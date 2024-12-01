package org.example.View;

import org.example.Model.Customer;
import org.example.Model.MasterPersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class AddCustomerScreen extends JFrame {
    private final JTextField txtCNIC;
    private final JTextField txtCustomerName;
    private final JTextField txtAddress;
    private final JTextField txtPhone;
    private final JCheckBox chkIsCommercial;
    private final JCheckBox chkIsThreePhase;
    private boolean isSubmitted = false;
    private Customer newCustomer;

    public AddCustomerScreen(JFrame parent) {
        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(parent);
        ImageIcon logo = new ImageIcon("org/example/Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());

        txtCNIC = new JTextField();
        txtCustomerName = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        chkIsCommercial = new JCheckBox();
        chkIsThreePhase = new JCheckBox();

        JLabel lblConnectionDate = new JLabel("Auto-filled");

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("CNIC:"));
        panel.add(txtCNIC);

        panel.add(new JLabel("Customer Name:"));
        panel.add(txtCustomerName);

        panel.add(new JLabel("Address:"));
        panel.add(txtAddress);

        panel.add(new JLabel("Phone:"));
        panel.add(txtPhone);

        panel.add(new JLabel("Is Commercial:"));
        panel.add(chkIsCommercial);

        panel.add(new JLabel("Is Three-Phase:"));
        panel.add(chkIsThreePhase);

        panel.add(new JLabel("Connection Date:"));
        panel.add(lblConnectionDate);

        JButton btnAdd;
        panel.add(btnAdd = new JButton("Add"));
        JButton btnCancel;
        panel.add(btnCancel = new JButton("Cancel"));

        // Button action listeners
        btnAdd.addActionListener(e -> {

            if (txtAddress.getText().isEmpty()) {
                showError("Address cannot be empty!");
                return;
            }
            if (txtAddress.getText().contains(",")) {
                showError("Commas are not allowed in the address!");
                return;
            }
            if (txtCNIC.getText().isEmpty() || txtCNIC.getText().length() != 13) {
                showError("CNIC must be 13 digits long!");
                return;
            }
            if (txtCNIC.getText().contains(",")) {
                showError("Commas are not allowed in the CNIC!");
                return;
            }
            if (txtCustomerName.getText().isEmpty()) {
                showError("Customer Name cannot be empty!");
                return;
            }
            if (txtPhone.getText().isEmpty() || txtPhone.getText().length() != 11) {
                showError("Phone cannot be empty!");
                return;
            }

            ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
            String maxID = "1000";
            for (Customer customer : customers) {
                if (Integer.parseInt(customer.getCustomerID()) > Integer.parseInt(maxID)) {
                    maxID = customer.getCustomerID();
                }
            }

            int CNICcount = 0;
            for (Customer customer : customers) {
                if (customer.getCNIC().equals(txtCNIC.getText())) {
                    CNICcount++;
                }
                if (CNICcount >= 3) {
                    JOptionPane.showMessageDialog(this, "More than 3 meters can not be added for one CNIC! Aborting addition...", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    return;
                }
            }

            maxID = String.valueOf(Integer.parseInt(maxID) + 1);

            String CNIC = txtCNIC.getText();
            String customerName = txtCustomerName.getText();
            String address = txtAddress.getText();
            String phone = txtPhone.getText();
            boolean isCommercial = chkIsCommercial.isSelected();
            boolean isThreePhase = chkIsThreePhase.isSelected();
            float regularUnits = 0.0f;
            float peakUnits = 0.0f;

            String connectionDate = String.format("%02d-%02d-%04d", java.time.LocalDate.now().getDayOfMonth(), java.time.LocalDate.now().getMonthValue(), java.time.LocalDate.now().getYear()); // Autofilled with today's date

            newCustomer = new Customer(maxID, CNIC, customerName, address, phone, isCommercial, isThreePhase, connectionDate, regularUnits, peakUnits);
            MasterPersistence.getInstance().getCustomers().add(newCustomer);
            MasterPersistence.getInstance().setCustomersUpdated();
            JOptionPane.showMessageDialog(this, "Customer added!");
            isSubmitted = true;
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }
}
