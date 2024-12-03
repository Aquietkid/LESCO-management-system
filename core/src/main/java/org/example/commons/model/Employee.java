package org.example.commons.model;

public class Employee implements User {
    private final String username;
    private String password;

    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toFileString() {
        return this.username + "," + this.password + "\n";
    }

}
