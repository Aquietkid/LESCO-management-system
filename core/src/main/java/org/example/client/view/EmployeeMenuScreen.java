package org.example.client.view;

import org.example.client.controller.EmployeeMenu;
import org.example.commons.model.MasterPersistence;
import org.example.commons.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EmployeeMenuScreen extends JFrame {
    private final EmployeeMenu employeeMenu;

    public EmployeeMenuScreen(User employee) {
        init();
        this.employeeMenu = new EmployeeMenu(employee);
    }

    private void init() {
        JPanel panelMenu = initjPanel();
        ImageIcon logo = new ImageIcon("org/example/server/assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Customer Menu");
        setVisible(true);
        setMinimumSize(new Dimension(400, 300));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(panelMenu);

        setTitle("Employee Menu");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MasterPersistence.getInstance().writeToFiles();
                new LoginScreen();
            }
        });
        setVisible(true);
    }

    private JPanel initjPanel() {
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new GridLayout(3, 3, 20, 20));

        JButton customersButton = new JButton("Customers");
        JButton billsButton = new JButton("Bills");
        JButton tariffsAndTaxesButton = new JButton("Tariffs and Taxes");
        JButton NADRAButton = new JButton("NADRA Records");
        JButton changePasswordButton = new JButton("Change Password");
        JButton exitButton = new JButton("Exit");

        customersButton.addActionListener(e -> new CustomersView(employeeMenu));
        billsButton.addActionListener(e -> new BillsViewEmployee(employeeMenu));
        tariffsAndTaxesButton.addActionListener(e -> new TariffTaxView());
        NADRAButton.addActionListener(e -> new NADRARecordView(employeeMenu));
        changePasswordButton.addActionListener(e -> new PasswordChangeScreen(employeeMenu));
        exitButton.addActionListener(e -> this.dispose());

        panelMenu.add(customersButton);
        panelMenu.add(billsButton);
        panelMenu.add(tariffsAndTaxesButton);
        panelMenu.add(NADRAButton);
        panelMenu.add(changePasswordButton);
        panelMenu.add(exitButton);

        panelMenu.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panelMenu;
    }
}
