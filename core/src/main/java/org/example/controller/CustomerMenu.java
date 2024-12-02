package org.example.controller;

import org.example.model.*;
import org.example.view.BillEstimator;
import org.example.view.BillViewer;
import org.example.view.CNICUpdateScreen;
import org.example.view.CustomerMenuScreen;

import javax.swing.*;
import java.util.ArrayList;

public class CustomerMenu extends Menu {

    private final Customer myCustomer;

    public CustomerMenu(User customer) {
        this.message = """
                Customer Menu
                1. View Bills
                2. Estimate Upcoming Bill
                3. Update CNIC Expiry
                4. Exit
                """;
        this.myCustomer = (Customer) customer;
    }

    public void runMenuGUI() {
        new CustomerMenuScreen(this);
    }

    public void executeMenuTask(int choice, JFrame customerMenu) {
        switch (choice) {
            case CustomerMenuScreen.VIEW_BILL:
                viewBills(MasterPersistence.getInstance().getBillingRecords());
                break;
            case CustomerMenuScreen.ESTIMATE_UPCOMING_BILL:
                estimateUpcomingBills(MasterPersistence.getInstance().getTariffTaxes());
                break;
            case CustomerMenuScreen.UPDATE_CNIC_EXPIRY:
                updateCNICExpiry(MasterPersistence.getInstance().getNadraRecords());
                break;
            case CustomerMenuScreen.EXIT:
                customerMenu.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(customerMenu, "Invalid choice!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCNICExpiry(ArrayList<NADRARecord> nadraRecords) {
        new CNICUpdateScreen(nadraRecords, myCustomer);
    }

    private void viewBills(ArrayList<BillingRecord> billingRecords) {
        BillViewer billViewer = new BillViewer();
        for (BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(myCustomer.getUsername())) {

                String row = br.toFileString();
                String[] rowData = row.trim().split(",");
                billViewer.addRow(rowData);
            }
        }
    }

    public void estimateUpcomingBills(ArrayList<TariffTax> tariffTaxes) {
        new BillEstimator(myCustomer);
    }

}