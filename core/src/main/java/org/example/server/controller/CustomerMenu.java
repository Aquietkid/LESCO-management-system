package org.example.server.controller;

import org.example.client.view.BillEstimator;
import org.example.client.view.BillViewer;
import org.example.client.view.CNICUpdateScreen;
import org.example.client.view.CustomerMenuScreen;
import org.example.commons.model.User;

import javax.swing.*;
import java.util.ArrayList;

public class CustomerMenu extends Menu {

    private final org.example.commons.model.Customer myCustomer;

    public CustomerMenu(User customer) {
        this.message = """
                Customer Menu
                1. View Bills
                2. Estimate Upcoming Bill
                3. Update CNIC Expiry
                4. Exit
                """;
        this.myCustomer = (org.example.commons.model.Customer) customer;
    }

    public void runMenuGUI() {
        new CustomerMenuScreen(this);
    }

    public void executeMenuTask(int choice, JFrame customerMenu) {
        switch (choice) {
            case CustomerMenuScreen.VIEW_BILL:
                viewBills(org.example.commons.model.MasterPersistence.getInstance().getBillingRecords());
                break;
            case CustomerMenuScreen.ESTIMATE_UPCOMING_BILL:
                estimateUpcomingBills(org.example.commons.model.MasterPersistence.getInstance().getTariffTaxes());
                break;
            case CustomerMenuScreen.UPDATE_CNIC_EXPIRY:
                updateCNICExpiry(org.example.commons.model.MasterPersistence.getInstance().getNadraRecords());
                break;
            case CustomerMenuScreen.EXIT:
                customerMenu.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(customerMenu, "Invalid choice!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCNICExpiry(ArrayList<org.example.commons.model.NADRARecord> nadraRecords) {
        new CNICUpdateScreen(nadraRecords, myCustomer);
    }

    private void viewBills(ArrayList<org.example.commons.model.BillingRecord> billingRecords) {
        BillViewer billViewer = new BillViewer();
        for (org.example.commons.model.BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(myCustomer.getUsername())) {

                String row = br.toFileString();
                String[] rowData = row.trim().split(",");
                billViewer.addRow(rowData);
            }
        }
    }

    public void estimateUpcomingBills(ArrayList<org.example.commons.model.TariffTax> tariffTaxes) {
        new BillEstimator(myCustomer);
    }

}