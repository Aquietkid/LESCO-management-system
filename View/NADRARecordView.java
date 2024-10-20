package View;

import Model.MasterPersistence;
import Model.NADRARecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class NADRARecordView extends JFrame {
    private JTable nadraTable;
    private DefaultTableModel tableModel;

    public NADRARecordView() {
        init();
    }

    private void init() {
        setTitle("NADRA Records");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = {"CNIC", "Issuance Date", "Expiry Date"};

        tableModel = new DefaultTableModel(columnNames, 0);
        nadraTable = new JTable(tableModel);
        nadraTable.setFillsViewportHeight(true);

        ArrayList<NADRARecord> nadraRecords = MasterPersistence.getInstance().getNadraRecords();

        for (NADRARecord record : nadraRecords) {
            Object[] rowData = {
                    record.getCNIC(),
                    record.getIssuanceDate(),
                    record.getExpiryDate()
            };
            tableModel.addRow(rowData);
        }

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                String value = (String) tableModel.getValueAt(row, col);
                NADRARecord nadraRecord = nadraRecords.get(row);

                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to update NADRA details? ") == JOptionPane.YES_OPTION) {
                    switch (col) {
                        case 1, 2 -> {
                            return;
                        }
                        case 3 -> nadraRecord.setExpiryDate(value);
                    }
                    MasterPersistence.getInstance().setNadraRecordsUpdated();
                }
            }

        });

        JScrollPane scrollPane = new JScrollPane(nadraTable);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

}
