package View;

import Controller.EmployeeMenu;

import javax.swing.*;
import java.awt.*;

public class PasswordChangeScreen extends JFrame {

    JLabel lblOldPassword;
    JLabel lblNewPassword;
    JLabel lblConfirmPassword;

    JPasswordField oldPassword;
    JPasswordField newPassword;
    JPasswordField confirmPassword;

    JButton changePassword = new JButton("Change Password");

    Controller.EmployeeMenu employeeMenu;

    PasswordChangeScreen(Controller.EmployeeMenu employeeMenu) {
        this.employeeMenu = employeeMenu;
        init();
    }

    private void init() {

        lblOldPassword = new JLabel("Old Password");
        lblNewPassword = new JLabel("New Password");
        lblConfirmPassword = new JLabel("Confirm Password");

        oldPassword = new JPasswordField();
        newPassword = new JPasswordField();
        confirmPassword = new JPasswordField();

        changePassword.addActionListener(e -> {
            int val = employeeMenu.changePassword(String.valueOf(oldPassword), String.valueOf(newPassword), String.valueOf(confirmPassword.getPassword()));
            if(val == EmployeeMenu.PASSWORD_MISMATCH) {
                JOptionPane.showMessageDialog(this, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(val == EmployeeMenu.CONFIRM_PASSWORD_MISMATCH) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(val == EmployeeMenu.OLD_NEW_PASSWORDS_SAME) {
                JOptionPane.showMessageDialog(this, "Old and new passwords can not be the same!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (val == EmployeeMenu.PASSWORD_TOO_SHORT) {
                JOptionPane.showMessageDialog(this, "New password is too short! Enter 6 or more characters.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        setLayout(new GridLayout(2, 2, 20, 20));
        setBounds(560, 190, 800, 600);
        add(oldPassword);
        add(newPassword);
        add(confirmPassword);
        add(changePassword);
        setTitle("Change Password");

        setVisible(true);

    }
}
