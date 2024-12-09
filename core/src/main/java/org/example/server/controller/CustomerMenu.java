package org.example.server.controller;

import org.example.client.view.BillEstimator;
import org.example.client.view.BillViewer;
import org.example.client.view.CustomerMenuScreen;
import org.example.commons.model.MasterPersistence;
import org.example.commons.model.NADRARecord;
import org.example.commons.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

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
        new CustomerMenuScreen(new org.example.client.controller.CustomerMenu(myCustomer));
    }

    public void executeMenuTask(int choice, JFrame customerMenu) {
        switch (choice) {
            case CustomerMenuScreen.VIEW_BILL:
//                viewBills(org.example.commons.model.MasterPersistence.getInstance().getBillingRecords());
                break;
            case CustomerMenuScreen.ESTIMATE_UPCOMING_BILL:
                estimateUpcomingBills(org.example.commons.model.MasterPersistence.getInstance().getTariffTaxes());
                break;
            case CustomerMenuScreen.UPDATE_CNIC_EXPIRY:
//                updateCNICExpiry();
                break;
            case CustomerMenuScreen.EXIT:
                customerMenu.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(customerMenu, "Invalid choice!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean updateCNICExpiry(String newExpiry) {

        for (NADRARecord record : MasterPersistence.getInstance().getNadraRecords()) {
            if (record.getCNIC().equals(myCustomer.getCNIC())) {
                record.setExpiryDate(newExpiry);
                return true;
            }
        }
        return false;
    }

    private JSONObject viewBills(ArrayList<org.example.commons.model.BillingRecord> billingRecords, String customerID) {
        JSONObject response = new JSONObject();
        JSONArray billingRecordsArray = new JSONArray();

        for (org.example.commons.model.BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(customerID)) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("customerID", br.getCustomerID());
                recordJson.put("billingMonth", br.getBillingMonth());
                recordJson.put("regularUnits", br.getCurrentMeterReadingRegular());
                recordJson.put("peakUnits", br.getCurrentMeterReadingPeak());
                recordJson.put("electricityCost", br.getCostOfElectricity());
                recordJson.put("salesTax", br.getSalesTaxAmount());
                recordJson.put("fixedCharges", br.getFixedCharges());
                recordJson.put("totalAmount", br.getTotalBillingAmount());
                recordJson.put("dueDate", br.getDueDate());
                recordJson.put("status", br.getBillPaidStatus());
                billingRecordsArray.put(recordJson);
            }
        }

        if (billingRecordsArray.length() > 0) {
            response.put("status", "success");
            response.put("returnCode", 200);
            response.put("resultBody", billingRecordsArray);
        } else {
            response.put("status", "error");
            response.put("returnCode", 404);
            response.put("resultBody", "No billing records found for customer: " + customerID);
        }

        return response;
    }


    public void estimateUpcomingBills(ArrayList<org.example.commons.model.TariffTax> tariffTaxes) {
        new BillEstimator(myCustomer);
    }

    public boolean updateCNICExp(String CNIC, int date, int month, int year) {
        if (CNIC.equals(myCustomer.getCNIC())) {
            for (NADRARecord record : MasterPersistence.getInstance().getNadraRecords()) {
                if (record.getCNIC().equals(CNIC)) {
                    record.setExpiryDate(String.format("%02d-%02d-%04d", date, month, year));
                    return true;
                }
            }
        }
        return false;
    }

}