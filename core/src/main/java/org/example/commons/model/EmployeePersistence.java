package org.example.commons.model;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class EmployeePersistence {

    public static final String FILENAME = "core/src/main/java/org/example/model/EmployeesData.txt";

    public static void writeToFile(ArrayList<Employee> employees) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Employee e : employees) {
                bw.write(e.toFileString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Employee> readFromFile() {
        ArrayList<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EmployeePersistence.FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                Employee employee = new Employee(username, password);
                employees.add(employee);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return employees;
    }
}
