package View;

import Model.BillingRecord;
import Model.MasterPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class BillsViewEmployee extends JFrame {

    JTable table;
    DefaultTableModel model;
    JButton btnAdd;
    JButton btnDelete;
    JButton btnBillReports;
    JButton btnBack;
    JPanel panelButtons;
    JPanel panelTable;
    JScrollPane scrollPane;
    ArrayList<BillingRecord> billingRecords;

    Controller.EmployeeMenu employeeMenu;

    public BillsViewEmployee(Controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {

        setTitle("Billing Records Management");
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {
                "Customer ID",
                "Billing Month",
                "Regular Reading",
                "Peak Reading",
                "Reading Entry Date",
                "Cost of Electricity",
                "Sales Tax Amount",
                "Fixed Charges",
                "Total Billing Amount",
                "Due Date",
                "Bill Paid",
                "Payment Date"
        };

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnBillReports = new JButton("Bill Reports");
        btnBack = new JButton("Back");

        panelButtons = new JPanel();
        panelButtons.add(btnAdd);
        panelButtons.add(btnDelete);
        panelButtons.add(btnBillReports);
        panelButtons.add(btnBack);

        scrollPane = new JScrollPane(table);
        panelTable = new JPanel(new BorderLayout(10, 10));
        panelTable.add(scrollPane, BorderLayout.CENTER);

        billingRecords = MasterPersistence.getInstance().getBillingRecords();
        loadBillData();

        btnAdd.addActionListener(e -> addBill());
        btnDelete.addActionListener(e -> removeBill());
        btnBillReports.addActionListener(e -> JOptionPane.showMessageDialog(this, employeeMenu.viewBillReports()));
        btnBack.addActionListener(e -> dispose());

        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty()));

        add(panelButtons, BorderLayout.SOUTH);
        add(panelTable, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());

        setVisible(true);
    }

    private void loadBillData() {
        for (BillingRecord billingRecord : billingRecords) {
            model.addRow(new Object[]{
                    billingRecord.getCustomerID(),                 // Customer ID
                    billingRecord.getBillingMonth(),               // Billing Month
                    billingRecord.getCurrentMeterReadingRegular(), // Regular Reading
                    billingRecord.getCurrentMeterReadingPeak(),    // Peak Reading
                    billingRecord.getReadingEntryDate(),           // Reading Entry Date
                    billingRecord.getCostOfElectricity(),          // Cost of Electricity
                    billingRecord.getSalesTaxAmount(),             // Sales Tax Amount
                    billingRecord.getFixedCharges(),               // Fixed Charges
                    billingRecord.getTotalBillingAmount(),         // Total Billing
                    billingRecord.getDueDate(),                    // Due Date
                    billingRecord.getBillPaidStatus(),             // Bill Paid Status
                    billingRecord.getBillPaymentDate()             // Payment Date
            });
        }
    }

    private void removeBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this billing record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                billingRecords.remove(selectedRow);
                model.removeRow(selectedRow);
                MasterPersistence.getInstance().setBillingRecordsUpdated();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBill() {
        AddBillingRecordScreen addBillingRecordScreen = new AddBillingRecordScreen();
        if(addBillingRecordScreen.isSubmitted()) {
            BillingRecord br = addBillingRecordScreen.getNewBillingRecord();
            MasterPersistence.getInstance().getBillingRecords().add(br);
            MasterPersistence.getInstance().setBillingRecordsUpdated();
        }
    }
}
