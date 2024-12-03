package org.example.commons.model;


import org.example.commons.model.*;

import java.util.ArrayList;


/**
 * The master resource manager.
 * Holds ArrayList<E> objects of Customer, Employee, BillingRecord, NADRARecord, and TariffTax types.
 * The data can be accessed using methods.
 */
public class MasterPersistence {
    private final ArrayList<Customer> customers;
    private final ArrayList<Employee> employees;
    private final ArrayList<BillingRecord> billingRecords;
    private final ArrayList<NADRARecord> nadraRecords;
    private final ArrayList<TariffTax> tariffTaxes;

    boolean customersUpdated = false;
    boolean employeesUpdated = false;
    boolean billingRecordsUpdated = false;
    boolean nadraRecordsUpdated = false;
    boolean tariffTaxesUpdated = false;

    private static MasterPersistence instance;

    public static MasterPersistence getInstance() {
        if (instance == null) {
            instance = new MasterPersistence();
        }
        return instance;
    }


    private MasterPersistence() {
        customers = CustomerPersistence.readFromFile();
        employees = EmployeePersistence.readFromFile();
        billingRecords = BillingRecordPersistence.readFromFile();
        nadraRecords = NADRADBPersistence.readFromFile();
        tariffTaxes = TariffTaxPersistence.readFromFile();
    }

    public void writeToFiles() {
        if (customersUpdated) CustomerPersistence.writeToFile(customers);
        if (employeesUpdated) EmployeePersistence.writeToFile(employees);
        if (billingRecordsUpdated) BillingRecordPersistence.writeToFile(billingRecords);
        if (nadraRecordsUpdated) NADRADBPersistence.writeToFile(nadraRecords);
        if (tariffTaxesUpdated) TariffTaxPersistence.writeToFile(tariffTaxes);
        setAllFlagsFalse();
        System.out.println("All files written successfully!");
    }

    private void setAllFlagsFalse() {
        this.customersUpdated = false;
        this.employeesUpdated = false;
        this.billingRecordsUpdated = false;
        this.nadraRecordsUpdated = false;
        this.tariffTaxesUpdated = false;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<BillingRecord> getBillingRecords() {
        return billingRecords;
    }

    public ArrayList<NADRARecord> getNadraRecords() {
        return nadraRecords;
    }

    public ArrayList<TariffTax> getTariffTaxes() {
        return tariffTaxes;
    }

    public void setCustomersUpdated() {
        this.customersUpdated = true;
    }

    public void setEmployeesUpdated() {
        this.employeesUpdated = true;
    }

    public void setBillingRecordsUpdated() {
        this.billingRecordsUpdated = true;
    }

    public void setNadraRecordsUpdated() {
        this.nadraRecordsUpdated = true;
    }

    public void setTariffTaxesUpdated() {
        this.tariffTaxesUpdated = true;
    }
}
