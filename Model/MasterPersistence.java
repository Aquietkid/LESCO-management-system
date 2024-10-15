package Model;

import java.util.ArrayList;


/**
 * The master resource manager.
 * Holds ArrayList<E> objects of Customer, Employee, BillingRecord, NADRARecord, and TarrifTax types.
 * The data can be accessed using methods.
 */
public class MasterPersistence {

    private ArrayList<Customer> customers;
    private ArrayList<Employee> employees;
    private ArrayList<BillingRecord> billingRecords;
    private ArrayList<NADRARecord> nadraRecords;
    private ArrayList<TariffTax> tariffTaxes;

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
        CustomerPersistence.writeToFile(customers);
        EmployeePersistence.writeToFile(employees);
        BillingRecordPersistence.writeToFile(billingRecords);
        NADRADBPersistence.writeToFile(nadraRecords);
        TariffTaxPersistence.writeToFile(tariffTaxes);
        System.out.println("All files written successfully!");
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
}
