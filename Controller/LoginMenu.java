package Controller;

import Model.CustomerPersistence;
import Model.EmployeePersistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LoginMenu {

    public static int UNKNOWN_ID = 0;
    public static int EMPLOYEE_ID = 1;
    public static int CUSTOMER_ID = 2;

    public int login(String username, String password) {

        Boolean loginStatus;

        try {
            loginStatus = this.compareEmployeeCredentials(username, password);
            if (loginStatus) { //Log in as an employee
                return EMPLOYEE_ID;
            } else {
                loginStatus = this.compareUserCredentials(username, password);
                if (loginStatus) { //Log in as a customer
                    return CUSTOMER_ID;
                }
                return UNKNOWN_ID;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean compareEmployeeCredentials(String _username, String _password) throws FileNotFoundException {

        return readFile(_username, _password, EmployeePersistence.FILENAME);
    }

    private Boolean compareUserCredentials(String _username, String _password) throws FileNotFoundException {
        return readFile(_username, _password, CustomerPersistence.FILENAME);
    }

    private Boolean readFile(String _username, String _password, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                if (username.equals(_username) && password.equals(_password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
