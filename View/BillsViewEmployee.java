//package View;
//
//import Model.BillingRecord;
//import Model.MasterPersistence;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.ArrayList;
//
//public class BillsViewEmployee extends JFrame {
//
//    JTable table;
//    DefaultTableModel model;
//    JButton btnAdd;
//    JButton btnDelete;
//    JButton btnBillReports;
//    JButton btnBack;
//    JPanel panelButtons;
//    JPanel panelTable;
//    JScrollPane scrollPane;
//    ArrayList<BillingRecord> billingRecords;
//
//    Controller.EmployeeMenu employeeMenu;
//
//    public BillsViewEmployee(Controller.EmployeeMenu employeeMenu) {
//        this.employeeMenu = employeeMenu;
//        init();
//    }
//
//    private void init() {
//
//        setTitle("Billing Records Management");
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//
//        String[] columnNames = {
//                "Customer ID",
//                "Billing Month",
//                "Regular Reading",
//                "Peak Reading",
//                "Reading Entry Date",
//                "Cost of Electricity",
//                "Sales Tax Amount",
//                "Fixed Charges",
//                "Total Billing Amount",
//                "Due Date",
//                "Bill Paid",
//                "Payment Date"
//        };
//
//        model = new DefaultTableModel(columnNames, 0);
//        table = new JTable(model);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setFillsViewportHeight(true);
//        btnAdd = new JButton("Add");
//        btnDelete = new JButton("Delete");
//        btnBillReports = new JButton("Bill Reports");
//        btnBack = new JButton("Back");
//
//        panelButtons = new JPanel();
//        panelButtons.add(btnAdd);
//        panelButtons.add(btnDelete);
//        panelButtons.add(btnBillReports);
//        panelButtons.add(btnBack);
//
//        scrollPane = new JScrollPane(table);
//        panelTable = new JPanel(new BorderLayout(10, 10));
//        panelTable.add(scrollPane, BorderLayout.CENTER);
//
//        billingRecords = MasterPersistence.getInstance().getBillingRecords();
//        loadBillData();
//
//        btnAdd.addActionListener(e -> addBill());
//        btnDelete.addActionListener(e -> removeBill());
//        btnBillReports.addActionListener(e -> JOptionPane.showMessageDialog(this, employeeMenu.viewBillReports()));
//        btnBack.addActionListener(e -> dispose());
//
//        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty()));
//
//        add(panelButtons, BorderLayout.SOUTH);
//        add(panelTable, BorderLayout.CENTER);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
//        setIconImage(logo.getImage());
//
//        setVisible(true);
//    }
//
//    private void loadBillData() {
//        for (BillingRecord billingRecord : billingRecords) {
//            model.addRow(new Object[]{
//                    billingRecord.getCustomerID(),                 // Customer ID
//                    billingRecord.getBillingMonth(),               // Billing Month
//                    billingRecord.getCurrentMeterReadingRegular(), // Regular Reading
//                    billingRecord.getCurrentMeterReadingPeak(),    // Peak Reading
//                    billingRecord.getReadingEntryDate(),           // Reading Entry Date
//                    billingRecord.getCostOfElectricity(),          // Cost of Electricity
//                    billingRecord.getSalesTaxAmount(),             // Sales Tax Amount
//                    billingRecord.getFixedCharges(),               // Fixed Charges
//                    billingRecord.getTotalBillingAmount(),         // Total Billing
//                    billingRecord.getDueDate(),                    // Due Date
//                    billingRecord.getBillPaidStatus(),             // Bill Paid Status
//                    billingRecord.getBillPaymentDate()             // Payment Date
//            });
//        }
//    }
//
//    private void removeBill() {
//        int selectedRow = table.getSelectedRow();
//        if (selectedRow >= 0) {
//            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this billing record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                billingRecords.remove(selectedRow);
//                model.removeRow(selectedRow);
//                MasterPersistence.getInstance().setBillingRecordsUpdated();
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void addBill() {
//        AddBillingRecordScreen addBillingRecordScreen = new AddBillingRecordScreen(employeeMenu);
//        if(addBillingRecordScreen.isSubmitted()) {
//            BillingRecord br = addBillingRecordScreen.getNewBillingRecord();
//            MasterPersistence.getInstance().getBillingRecords().add(br);
//            MasterPersistence.getInstance().setBillingRecordsUpdated();
//        }
//    }
//
//    /**
//     * Determines if any BillingRecord in the list has a billing month that is newer
//     * than the selected bill's billing month.
//     *
//     * @param billingRecords The list of billing records.
//     * @param selectedBill The selected BillingRecord to compare against.
//     * @return true if there is a newer bill, false otherwise.
//     */
//    public static boolean containsNewerBill(ArrayList<BillingRecord> billingRecords, BillingRecord selectedBill) {
//        String selectedBillingMonth = selectedBill.getBillingMonth();
//
//        for (BillingRecord record : billingRecords) {
//            String recordBillingMonth = record.getBillingMonth();
//
//            // Compare the billing months. The format is assumed to be MM/YYYY.
//            if (isNewerBillingMonth(recordBillingMonth, selectedBillingMonth)) {
//                return true;  // Found a bill that is newer
//            }
//        }
//
//        return false; // No newer bill found
//    }
//
//    /**
//     * Compares two billing months and returns true if the first
//     * billing month is newer than the second billing month.
//     *
//     * @param billingMonth1 The first billing month to compare.
//     * @param billingMonth2 The second billing month to compare.
//     * @return true if billingMonth1 is newer than billingMonth2, false otherwise.
//     */
//    private static boolean isNewerBillingMonth(String billingMonth1, String billingMonth2) {
//        String[] parts1 = billingMonth1.split("/");
//        String[] parts2 = billingMonth2.split("/");
//
//        int month1 = Integer.parseInt(parts1[0]);
//        int year1 = Integer.parseInt(parts1[1]);
//        int month2 = Integer.parseInt(parts2[0]);
//        int year2 = Integer.parseInt(parts2[1]);
//
//        // Compare years first
//        if (year1 > year2) {
//            return true;
//        } else if (year1 == year2) {
//            // If years are the same, compare months
//            return month1 > month2;
//        }
//
//        return false;
//    }
//}


package View;

import Controller.EmployeeMenu;
import Model.BillingRecord;
import Model.MasterPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillsViewEmployee extends JFrame {

    JTable table;
    DefaultTableModel model;
    JButton btnAdd, btnDelete, btnBillReports, btnBack;
    JPanel panelButtons, panelTable, panelTop;
    JScrollPane scrollPane;
    JTextField txtSearch;
    ArrayList<BillingRecord> billingRecords;

    Controller.EmployeeMenu employeeMenu;
    String searchText = ""; // Stores current search text for highlighting

    public BillsViewEmployee(Controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("Billing Records Management");
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {
                "Customer ID", "Billing Month", "Regular Reading", "Peak Reading",
                "Reading Entry Date", "Cost of Electricity", "Sales Tax Amount",
                "Fixed Charges", "Total Billing Amount", "Due Date", "Bill Paid", "Payment Date"
        };

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        // Set custom renderer for highlighting
        table.setDefaultRenderer(Object.class, new HighlightRenderer());

        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnBillReports = new JButton("Bill Reports");
        btnBack = new JButton("Back");

        panelButtons = new JPanel();
        panelButtons.add(btnAdd);
        panelButtons.add(btnDelete);
        panelButtons.add(btnBillReports);
        panelButtons.add(btnBack);

        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Search by any field...");
        panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Search:"));
        panelTop.add(txtSearch);

        scrollPane = new JScrollPane(table);
        panelTable = new JPanel(new BorderLayout(10, 10));
        panelTable.add(scrollPane, BorderLayout.CENTER);

        billingRecords = MasterPersistence.getInstance().getBillingRecords();
        loadBillData();

        btnAdd.addActionListener(e -> addBill());
        btnDelete.addActionListener(e -> removeBill());
        btnBillReports.addActionListener(e -> JOptionPane.showMessageDialog(this, employeeMenu.viewBillReports()));
        btnBack.addActionListener(e -> dispose());

        table.getSelectionModel().addListSelectionListener(e ->
                btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty()));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(sorter);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(sorter);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(sorter);
            }
        });

        add(panelTop, BorderLayout.NORTH);
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
                    billingRecord.getCustomerID(),
                    billingRecord.getBillingMonth(),
                    billingRecord.getCurrentMeterReadingRegular(),
                    billingRecord.getCurrentMeterReadingPeak(),
                    billingRecord.getReadingEntryDate(),
                    billingRecord.getCostOfElectricity(),
                    billingRecord.getSalesTaxAmount(),
                    billingRecord.getFixedCharges(),
                    billingRecord.getTotalBillingAmount(),
                    billingRecord.getDueDate(),
                    billingRecord.getBillPaidStatus(),
                    billingRecord.getBillPaymentDate()
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
        AddBillingRecordScreen addBillingRecordScreen = new AddBillingRecordScreen(employeeMenu);
        if (addBillingRecordScreen.isSubmitted()) {
            BillingRecord br = addBillingRecordScreen.getNewBillingRecord();
            MasterPersistence.getInstance().getBillingRecords().add(br);
            MasterPersistence.getInstance().setBillingRecordsUpdated();
            model.addRow(new Object[]{
                    br.getCustomerID(),
                    br.getBillingMonth(),
                    br.getCurrentMeterReadingRegular(),
                    br.getCurrentMeterReadingPeak(),
                    br.getReadingEntryDate(),
                    br.getCostOfElectricity(),
                    br.getSalesTaxAmount(),
                    br.getFixedCharges(),
                    br.getTotalBillingAmount(),
                    br.getDueDate(),
                    br.getBillPaidStatus(),
                    br.getBillPaymentDate()
            });
        }
    }

    private void updateSearch(TableRowSorter<DefaultTableModel> sorter) {
        searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        table.repaint(); // Trigger re-rendering for highlighting
    }

    // Custom renderer for cell highlighting
    private class HighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null && !searchText.isEmpty()) {
                String cellText = value.toString();
                Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(searchText));
                Matcher matcher = pattern.matcher(cellText);

                if (matcher.find()) {
                    cellText = "<html>" + matcher.replaceAll("<span style='background-color: yellow;'>" + matcher.group() + "</span>") + "</html>";
                }
                setText(cellText);
            } else {
                setText(value != null ? value.toString() : "");
            }

            return this;
        }
    }
}
