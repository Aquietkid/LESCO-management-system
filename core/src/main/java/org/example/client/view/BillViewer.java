package org.example.client.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BillViewer extends JFrame {
    private DefaultTableModel model;

    public BillViewer() {
        createUIComponents();
    }

    private void createUIComponents() {
        setTitle("view Bills");
        model = new DefaultTableModel();
        JTable table = new JTable(model);
        JPanel panel = new JPanel();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        model.addColumn("Customer ID");
        model.addColumn("Billing Month");
        model.addColumn("Regular Reading");
        model.addColumn("Peak Reading");
        model.addColumn("Reading Entry Date");
        model.addColumn("Cost");
        model.addColumn("Sales Tax");
        model.addColumn("Fixed Charges");
        model.addColumn("Total Charges");
        model.addColumn("Due Date");
        model.addColumn("Status");
        model.addColumn("Payment Date");

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);

        ImageIcon logo = new ImageIcon("org/example/server/assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setBounds(560, 190, 800, 600);
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);
    }


    public void addRow(String[] text) {
        model.addRow(text);
    }

}