package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomerMenuScreen extends JFrame {
    private final org.example.controller.CustomerMenu customerMenu;

    public CustomerMenuScreen(org.example.controller.CustomerMenu customerMenu) {
        this.customerMenu = customerMenu;
        createUIComponents();
    }

    public static final int VIEW_BILL = 1;
    public static final int ESTIMATE_UPCOMING_BILL = 2;
    public static final int UPDATE_CNIC_EXPIRY = 3;
    public static final int EXIT = 4;

    private void createUIComponents() {
        JButton viewBillsButton = new JButton("view Bills");
        viewBillsButton.addActionListener(e -> customerMenu.executeMenuTask(VIEW_BILL, this));

        JButton estimateUpcomingBillButton = new JButton("Estimate Upcoming Bill");
        estimateUpcomingBillButton.addActionListener(e -> customerMenu.executeMenuTask(ESTIMATE_UPCOMING_BILL, this));

        JButton updateCNICExpiryButton = new JButton("Update CNIC Expiry");
        updateCNICExpiryButton.addActionListener(e -> customerMenu.executeMenuTask(UPDATE_CNIC_EXPIRY, this));

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());

        JPanel customerMenuPanel = new JPanel();
        customerMenuPanel.setLayout(new GridLayout(4, 0, 20, 20));
        customerMenuPanel.add(viewBillsButton);
        customerMenuPanel.add(estimateUpcomingBillButton);
        customerMenuPanel.add(updateCNICExpiryButton);
        customerMenuPanel.add(exitButton);
        customerMenuPanel.setBorder(BorderFactory.createTitledBorder("Customer Menu"));

        add(customerMenuPanel);
        ImageIcon logo = new ImageIcon("org/example/assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(560, 190, 800, 600);
        setTitle("Customer Menu");
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                new LoginScreen();
                dispose();
            }
        });
    }
}
