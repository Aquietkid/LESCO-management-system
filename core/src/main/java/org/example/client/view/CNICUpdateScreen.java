package org.example.client.view;

import org.example.client.controller.CustomerMenu;
import org.example.commons.model.Customer;
import org.example.commons.model.NADRARecord;
import org.example.commons.utilities.DateInput;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CNICUpdateScreen extends javax.swing.JFrame {
//    private final ArrayList<NADRARecord> nadraRecords;
    JLabel lblCNIC;
    JTextField txtCNIC;
    JButton btnUpdateCNICExpiry;
//    private final Customer myCustomer;
    private CustomerMenu customerMenu;
    private final JFrame customerMenuFrame;

    public CNICUpdateScreen(CustomerMenu customerMenu, JFrame customerMenuFrame) {
        this.customerMenu = customerMenu;
        this.customerMenuFrame = customerMenuFrame;
        init();
    }

    private void init() {
        lblCNIC = new JLabel("CNIC Number");
        txtCNIC = new JTextField();
        DateInput dateInput = new DateInput();
        btnUpdateCNICExpiry = new JButton("Update");

        setTitle("Update CNIC Expiry");

        setLayout(new GridLayout(2, 2, 20, 20));

        btnUpdateCNICExpiry.addActionListener(e -> {
            if(Boolean.TRUE.equals(customerMenu.updateCNICExpiry(String.format("%02d-%02d-%04d", dateInput.getDate(), dateInput.getMonth(), dateInput.getYear())))) {
                JOptionPane.showMessageDialog(this, "CNIC expiry updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else JOptionPane.showMessageDialog(this, "Failed to update CNIC expiry!", "Error", JOptionPane.ERROR_MESSAGE);
        });

        add(lblCNIC);
        add(txtCNIC);
        add(dateInput);
        add(btnUpdateCNICExpiry);

        setBounds(560, 190, 800, 600);
        setVisible(true);

    }

}
