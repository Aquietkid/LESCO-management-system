package Controller;

import Model.Customer;
import Model.Employee;
import Model.User;

import java.util.Scanner;

public class UserWrapper {
    User myUser;

    public UserWrapper() {
        this.myUser = null;
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    public int getLoginStatus() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        LoginMenu loginMenu = new LoginMenu();
        int loginStatus = loginMenu.login(username, password);
        if (loginStatus == 1) {
            this.setMyUser(new Employee(username, password));
        } else if (loginStatus == 2) {
            this.setMyUser(new Customer(username, password));
        } else this.setMyUser(null);
        return loginStatus;

    }

    public int getLoginStatus(String username, String password) {
        LoginMenu loginMenu = new LoginMenu();
        int loginStatus = loginMenu.login(username, password);
        if (loginStatus == LoginMenu.EMPLOYEE_ID) {
            this.setMyUser(new Employee(username, password));
        } else if (loginStatus == LoginMenu.CUSTOMER_ID) {
            this.setMyUser(new Customer(username, password));
        } else this.setMyUser(null);
        return loginStatus;

    }

}
