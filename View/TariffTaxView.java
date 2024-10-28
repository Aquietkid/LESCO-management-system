package View;

import Model.MasterPersistence;
import Model.TariffTax;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TariffTaxView extends JFrame {
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columnNames = {"Meter Type", "Customer Type", "Regular Unit Price", "Peak Hour Unit Price", "Tax Percentage", "Fixed Charges"};

        tableModel = new DefaultTableModel(columnNames, 0);
        JTable tariffTable = new JTable(tableModel);
        panelButtons = new JPanel();
        btnExit = new JButton("Exit");
        panelButtons.add(btnExit);
        tariffTable.setFillsViewportHeight(true);

        btnExit.addActionListener(e -> dispose());

        ArrayList<TariffTax> tariffTaxes = loadTableData();

        JScrollPane scrollPane = new JScrollPane(tariffTable);
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
                TariffTax tariffTax = tariffTaxes.get(row);

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
        return tariffTaxes;
    }
}
