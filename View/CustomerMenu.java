package View;

import javax.swing.*;
import java.awt.*;

public class CustomerMenu extends JFrame {
    private final Controller.CustomerMenu customerMenu;

    public CustomerMenu(Controller.CustomerMenu customerMenu) {
        this.customerMenu = customerMenu;
        createUIComponents();
    }

    public static final int VIEW_BILL = 1;
    public static final int ESTIMATE_UPCOMING_BILL = 2;
    public static final int UPDATE_CNIC_EXPIRY = 3;
    public static final int EXIT = 4;

    private void createUIComponents() {
        JButton viewBillsButton = new JButton("View Bills");
        viewBillsButton.addActionListener(e -> customerMenu.executeMenuTask(VIEW_BILL, this));

        JButton estimateUpcomingBillButton = new JButton("Estimate Upcoming Bill");
        estimateUpcomingBillButton.addActionListener(e -> customerMenu.executeMenuTask(ESTIMATE_UPCOMING_BILL, this));

        JButton updateCNICExpiryButton = new JButton("Update CNIC Expiry");
        updateCNICExpiryButton.addActionListener(e -> customerMenu.executeMenuTask(UPDATE_CNIC_EXPIRY, this));

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> customerMenu.executeMenuTask(EXIT, this));

        JPanel customerMenuPanel = new JPanel();
        customerMenuPanel.setLayout(new GridLayout(4, 0, 20, 20));
        customerMenuPanel.add(viewBillsButton);
        customerMenuPanel.add(estimateUpcomingBillButton);
        customerMenuPanel.add(updateCNICExpiryButton);
        customerMenuPanel.add(exitButton);
        customerMenuPanel.setBorder(BorderFactory.createTitledBorder("Customer Menu"));

        add(customerMenuPanel);
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(560, 190, 800, 600);
        setTitle("Customer Menu");
        setVisible(true);
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);
    }
}
