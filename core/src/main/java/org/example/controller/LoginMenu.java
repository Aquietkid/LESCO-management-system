package org.example.controller;

import org.example.model.Customer;
import org.example.model.Employee;
import org.example.model.MasterPersistence;

import java.util.ArrayList;

public class LoginMenu {

    public static final int UNKNOWN_ID = 0;
    public static final int EMPLOYEE_ID = 1;
    public static final int CUSTOMER_ID = 2;

    public int login(String username, String password) {

        Boolean loginStatus;

        loginStatus = this.compareEmployeeCredentials(username, password);
        if (Boolean.TRUE.equals(loginStatus)) {
            return EMPLOYEE_ID;
        } else {
            loginStatus = this.compareUserCredentials(username, password);
            if (Boolean.TRUE.equals(loginStatus)) {
                return CUSTOMER_ID;
            }
            return UNKNOWN_ID;
        }
    }

    private Boolean compareEmployeeCredentials(String username, String password) {
        ArrayList<Employee> employees = MasterPersistence.getInstance().getEmployees();
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username) && employee.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private Boolean compareUserCredentials(String username, String password) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        for (Customer customer : customers) {
            if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

}
