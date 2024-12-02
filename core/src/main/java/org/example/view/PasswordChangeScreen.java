package org.example.view;

import org.example.controller.EmployeeMenu;

import javax.swing.*;
import java.awt.*;

public class PasswordChangeScreen extends JFrame {

    JLabel lblOldPassword;
    JLabel lblNewPassword;
    JLabel lblConfirmPassword;

    JPasswordField txtOldPassword;
    JPasswordField txtNewPassword;
    JPasswordField txtConfirmPassword;

    JButton btnExit;
    JButton btnChangePassword;

    JPanel panel;

    final org.example.controller.EmployeeMenu employeeMenu;

    PasswordChangeScreen(org.example.controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {

        lblOldPassword = new JLabel("Old Password");
        lblNewPassword = new JLabel("New Password");
        lblConfirmPassword = new JLabel("Confirm Password");

        txtOldPassword = new JPasswordField();
        txtNewPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        btnChangePassword = new JButton("Change Password");
        btnExit = new JButton("Exit");

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnChangePassword.addActionListener(e -> {
            int val = employeeMenu.changePassword(String.valueOf(txtOldPassword.getPassword()), String.valueOf(txtNewPassword.getPassword()), String.valueOf(txtConfirmPassword.getPassword()));
            System.out.println(val);
            if (val == EmployeeMenu.PASSWORD_MISMATCH) {
                JOptionPane.showMessageDialog(this, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (val == EmployeeMenu.CONFIRM_PASSWORD_MISMATCH) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (val == EmployeeMenu.OLD_NEW_PASSWORDS_SAME) {
                JOptionPane.showMessageDialog(this, "Old and new passwords can not be the same!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (val == EmployeeMenu.PASSWORD_TOO_SHORT) {
                JOptionPane.showMessageDialog(this, "New password is too short! Enter 6 or more characters.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
        });

        btnExit.addActionListener(e -> this.dispose());


        panel.setLayout(new GridLayout(4, 2, 20, 20));
        setBounds(560, 190, 800, 600);
        panel.add(lblOldPassword);
        panel.add(txtOldPassword);
        panel.add(lblNewPassword);
        panel.add(txtNewPassword);
        panel.add(lblConfirmPassword);
        panel.add(txtConfirmPassword);
        panel.add(btnChangePassword);
        panel.add(btnExit);

        add(panel);

        setTitle("Change Password");

        setVisible(true);

    }
}
