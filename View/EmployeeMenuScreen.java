package View;

import Model.MasterPersistence;
import Model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class EmployeeMenuScreen extends JFrame {
    private final Controller.EmployeeMenu employeeMenu;

    public EmployeeMenuScreen(User employee) {
        init();
        this.employeeMenu = new Controller.EmployeeMenu(employee);
    }

    private void init() {
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new GridLayout(3, 3, 20, 20));

        JButton customersButton = new JButton("Customers");
        JButton billsButton = new JButton("Bills");
        JButton tariffsAndTaxesButton = new JButton("Tariffs and Taxes");
        JButton changePasswordButton = new JButton("Change Password");
        JButton exitButton = new JButton("Exit");

        customersButton.addActionListener(e -> {
            new CustomersView();
        });
        billsButton.addActionListener(e -> {
        });
        tariffsAndTaxesButton.addActionListener(e -> {
        });

        changePasswordButton.addActionListener(e -> new PasswordChangeScreen(employeeMenu));

        exitButton.addActionListener(e -> this.dispose());

        panelMenu.add(customersButton);
        panelMenu.add(billsButton);
        panelMenu.add(tariffsAndTaxesButton);
        panelMenu.add(changePasswordButton);
        panelMenu.add(exitButton);

        panelMenu.setBorder(new EmptyBorder(10, 10, 10, 10));
        ImageIcon logo = new ImageIcon("Assets/lesco-pk-logo.png");
        setIconImage(logo.getImage());
        setBounds(560, 190, 800, 600);
        setTitle("Customer Menu");
        setVisible(true);
        setMinimumSize(new Dimension(400, 300));

        add(panelMenu);

        setTitle("Employee Menu");
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                MasterPersistence.getInstance().writeToFiles();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                MasterPersistence.getInstance().writeToFiles();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        setVisible(true);
    }


}
