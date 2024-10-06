package View;

import javax.swing.*;
import java.awt.*;

public class CustomerMenu extends JFrame {
    private final Controller.CustomerMenu customerMenu;
    private JButton viewBillsButton;
    private JButton estimateUpcomingBillButton;
    private JButton updateCNICExpiryButton;
    private JButton exitButton;
    private JPanel customerMenuPanel;

    public CustomerMenu(Controller.CustomerMenu customerMenu) {
        this.customerMenu = customerMenu;
        createUIComponents();
    }

    public static int VIEW_BILL = 1;
    public static int ESTIMATE_UPCOMING_BILL = 2;
    public static int UPDATE_CNIC_EXPIRY = 3;
    public static int EXIT = 4;

    private void createUIComponents() {
        viewBillsButton = new JButton("View Bills");
        viewBillsButton.addActionListener(e -> customerMenu.executeMenuTask(VIEW_BILL, this));

        estimateUpcomingBillButton = new JButton("Estimate Upcoming Bill");
        estimateUpcomingBillButton.addActionListener(e -> customerMenu.executeMenuTask(ESTIMATE_UPCOMING_BILL, this));

        updateCNICExpiryButton = new JButton("Update CNIC Expiry");
        updateCNICExpiryButton.addActionListener(e -> customerMenu.executeMenuTask(UPDATE_CNIC_EXPIRY, this));

        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> customerMenu.executeMenuTask(EXIT, this));

        customerMenuPanel = new JPanel();
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
        setVisible(true);
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);
    }
}
