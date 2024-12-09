package org.example.client.controller;

import org.example.client.ServerParams;
import org.example.client.view.BillEstimator;
import org.example.client.view.BillViewer;
import org.example.client.view.CustomerMenuScreen;
import org.example.commons.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                viewBills();
                break;
            case CustomerMenuScreen.ESTIMATE_UPCOMING_BILL:
                estimateUpcomingBills(MasterPersistence.getInstance().getTariffTaxes());
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

    public boolean updateCNICExpiry(String newCNICExpiryDate) {
        JSONObject request = new JSONObject();
        request.put("operation", "update");
        request.put("subject", "CNICExpiry");

        JSONObject parameters = new JSONObject();
        parameters.put("customerId", myCustomer.getCustomerID());
        parameters.put("newCNICExpiryDate", newCNICExpiryDate);
        request.put("parameters", parameters);

        request.put("username", myCustomer.getUsername());
        request.put("password", myCustomer.getPassword());

        try (Socket socket = new Socket(ServerParams.ServerIP, ServerParams.ServerPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                responseBuilder.append(responseLine);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            if ("success".equalsIgnoreCase(response.getString("status")) && response.getInt("returnCode") == 200) {
                return true;
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error communicating with the server: " + e.getMessage());
        }
    }


    private void viewBills() {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "BillingRecords");

        JSONObject parameters = new JSONObject();
        parameters.put("customerId", myCustomer.getUsername());
        request.put("parameters", parameters);

        request.put("username", myCustomer.getUsername());
        request.put("password", myCustomer.getPassword());

        try (Socket socket = new Socket(ServerParams.ServerIP, ServerParams.ServerPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request.toString());

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                responseBuilder.append(responseLine);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            if ("success".equalsIgnoreCase(response.getString("status")) && response.getInt("returnCode") == 200) {
                JSONArray billingRecords = response.getJSONArray("resultBody");

                BillViewer billViewer = new BillViewer();
                for (int i = 0; i < billingRecords.length(); i++) {
                    JSONObject record = billingRecords.getJSONObject(i);
                    String[] rowData = {
                            record.getString("customerID"),
                            record.getString("billingMonth"),
                            record.getString("regularUnits"),
                            record.optString("peakUnits", "N/A"),
                            record.getString("electricityCost"),
                            record.getString("salesTax"),
                            record.getString("fixedCharges"),
                            record.getString("totalAmount"),
                            record.getString("dueDate"),
                            record.getString("status")
                    };
                    billViewer.addRow(rowData);
                }

            } else {
                throw new RuntimeException("Error fetching billing records: " + response.getString("resultBody"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error communicating with the server: " + e.getMessage());
        }
    }

    public void estimateUpcomingBills(ArrayList<TariffTax> tariffTaxes) {
//        JSONObject request = new JSONObject();
//        request.put("operation", "read");
//        request.put("subject", "estimateUpcomingBill");
//        request.put("username", myCustomer.getUsername());
//        request.put("password", myCustomer.getPassword());
//
//        JSONObject parameters = new JSONObject();
//        parameters.put("customerID", myCustomer.getCustomerID());
//        parameters.put("isCommercial", myCustomer.getIsCommercial());
//        parameters.put("isThreePhase", myCustomer.getThreePhase());
//
//
//        TariffTax tariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);
//        if (tariffTax != null) {
//            parameters.put("regularUnitPrice", tariffTax.getRegularUnitPrice());
//            parameters.put("peakHourUnitPrice", tariffTax.getPeakHourUnitPrice());
//            parameters.put("taxPercentage", tariffTax.getTaxPercentage());
//        } else {
//            JOptionPane.showMessageDialog(null, "Tariff details not found!", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        request.put("parameters", parameters);
//
//        try (Socket socket = new Socket(ServerParams.ServerIP, ServerParams.ServerPort);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//            out.println(request.toString());
//
//            StringBuilder responseBuilder = new StringBuilder();
//            String responseLine;
//            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
//                responseBuilder.append(responseLine);
//            }
//
//            JSONObject response = new JSONObject(responseBuilder.toString());
//            if ("success".equalsIgnoreCase(response.getString("status"))) {
//                double estimatedBill = response.getDouble("resultBody");
//                JOptionPane.showMessageDialog(null, "Estimated Upcoming Bill: " + estimatedBill,
//                        "Estimate Successful", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(null, "Error: " + response.getString("resultBody"),
//                        "Error", JOptionPane.ERROR_MESSAGE);
//            }
//
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, "Error communicating with server: " + e.getMessage(),
//                    "Communication Error", JOptionPane.ERROR_MESSAGE);
//        }

        new BillEstimator(myCustomer);
    }

}