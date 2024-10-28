package View;

import Model.BillingRecord;
import Model.Customer;
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
    String searchText = "";

    public BillsViewEmployee(Controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("Billing Records Management");
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"Customer ID", "Billing Month", "Regular Reading", "Peak Reading", "Reading Entry Date", "Cost of Electricity", "Sales Tax Amount", "Fixed Charges", "Total Billing Amount", "Due Date", "Bill Paid", "Payment Date"};

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

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

        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty()));

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
            model.addRow(new Object[]{billingRecord.getCustomerID(), billingRecord.getBillingMonth(), billingRecord.getCurrentMeterReadingRegular(), billingRecord.getCurrentMeterReadingPeak(), billingRecord.getReadingEntryDate(), billingRecord.getCostOfElectricity(), billingRecord.getSalesTaxAmount(), billingRecord.getFixedCharges(), billingRecord.getTotalBillingAmount(), billingRecord.getDueDate(), billingRecord.getBillPaidStatus(), billingRecord.getBillPaymentDate()});
        }
    }

    private void addBill() {
        AddBillingRecordScreen addBillingRecordScreen = new AddBillingRecordScreen(employeeMenu);
        if (addBillingRecordScreen.isSubmitted()) {
            BillingRecord br = addBillingRecordScreen.getNewBillingRecord();

            if (isMostRecentBill(br, billingRecords)) {
                // Add new record to the table model
                model.addRow(new Object[]{br.getCustomerID(), br.getBillingMonth(), br.getCurrentMeterReadingRegular(), br.getCurrentMeterReadingPeak(), br.getReadingEntryDate(), br.getCostOfElectricity(), br.getSalesTaxAmount(), br.getFixedCharges(), br.getTotalBillingAmount(), br.getDueDate(), br.getBillPaidStatus(), br.getBillPaymentDate()});

                ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
                int i = 0;
                for (Customer customer : customers) {
                    if (customer.getCustomerID().equals(br.getCustomerID())) {
                        break;
                    }
                }
                if (i < customers.size()) {
                    customers.get(i).setRegularUnitsConsumed(br.getCurrentMeterReadingRegular());
                    if (customers.get(i).getThreePhase()) {
                        customers.get(i).setPeakUnitsConsumed(br.getCurrentMeterReadingPeak());
                    }
                    MasterPersistence.getInstance().setCustomersUpdated();
                    System.out.println(MasterPersistence.getInstance().getBillingRecords());
                    System.out.println(MasterPersistence.getInstance().getCustomers());
                }
                billingRecords.add(br);  // Add new billing record
                MasterPersistence.getInstance().setBillingRecordsUpdated();


                revalidate();
                repaint();

            } else {
                JOptionPane.showMessageDialog(this, "Only the most recent month's bill can be added.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void removeBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            BillingRecord selectedRecord = billingRecords.get(selectedRow);

            if (isMostRecentBill(selectedRecord, billingRecords)) {
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this billing record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    billingRecords.remove(selectedRow);  // Remove from the main list
                    model.removeRow(selectedRow);  // Remove from the table
                    MasterPersistence.getInstance().setBillingRecordsUpdated();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Only the most recent month's bill can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSearch(TableRowSorter<DefaultTableModel> sorter) {
        searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        table.repaint();
    }

    private boolean isMostRecentBill(BillingRecord currentBill, ArrayList<BillingRecord> allBills) {
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


    private int compareBillingMonths(String month1, String month2) {
        String[] parts1 = month1.split("/");
        String[] parts2 = month2.split("/");

        int year1 = Integer.parseInt(parts1[1]);
        int monthValue1 = Integer.parseInt(parts1[0]);
        int year2 = Integer.parseInt(parts2[1]);
        int monthValue2 = Integer.parseInt(parts2[0]);

        if (year1 != year2) return Integer.compare(year1, year2);
        return Integer.compare(monthValue1, monthValue2);
    }

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
