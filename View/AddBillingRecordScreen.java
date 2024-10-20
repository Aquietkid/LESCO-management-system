package View;

import Model.BillingRecord;
import Model.Customer;
import Model.MasterPersistence;
import Model.TariffTax;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddBillingRecordScreen extends JFrame {
    private JSpinner spnCustomerID;
    private JSpinner spnRegularReading;
    private JSpinner spnPeakReading;
    private JSpinner spnBillingMonth;
    private JSpinner spnReadingEntryDate;
    private JSpinner spnDueDate;
    private boolean isSubmitted = false;
    private BillingRecord newBillingRecord;

    public AddBillingRecordScreen() {
        init();
    }

    private void init() {
        setTitle("Add New Billing Record");
        setLayout(new GridLayout(10, 2, 10, 10));
        setSize(500, 400);
        setLocationRelativeTo(null);

        spnCustomerID = new JSpinner(new SpinnerNumberModel(1000, 1000, 9999, 1)); // Assuming 4-digit customer IDs

        SpinnerDateModel billingMonthModel = new SpinnerDateModel();
        spnBillingMonth = new JSpinner(billingMonthModel);
        JSpinner.DateEditor monthEditor = new JSpinner.DateEditor(spnBillingMonth, "MM/yyyy");
        spnBillingMonth.setEditor(monthEditor);

        spnRegularReading = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 10000.0f, 0.1f));
        spnPeakReading = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 10000.0f, 0.1f));

        spnReadingEntryDate = new JSpinner(new SpinnerDateModel());
        spnReadingEntryDate.setValue(Calendar.getInstance().getTime()); // Default to today
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnReadingEntryDate, "yyyy-MM-dd");
        spnReadingEntryDate.setEditor(dateEditor);
        spnReadingEntryDate.setEnabled(false);

        spnDueDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dueDateEditor = new JSpinner.DateEditor(spnDueDate, "yyyy-MM-dd");
        spnDueDate.setEditor(dueDateEditor);

        JLabel lblCostOfElectricity = new JLabel("Auto-calculated");
        JLabel lblSalesTax = new JLabel("Auto-calculated");
        JLabel lblFixedCharges = new JLabel("Auto-calculated");
        JLabel lblTotalBillingAmount = new JLabel("Auto-calculated");

        add(new JLabel("Customer ID:"));
        add(spnCustomerID);
        add(new JLabel("Billing Month (MM/YYYY):"));
        add(spnBillingMonth);
        add(new JLabel("Regular Meter Reading:"));
        add(spnRegularReading);
        add(new JLabel("Peak Meter Reading:"));
        add(spnPeakReading);
        add(new JLabel("Reading Entry Date:"));
        add(spnReadingEntryDate);
        add(new JLabel("Due Date:"));
        add(spnDueDate);
        add(new JLabel("Cost of Electricity:"));
        add(lblCostOfElectricity);
        add(new JLabel("Sales Tax:"));
        add(lblSalesTax);
        add(new JLabel("Fixed Charges:"));
        add(lblFixedCharges);
        add(new JLabel("Total Billing Amount:"));
        add(lblTotalBillingAmount);

        JButton btnAdd = new JButton("Add");
        JButton btnCancel = new JButton("Cancel");
        add(btnAdd);
        add(btnCancel);

        btnAdd.addActionListener(e -> onSubmit());

        btnCancel.addActionListener(e -> dispose());
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setVisible(true);
    }

    private void onSubmit() {
        try {
            String customerID = spnCustomerID.getValue().toString();
            Customer myCustomer = getMyCustomer(customerID);

            SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");
            String billingMonth = monthFormat.format(spnBillingMonth.getValue());

            float regularReading = ((Number) spnRegularReading.getValue()).floatValue();
            float peakReading = ((Number) spnPeakReading.getValue()).floatValue();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String readingEntryDate = dateFormat.format(spnReadingEntryDate.getValue());

            String dueDate = dateFormat.format(spnDueDate.getValue());

            TariffTax myTariffTax = getTariffTax(myCustomer);

            float costOfElectricity = (float) calculateCostOfElectricity(regularReading, peakReading, myTariffTax);
            float salesTaxAmount = (float) calculateSalesTax(costOfElectricity, myTariffTax);
            float fixedCharges = (float) myTariffTax.getFixedCharges();
            float totalBillingAmount = costOfElectricity + salesTaxAmount + fixedCharges;

            newBillingRecord = new BillingRecord(customerID, billingMonth, regularReading, peakReading, readingEntryDate, costOfElectricity, salesTaxAmount, fixedCharges, totalBillingAmount, dueDate);

            JOptionPane.showMessageDialog(this, "Billing Record added!");

            isSubmitted = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateCostOfElectricity(double regularReading, double peakReading, TariffTax myTariffTax) {
        double regularRate = myTariffTax.getRegularUnitPrice();
        double peakRate = myTariffTax.getPeakHourUnitPrice();
        return (regularReading * regularRate) + (peakReading * peakRate);
    }

    private double calculateSalesTax(double costOfElectricity, TariffTax myTariffTax) {
        return costOfElectricity * myTariffTax.getTaxPercentage();
    }

    private TariffTax getTariffTax(Customer myCustomer) {
        return TariffTax.getTariffTax(MasterPersistence.getInstance().getTariffTaxes(), myCustomer);
    }

    private Customer getMyCustomer(String customerID) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                return customer;
            }
        }
        return null;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public BillingRecord getNewBillingRecord() {
        return newBillingRecord;
    }
}
