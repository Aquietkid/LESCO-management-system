package org.example.controller;

import org.example.commons.model.Customer;
import org.example.commons.model.Employee;
import org.example.commons.model.MasterPersistence;
import org.example.server.controller.LoginMenu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginMenuTest {

    private org.example.server.controller.LoginMenu loginMenu;
    private MasterPersistence mockPersistence;
    private ArrayList<Employee> mockEmployees;
    private ArrayList<Customer> mockCustomers;
    private MockedStatic<MasterPersistence> masterPersistenceMock;

    @BeforeEach
    void setUp() {
        loginMenu = new org.example.server.controller.LoginMenu();
        mockPersistence = Mockito.mock(MasterPersistence.class);
        mockEmployees = new ArrayList<>();
        mockCustomers = new ArrayList<>();

        Employee emp = new Employee("empUser", "empPass");
        mockEmployees.add(emp);

        Customer cust = new Customer(
                "0001",
                "1234567890123",
                "John Doe",
                "123 Street, City",
                "1234567890",
                true,
                true,
                "2024-01-01"
        );
        mockCustomers.add(cust);

        Mockito.when(mockPersistence.getEmployees()).thenReturn(mockEmployees);
        Mockito.when(mockPersistence.getCustomers()).thenReturn(mockCustomers);

        masterPersistenceMock = Mockito.mockStatic(MasterPersistence.class);
        masterPersistenceMock.when(MasterPersistence::getInstance).thenReturn(mockPersistence);
    }

    @AfterEach
    void tearDown() {
        masterPersistenceMock.close();
        mockPersistence = null;
        mockEmployees = null;
        mockCustomers = null;
    }

    @Test
    void testLoginWithValidEmployeeCredentials() {
        int result = loginMenu.login("empUser", "empPass");
        assertEquals(org.example.server.controller.LoginMenu.EMPLOYEE_ID, result, "Login with valid employee credentials should return EMPLOYEE_ID.");
    }

    @Test
    void testLoginWithValidCustomerCredentials() {
        int result = loginMenu.login("0001", "1234567890123");
        assertEquals(org.example.server.controller.LoginMenu.CUSTOMER_ID, result, "Login with valid customer credentials should return CUSTOMER_ID.");
    }

    @Test
    void testLoginWithInvalidCredentials() {
        int result = loginMenu.login("invalidUser", "invalidPass");
        assertEquals(org.example.server.controller.LoginMenu.UNKNOWN_ID, result, "Login with invalid credentials should return UNKNOWN_ID.");
    }

    @Test
    void testLoginWithEmptyUsernameAndPassword() {
        int result = loginMenu.login("", "");
        assertEquals(org.example.server.controller.LoginMenu.UNKNOWN_ID, result, "Login with empty username and password should return UNKNOWN_ID.");
    }

    @Test
    void testLoginWithNullUsernameAndPassword() {
        int result = loginMenu.login(null, null);
        assertEquals(org.example.server.controller.LoginMenu.UNKNOWN_ID, result, "Login with null username and password should return UNKNOWN_ID.");
    }

    @Test
    void testLoginWhenNoEmployeesOrCustomersExist() {
        // Empty the mock data
        mockEmployees.clear();
        mockCustomers.clear();

        int result = loginMenu.login("anyUser", "anyPass");
        assertEquals(LoginMenu.UNKNOWN_ID, result, "Login when no employees or customers exist should return UNKNOWN_ID.");
    }
}
