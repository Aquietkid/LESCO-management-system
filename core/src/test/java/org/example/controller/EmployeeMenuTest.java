package org.example.controller;

import org.example.model.Customer;
import org.example.model.Employee;
import org.example.model.MasterPersistence;
import org.example.model.NADRARecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMenuTest {

    Customer customer;
    EmployeeMenu employeeMenu;
    NADRARecord nadraRecord;
    String CNIC = "1234567891234";
    private MasterPersistence mockPersistence;
    private ArrayList<Employee> mockEmployees;
    private ArrayList<Customer> mockCustomers;
    private ArrayList<NADRARecord> mockNadraRecords;
    private MockedStatic<MasterPersistence> masterPersistenceMock;

    @BeforeEach
    void setUp() {

        mockPersistence = Mockito.mock(MasterPersistence.class);
        mockEmployees = new ArrayList<>();
        mockCustomers = new ArrayList<>();
        mockNadraRecords = new ArrayList<>();

        customer = new Customer(
                "9999",
                CNIC,
                "Test Customer",
                "123 Example Street - Town - 12345",
                "03001234567",
                false,
                true,
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );

        nadraRecord = new NADRARecord(CNIC,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        mockCustomers.add(customer);
        mockNadraRecords.add(nadraRecord);

        Mockito.when(mockPersistence.getEmployees()).thenReturn(mockEmployees);
        Mockito.when(mockPersistence.getCustomers()).thenReturn(mockCustomers);
        Mockito.when(mockPersistence.getNadraRecords()).thenReturn(mockNadraRecords);

        masterPersistenceMock = Mockito.mockStatic(MasterPersistence.class);
        masterPersistenceMock.when(MasterPersistence::getInstance).thenReturn(mockPersistence);

        employeeMenu = new EmployeeMenu(new Employee("testEmp", "testPass"));
    }

    @AfterEach
    void tearDown() {
        masterPersistenceMock.close();
    }

    @Order(2)
    @Test
    void testViewCNICCustomersWhenExists() {
        assertTrue(employeeMenu.viewCNICCustomers().contains(customer.getCustomerID()), "The customer with a CNIC expiring within a month was not found in the message.");
    }

    @Order(3)
    @Test
    void testViewCNICCustomersWhenNotExists() {
        nadraRecord.setExpiryDate(LocalDate.now().plusDays(50).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        assertFalse(employeeMenu.viewCNICCustomers().contains(customer.toString()));
        nadraRecord.setExpiryDate(LocalDate.now().minusDays(50).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    @Order(1)
    @Test
    void testGetExpiryDate() {
        assertEquals(nadraRecord.getExpiryDate(), employeeMenu.getExpiryDate(nadraRecord).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        assertEquals(LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                employeeMenu.getExpiryDate(nadraRecord).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}