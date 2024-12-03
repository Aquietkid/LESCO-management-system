package org.example.client.controller;

import org.example.commons.model.*;
import org.example.server.controller.Menu;
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
        ArrayList<BillingRecord> billingRecords = MasterPersistence.getInstance().getBillingRecords();

        int countPaidBills = 0;
        int countUnpaidBills = 0;
        float unpaidAmount = 0.0f;
        float paidAmount = 0.0f;

        for (org.example.commons.model.BillingRecord br : billingRecords) {
            if (br.getBillPaidStatus()) {
                countPaidBills++;
                paidAmount += br.getTotalBillingAmount();
            } else {
                countUnpaidBills++;
                unpaidAmount += br.getTotalBillingAmount();
            }
        }
        return "Bill report: " +
                "\nTotal unpaid bills: " + countUnpaidBills + "\nTotal amount unpaid: " + unpaidAmount +
                "\nTotal paid bills: " + countPaidBills + "\nTotal amount paid: " + paidAmount;
    }

    public String viewCNICCustomers() {
        JSONObject request = new JSONObject();
        request.put("operation", "read");
        request.put("subject", "CNICCustomers");
        request.put("parameters", new JSONObject());
        request.put("username", myEmployee.getUsername());
        request.put("password", myEmployee.getPassword());

        try (Socket socket = new Socket("localhost", 12345);
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
        String expiryDate = n.getExpiryDate().trim();

        int day = Integer.parseInt(expiryDate.substring(0, 2).trim());
        int month = Integer.parseInt(expiryDate.substring(3, 5).trim());
        int year = Integer.parseInt(expiryDate.substring(6, 10).trim());

        return LocalDate.of(year, month, day);
    }

    public int changePassword(String password, String newPassword, String confirmPassword) {
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


    public org.example.commons.model.Customer addCustomer(JFrame parent) {
        ArrayList<org.example.commons.model.Customer> customers = org.example.commons.model.MasterPersistence.getInstance().getCustomers();
        org.example.client.view.AddCustomerScreen addCustomerScreen = new org.example.client.view.AddCustomerScreen(parent);
        if (addCustomerScreen.isSubmitted()) {
            Customer customer = addCustomerScreen.getNewCustomer();
            if (!customers.contains(customer)) {
                customers.add(customer);
                org.example.commons.model.MasterPersistence.getInstance().setCustomersUpdated();
                return customer;
            } else {
                JOptionPane.showMessageDialog(parent, "Customer ID already exists!");
            }
        }
        return null;
    }

    public double calculateCostOfElectricity(double regularReading, double peakReading, TariffTax myTariffTax) {
        double regularRate = myTariffTax.getRegularUnitPrice();
        double peakRate = (myTariffTax.getPeakHourUnitPrice() != null) ? myTariffTax.getPeakHourUnitPrice() : 0.0;
        return (regularReading * regularRate) + (peakReading * peakRate);
    }

    public double calculateSalesTax(double costOfElectricity, TariffTax myTariffTax) {
        return costOfElectricity * myTariffTax.getTaxPercentage() / 100;
    }

    public TariffTax getTariffTax(Customer myCustomer) {
        return TariffTax.getTariffTax(MasterPersistence.getInstance().getTariffTaxes(), myCustomer);
    }

    public Customer getCustomerFromID(String customerID) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                return customer;
            }
        }
        return null;
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
