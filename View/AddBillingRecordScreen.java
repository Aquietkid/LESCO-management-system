package View;

import Controller.EmployeeMenu;
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
    private final EmployeeMenu employeeMenu;
    private JSpinner spnCustomerID;
    private JSpinner spnRegularReading;
    private JSpinner spnPeakReading;
    private JSpinner spnBillingMonth;
    private JSpinner spnReadingEntryDate;
    private JSpinner spnDueDate;
    private boolean isSubmitted = false;
    private BillingRecord newBillingRecord;

    private JLabel lblCostOfElectricity;
    private JLabel lblSalesTax;
    private JLabel lblFixedCharges;
    private JLabel lblTotalBillingAmount;

    private Customer myCustomer;

    public AddBillingRecordScreen(EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("Add New Billing Record");
        setSize(500, 400);
        setLocationRelativeTo(null);

        spnCustomerID = new JSpinner(new SpinnerNumberModel(employeeMenu.getMinCustomerID(), employeeMenu.getMinCustomerID(), employeeMenu.getMaxCustomerID(), 1)); // Assuming 4-digit customer IDs
        myCustomer = employeeMenu.getCustomerFromID(spnCustomerID.getValue().toString());

        SpinnerDateModel billingMonthModel = new SpinnerDateModel();
        spnBillingMonth = new JSpinner(billingMonthModel);
        JSpinner.DateEditor monthEditor = new JSpinner.DateEditor(spnBillingMonth, "MM/yyyy");
        spnBillingMonth.setEditor(monthEditor);

        spnCustomerID.addChangeListener(e -> {
            myCustomer = employeeMenu.getCustomerFromID(spnCustomerID.getValue().toString());
            if (myCustomer != null) {
                spnRegularReading.setModel(new SpinnerNumberModel(myCustomer.getRegularUnitsConsumed(), myCustomer.getRegularUnitsConsumed(), 999999.9, 0.1));
                if (!myCustomer.getThreePhase()) {
                    spnPeakReading.setEnabled(false);
                } else {
                    spnPeakReading.setEnabled(true);
                    spnPeakReading.setModel(new SpinnerNumberModel(myCustomer.getPeakUnitsConsumed(), myCustomer.getPeakUnitsConsumed(), 999999.9, 0.1));
                }
            } else {
                spnRegularReading.setModel(new SpinnerNumberModel(0.0, 0.0, 999999.9, 0.1));
                spnPeakReading.setModel(new SpinnerNumberModel(0.0, 0.0, 999999.9, 0.1));
            }
            updateCostLabels();
        });

        spnRegularReading = new JSpinner(new SpinnerNumberModel(myCustomer.getRegularUnitsConsumed(), myCustomer.getRegularUnitsConsumed(), 999999.9, 0.1));
        spnPeakReading = new JSpinner(new SpinnerNumberModel(myCustomer.getPeakUnitsConsumed(), myCustomer.getPeakUnitsConsumed(), 999999.9, 0.1));
        if (!myCustomer.getThreePhase()) {
            spnPeakReading.setEnabled(false);
        }

        spnRegularReading.addChangeListener(e -> updateCostLabels());
        spnPeakReading.addChangeListener(e -> updateCostLabels());

        spnReadingEntryDate = new JSpinner(new SpinnerDateModel());
        spnReadingEntryDate.setValue(Calendar.getInstance().getTime());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnReadingEntryDate, "dd-MM-yyyy");
        spnReadingEntryDate.setEditor(dateEditor);
        spnReadingEntryDate.setEnabled(false);

        spnDueDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dueDateEditor = new JSpinner.DateEditor(spnDueDate, "dd-MM-yyyy");
        spnDueDate.setEditor(dueDateEditor);

        lblCostOfElectricity = new JLabel();
        lblSalesTax = new JLabel();
        lblFixedCharges = new JLabel();
        lblTotalBillingAmount = new JLabel();

        updateCostLabels();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Customer ID:"));
        panel.add(spnCustomerID);
        panel.add(new JLabel("Billing Month (MM-YYYY):"));
        panel.add(spnBillingMonth);
        panel.add(new JLabel("Regular Meter Reading:"));
        panel.add(spnRegularReading);
        panel.add(new JLabel("Peak Meter Reading:"));
        panel.add(spnPeakReading);
        panel.add(new JLabel("Reading Entry Date:"));
        panel.add(spnReadingEntryDate);
        panel.add(new JLabel("Due Date:"));
        panel.add(spnDueDate);
        panel.add(new JLabel("Cost of Electricity:"));
        panel.add(lblCostOfElectricity);
        panel.add(new JLabel("Sales Tax:"));
        panel.add(lblSalesTax);
        panel.add(new JLabel("Fixed Charges:"));
        panel.add(lblFixedCharges);
        panel.add(new JLabel("Total Billing Amount:"));
        panel.add(lblTotalBillingAmount);

        JButton btnAdd = new JButton("Add");
        JButton btnCancel = new JButton("Cancel");
        panel.add(btnAdd);
        panel.add(btnCancel);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel);

        btnAdd.addActionListener(e -> onSubmit(myCustomer));

        btnCancel.addActionListener(e -> dispose());
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setVisible(true);
    }

    private void updateCostLabels() {
        if (myCustomer.getThreePhase())
            lblCostOfElectricity.setText(String.valueOf(employeeMenu.calculateCostOfElectricity(((double) spnRegularReading.getValue() - myCustomer.getRegularUnitsConsumed()), ((double) spnPeakReading.getValue() - myCustomer.getPeakUnitsConsumed()), employeeMenu.getTariffTax(myCustomer))));
        else
            lblCostOfElectricity.setText(String.valueOf(employeeMenu.calculateCostOfElectricity(((double) spnRegularReading.getValue() - myCustomer.getRegularUnitsConsumed()), 0.0, employeeMenu.getTariffTax(myCustomer))));
        lblSalesTax.setText(String.valueOf(employeeMenu.calculateSalesTax(Double.parseDouble(lblCostOfElectricity.getText()), employeeMenu.getTariffTax(myCustomer))));
        lblFixedCharges.setText(String.valueOf(employeeMenu.getTariffTax(myCustomer).getFixedCharges()));
        lblTotalBillingAmount.setText(String.valueOf(
                Double.parseDouble(lblCostOfElectricity.getText()) +
                        Double.parseDouble(lblFixedCharges.getText()) +
                        Double.parseDouble(lblSalesTax.getText())
        ));
    }

    private void onSubmit(Customer myCustomer) {
        try {
            if (myCustomer == null) {
                JOptionPane.showMessageDialog(this, "The customer ID does not exist!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat monthFormat = new SimpleDateFormat("MM-yyyy");
            String billingMonth = monthFormat.format(spnBillingMonth.getValue());

            float regularReading = ((Number) spnRegularReading.getValue()).floatValue();
            float peakReading = ((Number) spnPeakReading.getValue()).floatValue();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String readingEntryDate = dateFormat.format(spnReadingEntryDate.getValue());

            String dueDate = dateFormat.format(spnDueDate.getValue());

            TariffTax myTariffTax = employeeMenu.getTariffTax(myCustomer);

            float costOfElectricity = (float) employeeMenu.calculateCostOfElectricity(regularReading, peakReading, myTariffTax);
            float salesTaxAmount = (float) employeeMenu.calculateSalesTax(costOfElectricity, myTariffTax);
            float fixedCharges = (float) myTariffTax.getFixedCharges();
            float totalBillingAmount = costOfElectricity + salesTaxAmount + fixedCharges;

            newBillingRecord = new BillingRecord(myCustomer.getCustomerID(), billingMonth, regularReading, peakReading, readingEntryDate, costOfElectricity, salesTaxAmount, fixedCharges, totalBillingAmount, dueDate);

            ArrayList<BillingRecord> billingRecords = MasterPersistence.getInstance().getBillingRecords();
            for (BillingRecord billingRecord : billingRecords) {
                if (billingRecord.getBillingMonth().equals(billingMonth) && billingRecord.getCustomerID().equals(myCustomer.getCustomerID())) {
                    JOptionPane.showMessageDialog(this, "The bill for this month is already added!", "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            billingRecords.add(newBillingRecord);
            MasterPersistence.getInstance().setBillingRecordsUpdated();
            JOptionPane.showMessageDialog(this, "Billing Record added!");

            isSubmitted = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public BillingRecord getNewBillingRecord() {
        return newBillingRecord;
    }
}
