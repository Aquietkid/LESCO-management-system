package View;

import controller.EmployeeMenu;
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

public class CustomersView extends JFrame {

    JTable table;
    DefaultTableModel model;
    JButton btnAdd, btnDelete, btnBack;
    JPanel panelButtons, panelTable, panelTop;
    JScrollPane scrollPane;
    JTextField txtSearch;
    ArrayList<Customer> customers;
    String searchText = ""; // To hold the current search term for highlighting

    EmployeeMenu employeeMenu;

    public CustomersView(EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("Customer Management");
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"Customer ID", "CNIC", "Customer Name", "Address", "Phone", "Is Commercial", "Is Three-Phase", "Regular Units Consumed", "Peak Units Consumed", "Connection Date"};

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.setDefaultRenderer(Object.class, new HighlightRenderer());

        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnBack = new JButton("Back");
        panelButtons = new JPanel();
        panelButtons.add(btnAdd);
        panelButtons.add(btnDelete);
        panelButtons.add(btnBack);

        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Search by any field...");
        panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Search:"));
        panelTop.add(txtSearch);

        scrollPane = new JScrollPane(table);
        panelTable = new JPanel(new BorderLayout(10, 10));
        panelTable.add(scrollPane, BorderLayout.CENTER);

        customers = MasterPersistence.getInstance().getCustomers();
        loadCustomerData();

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

        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                if (col == 0 || col == 1 || col == 9) return;
                String value = (String) model.getValueAt(row, col);
                Customer customer = customers.get(row);

                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to update customer details? ") == JOptionPane.YES_OPTION) {
                    try {
                        switch (col) {
                            case 2 -> customer.setCustomerName(value);
                            case 3 -> customer.setAddress(value);
                            case 4 -> customer.setPhone(value);
                            case 5 -> customer.setIsCommercial(Boolean.parseBoolean(value));
                            case 6 -> customer.setThreePhase(Boolean.parseBoolean(value));
                            case 7 -> customer.setRegularUnitsConsumed(Float.parseFloat(value));
                            case 8 -> customer.setPeakUnitsConsumed(Float.parseFloat(value));
                        }
                        MasterPersistence.getInstance().setCustomersUpdated();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> addCustomer());
        btnDelete.addActionListener(e -> removeCustomer());
        btnBack.addActionListener(e -> dispose());
        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty()));

        add(panelTop, BorderLayout.NORTH);
        add(panelButtons, BorderLayout.SOUTH);
        add(panelTable, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setVisible(true);
    }

    private void addCustomer() {
        Customer addedCustomer = employeeMenu.addCustomer(this);
        // TODO: fix added customer not showing in table automatically after addition
        if (addedCustomer != null)
            model.addRow(new Object[]{addedCustomer.getCustomerID(), addedCustomer.getCustomerName(), addedCustomer.getAddress(), addedCustomer.getPhone(), addedCustomer.getIsCommercial(), addedCustomer.getThreePhase(), addedCustomer.getRegularUnitsConsumed(), addedCustomer.getPeakUnitsConsumed(), addedCustomer.getConnectionDate()});
        repaint();
        revalidate();
    }


    private void loadCustomerData() {
        for (Customer customer : customers) {
            model.addRow(new Object[]{customer.getCustomerID(), customer.getCNIC(), customer.getCustomerName(), customer.getAddress(), customer.getPhone(), customer.getIsCommercial(), customer.getThreePhase(), customer.getRegularUnitsConsumed(), customer.getPeakUnitsConsumed(), customer.getConnectionDate()});
        }
    }

    private void removeCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                customers.remove(selectedRow);
                model.removeRow(selectedRow);
                MasterPersistence.getInstance().setCustomersUpdated();
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
