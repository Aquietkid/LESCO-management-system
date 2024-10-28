package View;

import Model.Customer;
import Model.MasterPersistence;
import Model.TariffTax;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BillEstimator extends JFrame {

    private JSpinner spinRegularUnits;
    private JSpinner spinPeakUnits;

    private JLabel lblElectricityCost;
    private JLabel lblSalesTax;
    private JLabel lblFixedCharges;
    private JLabel lblTotal;

    private final double minRegularUnits;
    private final double minPeakUnits;
    private final boolean isPeak;
    private final TariffTax myTariffTax;

    private final Customer myCustomer;

//    public BillEstimator(double minReg, double minPeak, boolean isPeak, TariffTax myTariffTax) {
//        this.minRegularUnits = minReg;
//        this.minPeakUnits = minPeak;
//        this.isPeak = isPeak;
//        this.myTariffTax = myTariffTax;
//        init();
//    }

    public BillEstimator(Customer myCustomer) {
        this.myCustomer = myCustomer;
        this.minRegularUnits = myCustomer.getRegularUnitsConsumed();
        this.minPeakUnits = myCustomer.getPeakUnitsConsumed();

        ArrayList<TariffTax> tariffTaxes = MasterPersistence.getInstance().getTariffTaxes();
        this.myTariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);

        this.isPeak = myTariffTax.getPeakHourUnitPrice() != null;
        init();
    }

    private void init() {
        initRegularUnitsPanel();
        if (isPeak) {
            initPeakUnitsPanel();
        }
        initLabels();
        setLayout(new GridLayout(3, 1, 20, 20));
        setBounds(560, 190, 800, 600);
        setVisible(true);
    }

    private void initRegularUnitsPanel() {
        spinRegularUnits = new JSpinner();
        JLabel lblRegularUnits = new JLabel("Regular Units Reading");
        JPanel regularUnitsPanel = new JPanel();
        regularUnitsPanel.setBorder(BorderFactory.createTitledBorder("Regular Units"));
        regularUnitsPanel.setLayout(new BorderLayout(20, 20));
        regularUnitsPanel.add(lblRegularUnits, BorderLayout.WEST);
        regularUnitsPanel.add(spinRegularUnits, BorderLayout.CENTER);
        spinRegularUnits.setModel(new SpinnerNumberModel(minRegularUnits, minRegularUnits, Float.MAX_VALUE, 0.01));
        spinRegularUnits.addChangeListener(e -> {
            updateTotal();
        });
        add(regularUnitsPanel);
    }

    private void initPeakUnitsPanel() {
        JPanel peakUnitsPanel = new JPanel();
        peakUnitsPanel.setLayout(new BorderLayout(20, 20));
        peakUnitsPanel.setBorder(BorderFactory.createTitledBorder("Peak Hour Units"));
        spinPeakUnits = new JSpinner();
        JLabel lblPeakUnits = new JLabel("Peak Units Reading");
        peakUnitsPanel.add(lblPeakUnits, BorderLayout.WEST);
        peakUnitsPanel.add(spinPeakUnits, BorderLayout.CENTER);
        spinPeakUnits.setModel(new SpinnerNumberModel(minPeakUnits, minPeakUnits, Float.MAX_VALUE, 0.01));
        spinPeakUnits.addChangeListener(e -> {
            updateTotal();
        });
        add(peakUnitsPanel);
    }

    private void initLabels() {
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(4, 1, 20, 20));
        labelsPanel.setBorder(BorderFactory.createTitledBorder("Cost Breakdown"));
        lblTotal = new JLabel("Total Reading: ");
        labelsPanel.add(lblTotal);
        lblElectricityCost = new JLabel("Electricity Cost: ");
        labelsPanel.add(lblElectricityCost);
        lblSalesTax = new JLabel("Sales Tax: ");
        labelsPanel.add(lblSalesTax);
        lblFixedCharges = new JLabel("Fixed Charges: ");
        labelsPanel.add(lblFixedCharges);
        add(labelsPanel);
    }

    private void updateTotal() {
        double currentRegularUnits = (double) spinRegularUnits.getValue() - myCustomer.getRegularUnitsConsumed();
        Double currentPeakUnits = (Double) spinPeakUnits.getValue() - myCustomer.getPeakUnitsConsumed();

        Double costOfElectricity = (Double) (currentRegularUnits * myTariffTax.getRegularUnitPrice()) + (Double) ((myTariffTax.getPeakHourUnitPrice() != null) ? (currentPeakUnits * myTariffTax.getPeakHourUnitPrice()) : 0.0);
        Double salesTaxAmount = (Double) (costOfElectricity * (myTariffTax.getTaxPercentage() / 100));
        Double fixedCharges = (Double) myTariffTax.getFixedCharges();
        Double total = costOfElectricity + salesTaxAmount + fixedCharges;

        lblElectricityCost.setText("Electricity Cost: Rs. " + String.format("%.2f",costOfElectricity));
        lblSalesTax.setText("Sales Tax: Rs. " + String.format("%.2f", salesTaxAmount));
        lblFixedCharges.setText("Fixed Charges: Rs. " + String.format("%.2f", fixedCharges));
        lblTotal.setText("Total Reading: Rs. " + String.format("%.2f", total));
    }

}
