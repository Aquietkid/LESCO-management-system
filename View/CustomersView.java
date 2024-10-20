package View;

import Controller.EmployeeMenu;
import Model.Customer;
import Model.MasterPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


public class CustomersView extends JFrame {

    JTable table;
    DefaultTableModel model;
    JButton btnAdd, btnDelete, btnBack;
    JPanel panelButtons, panelTable;
    JScrollPane scrollPane;
    ArrayList<Customer> customers;

    EmployeeMenu employeeMenu;

    public CustomersView(EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("Customer Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        String[] columnNames = {
                "Customer ID",
                "CNIC",
                "Customer Name",
                "Address",
                "Phone",
                "Is Commercial",
                "Is Three-Phase",
                "Regular Units Consumed",
                "Peak Units Consumed",
                "Connection Date"
        };

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnBack = new JButton("Back");

        panelButtons = new JPanel();
        panelButtons.add(btnAdd);
        panelButtons.add(btnDelete);
        panelButtons.add(btnBack);

        scrollPane = new JScrollPane(table);
        panelTable = new JPanel(new BorderLayout(10, 10));
        panelTable.add(scrollPane, BorderLayout.CENTER);

        customers = MasterPersistence.getInstance().getCustomers();
        loadCustomerData();

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

        table.getSelectionModel().addListSelectionListener(e -> {
            btnDelete.setEnabled(!table.getSelectionModel().isSelectionEmpty());
        });

        add(panelButtons, BorderLayout.SOUTH);
        add(panelTable, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setVisible(true);

    }

    private void addCustomer() {
        employeeMenu.addCustomer(this);
        repaint();
        revalidate();
    }

    private void loadCustomerData() {
        for (Customer customer : customers) {
            model.addRow(new Object[]{
                    customer.getCustomerID(),            // Customer ID
                    customer.getCNIC(),                  // CNIC
                    customer.getCustomerName(),          // Customer Name
                    customer.getAddress(),               // Address
                    customer.getPhone(),                 // Phone
                    customer.getIsCommercial(),          // Is Commercial
                    customer.getThreePhase(),            // Is Three-Phase
                    customer.getRegularUnitsConsumed(),  // Regular Units Consumed
                    customer.getPeakUnitsConsumed(),     // Peak Units Consumed
                    customer.getConnectionDate()         // Connection Date (final)
            });
        }
    }

    private void removeCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
            customers.remove(selectedRow);
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}