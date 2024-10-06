package Controller;

import Model.Customer;
import Model.Employee;
import Model.MasterPersistence;

import java.util.ArrayList;

public class LoginMenu {

    public static int UNKNOWN_ID = 0;
    public static int EMPLOYEE_ID = 1;
    public static int CUSTOMER_ID = 2;

    public int login(String username, String password) {

        Boolean loginStatus;

        loginStatus = this.compareEmployeeCredentials(username, password);
        if (loginStatus) {
            return EMPLOYEE_ID;
        } else {
            loginStatus = this.compareUserCredentials(username, password);
            if (loginStatus) {
                return CUSTOMER_ID;
            }
            return UNKNOWN_ID;
        }
    }

    private Boolean compareEmployeeCredentials(String _username, String _password) {
        ArrayList<Employee> employees = MasterPersistence.getInstance().getEmployees();
        for (Employee employee : employees) {
            if (employee.getUsername().equals(_username) && employee.getPassword().equals(_password)) {
                return true;
            }
        }
        return false;
    }

    private Boolean compareUserCredentials(String _username, String _password) {
        ArrayList<Customer> customers = MasterPersistence.getInstance().getCustomers();
        for (Customer customer : customers) {
            if (customer.getUsername().equals(_username) && customer.getPassword().equals(_password)) {
                return true;
            }
        }
        return false;
    }

}
