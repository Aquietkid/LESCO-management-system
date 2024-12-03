package org.example.client.view;

import org.example.commons.model.Customer;
import org.example.commons.model.NADRARecord;
import org.example.commons.utilities.DateInput;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CNICUpdateScreen extends javax.swing.JFrame {
    private final ArrayList<NADRARecord> nadraRecords;
    JLabel lblCNIC;
    JTextField txtCNIC;
    JButton btnUpdateCNICExpiry;
    private final Customer myCustomer;

    public CNICUpdateScreen(ArrayList<NADRARecord> nadraRecords, Customer myCustomer) {
        this.nadraRecords = nadraRecords;
        this.myCustomer = myCustomer;
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
            if(txtCNIC.getText().equals(myCustomer.getCNIC())) {
                for(NADRARecord record : nadraRecords) {
                    if(record.getCNIC().equals(txtCNIC.getText())) {
                        record.setExpiryDate(String.format("%02d-%02d-%04d", dateInput.getDate(), dateInput.getMonth(), dateInput.getYear()));
                        break;
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(this, myCustomer.getCNIC() + " is not your CNIC number! Please enter CNIC again!", "Incorrect CNIC!", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(lblCNIC);
        add(txtCNIC);
        add(dateInput);
        add(btnUpdateCNICExpiry);

        setBounds(560, 190, 800, 600);
        setVisible(true);

    }

}
