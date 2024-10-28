package View;

import Model.MasterPersistence;
import Model.TariffTax;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TariffTaxView extends JFrame {
    private DefaultTableModel tableModel;
    JPanel panelButtons;
    JButton btnExit;
    JTextField searchField;
    private ArrayList<TariffTax> originalData;

    public TariffTaxView() {
        init();
    }

    private void init() {
        setTitle("Tariff Tax Information");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(searchField, BorderLayout.CENTER);

        String[] columnNames = {"Meter Type", "Customer Type", "Regular Unit Price", "Peak Hour Unit Price", "Tax Percentage", "Fixed Charges"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable tariffTable = new JTable(tableModel);
        tariffTable.setDefaultRenderer(Object.class, new HighlightRenderer());
        panelButtons = new JPanel();
        btnExit = new JButton("Exit");
        panelButtons.add(btnExit);
        tariffTable.setFillsViewportHeight(true);
        btnExit.addActionListener(e -> dispose());

        originalData = loadTableData();
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

        JScrollPane scrollPane = new JScrollPane(tariffTable);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelButtons, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(panel);
        setVisible(true);

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                if (col == 0 || col == 1) return;
                String value = (String) tableModel.getValueAt(row, col);
                TariffTax tariffTax = originalData.get(row);

                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to update tariff & tax details? ") == JOptionPane.YES_OPTION) {
                    switch (col) {
                        case 2 -> tariffTax.setRegularUnitPrice(Double.parseDouble(value));
                        case 3 -> tariffTax.setPeakHourUnitPrice(Double.parseDouble(value));
                        case 4 -> tariffTax.setTaxPercentage(Double.parseDouble(value));
                        case 5 -> tariffTax.setFixedCharges(Double.parseDouble(value));
                    }
                    MasterPersistence.getInstance().setTariffTaxesUpdated();
                }
            }
        });
    }

    private ArrayList<TariffTax> loadTableData() {
        ArrayList<TariffTax> tariffTaxes = MasterPersistence.getInstance().getTariffTaxes();
        updateTable(tariffTaxes);
        return tariffTaxes;
    }

    private void updateTable(ArrayList<TariffTax> tariffTaxes) {
        tableModel.setRowCount(0);
        for (TariffTax tariff : tariffTaxes) {
            Object[] rowData = {tariff.getMeterType(), tariff.getCustomerType(), tariff.getRegularUnitPrice(),
                    tariff.getPeakHourUnitPrice() != null ? tariff.getPeakHourUnitPrice() : "N/A",
                    tariff.getTaxPercentage(), tariff.getFixedCharges()};
            tableModel.addRow(rowData);
        }
    }

    private void applyFilter(String query) {
        ArrayList<TariffTax> filteredList = (ArrayList<TariffTax>) originalData.stream()
                .filter(tariff -> tariff.getMeterType().toLowerCase().contains(query)
                        || tariff.getCustomerType().toLowerCase().contains(query)
                        || String.valueOf(tariff.getRegularUnitPrice()).contains(query)
                        || String.valueOf(tariff.getPeakHourUnitPrice()).contains(query)
                        || String.valueOf(tariff.getTaxPercentage()).contains(query)
                        || String.valueOf(tariff.getFixedCharges()).contains(query))
                .collect(Collectors.toList());
        updateTable(filteredList);
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
                StringBuilder highlightedText = new StringBuilder("<html>" + text.substring(0, start) +
                        "<span style='background-color: yellow;'>" + text.substring(start, end) + "</span>" +
                        text.substring(end) + "</html>");
                setText(highlightedText.toString());
            } else {
                setText(text);
            }
        }
    }
}
