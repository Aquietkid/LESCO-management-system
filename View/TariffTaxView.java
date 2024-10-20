package View;

import Model.Customer;
import Model.MasterPersistence;
import Model.TariffTax;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TariffTaxView extends JFrame {
    private JTable tariffTable;
    private DefaultTableModel tableModel;
    JPanel panelButtons;
    JButton btnExit;

    public TariffTaxView() {
        init();
    }

    private void init() {
        setTitle("Tariff Tax Information");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"Meter Type", "Customer Type", "Regular Unit Price", "Peak Hour Unit Price", "Tax Percentage", "Fixed Charges"};

        tableModel = new DefaultTableModel(columnNames, 0);
        tariffTable = new JTable(tableModel);
        panelButtons = new JPanel();
        btnExit = new JButton("Exit");
        panelButtons.add(btnExit);
        tariffTable.setFillsViewportHeight(true);

        btnExit.addActionListener(e -> {
            dispose();
        });

        ArrayList<TariffTax> tariffTaxes = MasterPersistence.getInstance().getTariffTaxes();

        for (TariffTax tariff : tariffTaxes) {
            Object[] rowData = {
                    tariff.getMeterType(),
                    tariff.getCustomerType(),
                    tariff.getRegularUnitPrice(),
                    tariff.getPeakHourUnitPrice() != null ? tariff.getPeakHourUnitPrice() : "N/A",
                    tariff.getTaxPercentage(),
                    tariff.getFixedCharges()
            };
            tableModel.addRow(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(tariffTable);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
        setVisible(true);


        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                if (col == 0 || col == 1) return;
                String value = (String) tableModel.getValueAt(row, col);
                TariffTax tariffTax = tariffTaxes.get(row);

                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to update tariff & tax details? ") == JOptionPane.YES_NO_OPTION) {
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
}
