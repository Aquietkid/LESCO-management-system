package org.example.controller;

import org.example.model.*;
import org.example.view.AddCustomerScreen;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeMenu extends Menu {

    private final User myEmployee;

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

        for (BillingRecord br : billingRecords) {
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
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        ArrayList<NADRARecord> nadraRecords = MasterPersistence.getInstance().getNadraRecords();
        StringBuilder message = new StringBuilder();
        boolean flag = false;
        if (customers != null) {
            LocalDate now = LocalDate.now();
            for (Customer c : customers) {
                for (NADRARecord n : nadraRecords) {
                    if (n.getCNIC().equals(c.getCNIC())) {
                        //Check if date is within the next 30 days
                        LocalDate expiry = getExpiryDate(n);
                        if (!expiry.isBefore(now) && !expiry.isAfter(now.plusDays(30))) {
                            message.append(c).append('\n');
                            flag = true;
                        }
                    }
                }
            }
        }
        if(!flag) message.append("There are no customers with CNICs expiring soon.");
        return message.toString();
    }

    private LocalDate getExpiryDate(NADRARecord n) {
        String expiryDate = n.getExpiryDate().trim();

        int day = Integer.parseInt(expiryDate.substring(0, 2).trim());
        int month = Integer.parseInt(expiryDate.substring(3, 5).trim());
        int year = Integer.parseInt(expiryDate.substring(6, 10).trim());

        return LocalDate.of(year, month, day);
    }

    public int changePassword(String password, String newPassword, String confirmPassword) {
        if (!password.equals(myEmployee.getPassword())) {
            return EmployeeMenu.PASSWORD_MISMATCH;
        } else if (newPassword.equals(myEmployee.getPassword())) {
            return EmployeeMenu.OLD_NEW_PASSWORDS_SAME;
        } else if (newPassword.length() < 6) {
            return EmployeeMenu.PASSWORD_TOO_SHORT;
        } else if (!confirmPassword.equals(newPassword)) {
            return EmployeeMenu.CONFIRM_PASSWORD_MISMATCH;
        }
        ArrayList<Employee> employees = MasterPersistence.getInstance().getEmployees();
        for (Employee employee : employees) {
            if (employee.getUsername().equals(myEmployee.getUsername())) {
                ((Employee) myEmployee).setPassword(newPassword);
                employees.remove(employee);
                employees.add((Employee) myEmployee);
                System.out.println(myEmployee.toFileString());
                MasterPersistence.getInstance().setEmployeesUpdated();
                break;
            }
        }
        for (Employee employee : employees) {
            System.out.println(employee.toFileString());
        }
        return 0;
    }


    public Customer addCustomer(JFrame parent) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        AddCustomerScreen addCustomerScreen = new AddCustomerScreen(parent);
        if (addCustomerScreen.isSubmitted()) {
            Customer customer = addCustomerScreen.getNewCustomer();
            if (!customers.contains(customer)) {
                customers.add(customer);
                MasterPersistence.getInstance().setCustomersUpdated();
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
