package org.example.server.controller;

import org.example.client.view.AddCustomerScreen;
import org.example.commons.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

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
        return "Bill report: " + "\nTotal unpaid bills: " + countUnpaidBills + "\nTotal amount unpaid: " + unpaidAmount + "\nTotal paid bills: " + countPaidBills + "\nTotal amount paid: " + paidAmount;
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
        if (!flag) message.append("There are no customers with CNICs expiring soon.");
        return message.toString();
    }

    public LocalDate getExpiryDate(NADRARecord n) {
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


    public void addCustomer(Customer newCustomer) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        customers.add(newCustomer);
        MasterPersistence.getInstance().setCustomersUpdated();
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

    public static Customer getCustomerFromID(String customerID) {
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

    public BillingRecord addBillingRecord(String customerID, Date billingMonth, float regularReading, float peakReading, Date readingEntryDate, Date dueDate) {
        Customer myCustomer = getCustomerFromID(customerID);
        if (myCustomer == null) {
            throw new IllegalArgumentException("The customer ID does not exist!");
        }

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM-yyyy");
        String billingMonthStr = monthFormat.format(billingMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String readingEntryDateStr = dateFormat.format(readingEntryDate);
        String dueDateStr = dateFormat.format(dueDate);

        TariffTax myTariffTax = getTariffTax(myCustomer);

        float costOfElectricity = (float) calculateCostOfElectricity(regularReading - myCustomer.getRegularUnitsConsumed(), myCustomer.getThreePhase() ? peakReading - myCustomer.getPeakUnitsConsumed() : 0.0, myTariffTax);

        float salesTaxAmount = (float) calculateSalesTax(costOfElectricity, myTariffTax);
        float fixedCharges = (float) myTariffTax.getFixedCharges();
        float totalBillingAmount = costOfElectricity + salesTaxAmount + fixedCharges;

        BillingRecord newBillingRecord = new BillingRecord(customerID, billingMonthStr, regularReading, peakReading, readingEntryDateStr, costOfElectricity, salesTaxAmount, fixedCharges, totalBillingAmount, dueDateStr);

        ArrayList<BillingRecord> billingRecords = MasterPersistence.getInstance().getBillingRecords();
        for (BillingRecord billingRecord : billingRecords) {
            if (billingRecord.getBillingMonth().equals(billingMonthStr) && billingRecord.getCustomerID().equals(customerID)) {
                throw new IllegalArgumentException("The bill for this month is already added!");
            }
        }

        billingRecords.add(newBillingRecord);
        MasterPersistence.getInstance().setBillingRecordsUpdated();

        return newBillingRecord;
    }

    public void removeBill(JTable table, DefaultTableModel model, ArrayList<BillingRecord> billingRecords, JFrame parentFrame) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            BillingRecord selectedRecord = billingRecords.get(selectedRow);

            if (isMostRecentBill(selectedRecord, billingRecords)) {
                if (JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this billing record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    billingRecords.remove(selectedRow);  // Remove from the main list
                    model.removeRow(selectedRow);  // Remove from the table
                    MasterPersistence.getInstance().setBillingRecordsUpdated();
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Only the most recent month's bill can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isMostRecentBill(BillingRecord currentBill, ArrayList<BillingRecord> allBills) {
        String currentCustomerId = currentBill.getCustomerID();
        String currentBillingMonth = currentBill.getBillingMonth();

        for (BillingRecord record : allBills) {
            if (record.getCustomerID().equals(currentCustomerId)) {
                if (record.getBillingMonth().compareTo(currentBillingMonth) > 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
