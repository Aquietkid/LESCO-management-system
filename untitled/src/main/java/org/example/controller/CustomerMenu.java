package org.example.controller;

import org.example.Model.*;
import org.example.View.BillEstimator;
import org.example.View.BillViewer;
import org.example.View.CNICUpdateScreen;
import org.example.View.CustomerMenuScreen;

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

    @Override
    public void displayMenu() {
        System.out.println(this.message);
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
                estimateUpcomingBills(MasterPersistence.getInstance().getTariffTaxes(), customerMenu);
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

    public void estimateUpcomingBills(ArrayList<TariffTax> tariffTaxes, JFrame customerMenu) {
        new BillEstimator(myCustomer);
    }

}