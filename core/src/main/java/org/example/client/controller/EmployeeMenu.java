package org.example.client.controller;

import org.example.client.view.AddCustomerScreen;
import org.example.commons.model.*;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeMenu extends Menu {

    private final org.example.commons.model.User myEmployee;

    public static final int PASSWORD_MISMATCH = -2;
    public static final int CONFIRM_PASSWORD_MISMATCH = -3;
    public static final int OLD_NEW_PASSWORDS_SAME = -4;
    public static final int PASSWORD_TOO_SHORT = -5;

    public EmployeeMenu(User employee) {
        this.myEmployee = employee;
    }

    public String viewBillReports() {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "billingReports");
        request.put("parameters", new JSONObject()); // No specific parameters needed
        request.put("username", myEmployee.getUsername());
        request.put("password", myEmployee.getPassword());

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
                return response.getString("resultBody");
            } else {
                return "Error: " + response.getString("resultBody");
            }

        } catch (IOException e) {
            return "Error: Unable to communicate with the server. " + e.getMessage();
        }
    }
    public String viewCNICCustomers() {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "CNICCustomers");
        request.put("parameters", new JSONObject());
        request.put("username", myEmployee.getUsername());
        request.put("password", myEmployee.getPassword());

        try (Socket socket = new Socket("192.168.47.232", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the request with a newline
            System.out.println(request);
            out.println(request.toString());
            out.println("END");

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                responseBuilder.append(responseLine);
            }
            JSONObject response = new JSONObject(responseBuilder.toString());

            if ("success".equalsIgnoreCase(response.getString("status")) && response.getInt("returnCode") == 200) {
                return response.getString("resultBody");
            } else {
                return "Error (" + response.getInt("returnCode") + "): " + response.getString("resultBody");
            }

        } catch (IOException e) {
            return "Error: Unable to communicate with the server. " + e.getMessage();
        }
    }



    public LocalDate getExpiryDate(org.example.commons.model.NADRARecord n) {
        // TODO: connect to server for logic (read)
        String expiryDate = n.getExpiryDate().trim();

        int day = Integer.parseInt(expiryDate.substring(0, 2).trim());
        int month = Integer.parseInt(expiryDate.substring(3, 5).trim());
        int year = Integer.parseInt(expiryDate.substring(6, 10).trim());

        return LocalDate.of(year, month, day);
    }

    public int changePassword(String password, String newPassword, String confirmPassword) {
        // TODO: connect to server for logic (read, write, update, delete, each and everything inside server, nothing inside client)
        if (!password.equals(myEmployee.getPassword())) {
            return org.example.server.controller.EmployeeMenu.PASSWORD_MISMATCH;
        } else if (newPassword.equals(myEmployee.getPassword())) {
            return org.example.server.controller.EmployeeMenu.OLD_NEW_PASSWORDS_SAME;
        } else if (newPassword.length() < 6) {
            return org.example.server.controller.EmployeeMenu.PASSWORD_TOO_SHORT;
        } else if (!confirmPassword.equals(newPassword)) {
            return org.example.server.controller.EmployeeMenu.CONFIRM_PASSWORD_MISMATCH;
        }
        ArrayList<org.example.commons.model.Employee> employees = org.example.commons.model.MasterPersistence.getInstance().getEmployees();
        for (org.example.commons.model.Employee employee : employees) {
            if (employee.getUsername().equals(myEmployee.getUsername())) {
                ((org.example.commons.model.Employee) myEmployee).setPassword(newPassword);
                employees.remove(employee);
                employees.add((org.example.commons.model.Employee) myEmployee);
                System.out.println(myEmployee.toFileString());
                org.example.commons.model.MasterPersistence.getInstance().setEmployeesUpdated();
                break;
            }
        }
        for (org.example.commons.model.Employee employee : employees) {
            System.out.println(employee.toFileString());
        }
        return 0;
    }


    public Customer addCustomer(JFrame parent) {
        org.example.client.view.AddCustomerScreen addCustomerScreen = new org.example.client.view.AddCustomerScreen(parent);
        if (addCustomerScreen.isSubmitted()) {
            Customer customer = addCustomerScreen.getNewCustomer();

            JSONObject request = new JSONObject();
            request.put("operation", "write");
            request.put("subject", "customer");
            request.put("username", myEmployee.getUsername());
            request.put("password", myEmployee.getPassword());
            request.put("parameters", new JSONObject()
                    .put("customerID", customer.getCustomerID())
                    .put("cnic", customer.getCNIC())
                    .put("name", customer.getCustomerName())
                    .put("address", customer.getAddress())
                    .put("phone", customer.getPhone())
                    .put("isCommercial", customer.getIsCommercial())
                    .put("isThreePhase", customer.getThreePhase())
                    .put("connectionDate", customer.getConnectionDate())
            );

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
                    return customer;
                } else {
                    JOptionPane.showMessageDialog(parent, "Error: " + response.getString("resultBody"));
                    return null;
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error: Unable to communicate with the server. " + e.getMessage());
                return null;
            }
        }
        return null;
    }
    public double calculateCostOfElectricity(double regularReading, double peakReading, TariffTax myTariffTax) {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "electricityCost");
        request.put("username", myEmployee.getUsername());
        request.put("password", myEmployee.getPassword());
        request.put("parameters", new JSONObject()
                .put("regularReading", regularReading)
                .put("peakReading", peakReading)
                .put("regularUnitPrice", myTariffTax.getRegularUnitPrice())
                .put("peakHourUnitPrice", myTariffTax.getPeakHourUnitPrice())
        );

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
                return response.getDouble("resultBody");
            } else {
                throw new IOException("Error: " + response.getString("resultBody"));
            }

        } catch (IOException e) {
            System.err.println("Error: Unable to communicate with the server. " + e.getMessage());
            return -1;
        }
    }

    public double calculateSalesTax(double costOfElectricity, TariffTax myTariffTax) {
        // TODO: fetch myTariffTax from server and perform calculation in client
        return costOfElectricity * myTariffTax.getTaxPercentage() / 100;
    }

    public TariffTax getTariffTax(Customer myCustomer) {
        // TODO: connect to server for logic (read, write, update, delete, each and everything inside server, nothing inside client)
        return TariffTax.getTariffTax(MasterPersistence.getInstance().getTariffTaxes(), myCustomer);
    }

    public Customer getCustomerFromID(String customerID) {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "customer");
        request.put("username", myEmployee.getUsername());
        request.put("password", myEmployee.getPassword());
        request.put("parameters", new JSONObject().put("customerID", customerID));

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
                JSONObject customerJson = response.getJSONObject("resultBody");
                return new Customer(
                        customerJson.getString("customerID"),
                        customerJson.getString("cnic"),
                        customerJson.getString("name"),
                        customerJson.getString("address"),
                        customerJson.getString("phone"),
                        customerJson.getBoolean("isCommercial"),
                        customerJson.getBoolean("isThreePhase"),
                        customerJson.getString("connectionDate"),
                        customerJson.getInt("regularUnitsConsumed"),
                        customerJson.getInt("peakUnitsConsumed")
                );
            } else {
                System.err.println("Error: " + response.getString("resultBody"));
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error: Unable to communicate with the server. " + e.getMessage());
            return null;
        }
    }

    public int getMaxCustomerID() {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        int maxID = Integer.parseInt(customers.getFirst().getCustomerID());
        for (Customer customer : customers) {
            if (Integer.parseInt(customer.getCustomerID()) > maxID) {
                maxID = Integer.parseInt(customer.getCustomerID());
            }
        }
        return maxID;
    }

    public int getMinCustomerID() {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        int minID = Integer.parseInt(customers.getFirst().getCustomerID());
        for (Customer customer : customers) {
            if (Integer.parseInt(customer.getCustomerID()) < minID) {
                minID = Integer.parseInt(customer.getCustomerID());
            }
        }
        return minID;
    }

}
