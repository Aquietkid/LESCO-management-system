package org.example.controller;

import org.example.Model.BillingRecord;
import org.example.Model.Customer;
import org.example.Model.Employee;
import org.example.Model.TariffTax;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeMenuTest {

    @Mock
    private EmployeeMenu employeeMenu;

    @Mock
    private ArrayList<Employee> employees;

    @Mock
    private ArrayList<Customer> customers;

    @Mock
    private ArrayList<BillingRecord> billingRecords;

    @Mock
    private ArrayList<TariffTax> tariffs;

    @Mock
    private ArrayList<Customer> NADRARecords;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        when(employeeMenu.viewBillReports()).thenReturn("Sample Bill Report");
        when(employeeMenu.viewCNICCustomers()).thenReturn("Sample CNIC Customers Report");
        when(employeeMenu.changePassword("empUser1", "pass1", "pass1")).thenReturn(0);
        when(employeeMenu.changePassword("empUser1", "wrongPass", "newPass"))
                .thenReturn(EmployeeMenu.CONFIRM_PASSWORD_MISMATCH);

        Employee emp1 = new Employee("empUser1", "pass1");
        Employee emp2 = new Employee("empUser2", "pass2");
        when(employees.get(0)).thenReturn(emp1);
        when(employees.get(1)).thenReturn(emp2);
        when(employees.size()).thenReturn(2);

        Customer customer1 = new Customer("0001", "1234567890123", "John Doe", "123 Main Street", "1234567890", false, false, "2023-01-01", 0, 0);
        Customer customer2 = new Customer("0002", "9876543210987", "Jane Doe", "456 Elm Street", "9876543210", true, true, "2023-02-01", 0, 0);
        when(customers.get(0)).thenReturn(customer1);
        when(customers.get(1)).thenReturn(customer2);
        when(customers.size()).thenReturn(2);

        BillingRecord billingRecord = new BillingRecord("0001", "2024-01", 100, 50, "06-12-2023", 17, 150, 2500, 5000, "Unpaid");
        when(billingRecords.get(0)).thenReturn(billingRecord);
        when(billingRecords.size()).thenReturn(1);

        TariffTax tariff1 = new TariffTax("1Phase", "Domestic", 11.0, 17.0, 0.25, 150);
        TariffTax tariff2 = new TariffTax("3Phase", "Domestic", 12.0, 18.0, 0.25, 150);
        when(tariffs.get(0)).thenReturn(tariff1);
        when(tariffs.get(1)).thenReturn(tariff2);
        when(tariffs.size()).thenReturn(2);

        Customer nadraCustomer1 = new Customer("1015", "3520123456789", "Ali Imran", "Rawalpindi", "03002759555", true, true, "28-10-2024", 0.0f, 0.0f);
        Customer nadraCustomer2 = new Customer("1016", "3520123456789", "Ali Imran", "Rawalpindi", "03002759555", true, true, "28-10-2024", 0.0f, 0.0f);
        when(NADRARecords.get(0)).thenReturn(nadraCustomer1);
        when(NADRARecords.get(1)).thenReturn(nadraCustomer2);
        when(NADRARecords.size()).thenReturn(2);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testViewBillReportsValid() {
        String report = employeeMenu.viewBillReports();
        assertNotNull(report, "View bill reports should return a valid report.");
        assertEquals("Sample Bill Report", report);
    }

    @Test
    void testViewCNICCustomersValid() {
        String result = employeeMenu.viewCNICCustomers();
        assertNotNull(result, "Viewing CNIC customers should return a list of customers.");
        assertEquals("Sample CNIC Customers Report", result);
    }

    @Test
    void testChangePasswordValid() {
        int result = employeeMenu.changePassword("empUser1", "pass1", "pass1");
        assertEquals(0, result);
    }

    @Test
    void testChangePasswordInvalid() {
        int result = employeeMenu.changePassword("empUser1", "wrongPass", "newPass");
        assertEquals(EmployeeMenu.CONFIRM_PASSWORD_MISMATCH, result);
    }
}
