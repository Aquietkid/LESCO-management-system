package View;

import Controller.EmployeeMenu;
import Controller.UserWrapper;
import Model.Employee;
import Model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EmployeeMenuScreen extends JFrame {
    private JButton customersButton;
    private JButton billsButton;
    private JButton tariffsAndTaxesButton;
    private JButton changePasswordButton;
    private JButton exitButton;
    private JPanel panelMenu;
    private Controller.EmployeeMenu employeeMenu;

    public EmployeeMenuScreen(User employee) {
        init();
        this.employeeMenu = new EmployeeMenu(employee);
    }

    private void init() {
        panelMenu = new JPanel();
        panelMenu.setLayout(new GridLayout(3, 3, 20, 20));

        customersButton = new JButton("Customers");
        billsButton = new JButton("Bills");
        tariffsAndTaxesButton = new JButton("Tariffs and Taxes");
        changePasswordButton = new JButton("Change Password");
        exitButton = new JButton("Exit");

        customersButton.addActionListener(e -> {});
        billsButton.addActionListener(e -> {});
        tariffsAndTaxesButton.addActionListener(e -> {});
        changePasswordButton.addActionListener(e -> {});
        exitButton.addActionListener(e -> this.dispose());


        panelMenu.add(customersButton);
        panelMenu.add(billsButton);
        panelMenu.add(tariffsAndTaxesButton);
        panelMenu.add(changePasswordButton);
        panelMenu.add(exitButton);

        panelMenu.setBorder(new EmptyBorder(10, 10, 10, 10));
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(560, 190, 800, 600);
        setTitle("Customer Menu");
        setVisible(true);
        setMinimumSize(new Dimension(400, 300));

        add(panelMenu);

        setTitle("Employee Menu");
        setVisible(true);
    }


}
