package org.example.client.controller;

import org.example.client.view.BillEstimator;
import org.example.client.view.BillViewer;
import org.example.client.view.CNICUpdateScreen;
import org.example.client.view.CustomerMenuScreen;
import org.example.commons.model.*;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
        new CustomerMenuScreen(new CustomerMenu(myCustomer));
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
        // TODO: connect to server for logic (read, write, update, delete, each and everything inside server, nothing inside client)
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
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "estimateUpcomingBill");
        request.put("username", myCustomer.getUsername());
        request.put("password", myCustomer.getPassword());

        JSONObject parameters = new JSONObject();
        parameters.put("customerID", myCustomer.getCustomerID());
        parameters.put("isCommercial", myCustomer.getIsCommercial());
        parameters.put("isThreePhase", myCustomer.getThreePhase());


        TariffTax tariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);
        if (tariffTax != null) {
            parameters.put("regularUnitPrice", tariffTax.getRegularUnitPrice());
            parameters.put("peakHourUnitPrice", tariffTax.getPeakHourUnitPrice());
            parameters.put("taxPercentage", tariffTax.getTaxPercentage());
        } else {
            JOptionPane.showMessageDialog(null, "Tariff details not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        request.put("parameters", parameters);

        try (Socket socket = new Socket("192.168.47.232", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request.toString());
            out.println("END");

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                responseBuilder.append(responseLine);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            if ("success".equalsIgnoreCase(response.getString("status"))) {
                // display the estimated bill
                double estimatedBill = response.getDouble("resultBody");
                JOptionPane.showMessageDialog(null, "Estimated Upcoming Bill: " + estimatedBill,
                        "Estimate Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error: " + response.getString("resultBody"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error communicating with server: " + e.getMessage(),
                    "Communication Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}