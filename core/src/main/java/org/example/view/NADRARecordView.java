package org.example.view;

import org.example.model.MasterPersistence;
import org.example.model.NADRARecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NADRARecordView extends JFrame {
    private DefaultTableModel tableModel;
    private ArrayList<NADRARecord> originalData;
    private JTextField searchField;
    private final org.example.controller.EmployeeMenu employeeMenu;

    public NADRARecordView(org.example.controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {
        setTitle("NADRA Records");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelMain = new JPanel(new BorderLayout(20, 20));
        panelMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(searchField, BorderLayout.CENTER);

        String[] columnNames = {"CNIC", "Issuance Date", "Expiry Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable nadraTable = new JTable(tableModel);
        nadraTable.setDefaultRenderer(Object.class, new HighlightRenderer());
        nadraTable.setFillsViewportHeight(true);

        originalData = MasterPersistence.getInstance().getNadraRecords();
        loadTableData(originalData);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter(searchField.getText().toLowerCase());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter(searchField.getText().toLowerCase());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter(searchField.getText().toLowerCase());
            }
        });

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                String value = (String) tableModel.getValueAt(row, col);
                NADRARecord nadraRecord = originalData.get(row);
                if (col == 0 || col == 1) return;

                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to update NADRA details? ") == JOptionPane.YES_OPTION) {
                    if (col == 2) nadraRecord.setExpiryDate(value);
                    MasterPersistence.getInstance().setNadraRecordsUpdated();
                }
            }
        });

        JButton btnCNICReports = new JButton("CNIC Reports");
        btnCNICReports.addActionListener(e -> JOptionPane.showMessageDialog(this, employeeMenu.viewCNICCustomers()));

        JButton btnExit = new JButton("Back");
        btnExit.addActionListener(e -> dispose());

        JPanel panelButtons = new JPanel(new BorderLayout(20, 20));

        JScrollPane scrollPane = new JScrollPane(nadraTable);

        panelMain.add(searchPanel, BorderLayout.NORTH);
        panelMain.add(scrollPane, BorderLayout.CENTER);
        panelButtons.add(btnCNICReports, BorderLayout.EAST);
        panelButtons.add(btnExit, BorderLayout.WEST);
        panelMain.add(panelButtons, BorderLayout.SOUTH);
        add(panelMain, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadTableData(ArrayList<NADRARecord> records) {
        tableModel.setRowCount(0);
        for (NADRARecord record : records) {
            Object[] rowData = {record.getCNIC(), record.getIssuanceDate(), record.getExpiryDate()};
            tableModel.addRow(rowData);
        }
    }

    private void applyFilter(String query) {
        ArrayList<NADRARecord> filteredList = (ArrayList<NADRARecord>) originalData.stream()
                .filter(record -> record.getCNIC().toLowerCase().contains(query)
                        || record.getIssuanceDate().toLowerCase().contains(query)
                        || record.getExpiryDate().toLowerCase().contains(query))
                .collect(Collectors.toList());
        loadTableData(filteredList);
        repaint();
    }

    private class HighlightRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            String text = value == null ? "" : value.toString();
            String query = searchField.getText().toLowerCase();

            if (!query.isEmpty() && text.toLowerCase().contains(query)) {
                int start = text.toLowerCase().indexOf(query);
                int end = start + query.length();
                String highlightedText = "<html>" + text.substring(0, start) +
                        "<span style='background-color: yellow;'>" + text.substring(start, end) + "</span>" +
                        text.substring(end) + "</html>";
                setText(highlightedText);
            } else {
                setText(text);
            }
        }
    }
}
