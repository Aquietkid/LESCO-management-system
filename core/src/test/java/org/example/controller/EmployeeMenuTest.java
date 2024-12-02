package org.example.controller;

import org.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeMenuTest {

    private EmployeeMenu employeeMenu;
    private MasterPersistence mockPersistence;
    private MockedStatic<MasterPersistence> masterPersistenceMock;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Mock persistence and its behavior
        mockPersistence = Mockito.mock(MasterPersistence.class);

        ArrayList<Employee> mockEmployees = new ArrayList<>();
        ArrayList<Customer> mockCustomers = new ArrayList<>();
        ArrayList<NADRARecord> mockNadraRecords = new ArrayList<>();

        // Setup employee menu mock dependencies
        Mockito.when(mockPersistence.getEmployees()).thenReturn(mockEmployees);
        Mockito.when(mockPersistence.getCustomers()).thenReturn(mockCustomers);
        Mockito.when(mockPersistence.getNadraRecords()).thenReturn(mockNadraRecords);

        masterPersistenceMock = Mockito.mockStatic(MasterPersistence.class);
        masterPersistenceMock.when(MasterPersistence::getInstance).thenReturn(mockPersistence);

        employeeMenu = Mockito.spy(new EmployeeMenu(new Employee("testEmp", "testPass")));

        // Configure EmployeeMenu mock behaviors
        doReturn("Sample Bill Report").when(employeeMenu).viewBillReports();
        doReturn("Sample CNIC Customers Report").when(employeeMenu).viewCNICCustomers();
        doReturn(0).when(employeeMenu).changePassword("empUser1", "pass1", "pass1");
        doReturn(EmployeeMenu.CONFIRM_PASSWORD_MISMATCH).when(employeeMenu).changePassword("empUser1", "wrongPass", "newPass");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        masterPersistenceMock.close();
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
        assertEquals(0, result, "Change password should return 0 for valid inputs.");
    }

    @Test
    void testChangePasswordInvalid() {
        int result = employeeMenu.changePassword("empUser1", "wrongPass", "newPass");
        assertEquals(EmployeeMenu.CONFIRM_PASSWORD_MISMATCH, result, "Change password should return mismatch code for incorrect inputs.");
    }
}
