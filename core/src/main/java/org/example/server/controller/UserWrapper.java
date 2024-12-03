package org.example.server.controller;

import org.example.commons.model.Customer;
import org.example.commons.model.Employee;
import org.example.commons.model.User;

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

    public int getLoginStatus(String username, String password) {
        LoginMenu loginMenu = new LoginMenu();
        int loginStatus = loginMenu.login(username, password);
        if (loginStatus == LoginMenu.EMPLOYEE_ID) {
            this.setMyUser(new Employee(username, password));
        } else if (loginStatus == LoginMenu.CUSTOMER_ID) {
            this.setMyUser(Customer.getMatchingCustomer(username, password));
        } else this.setMyUser(null);
        return loginStatus;

    }

}
