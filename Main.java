import Controller.CustomerMenu;
import Controller.EmployeeMenu;
import Controller.UserWrapper;
import Model.*;
import View.LoginScreen;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new LoginScreen();
        ArrayList<TariffTax> tariffs = TariffTaxPersistence.readFromFile();
        ArrayList<Customer> customers = CustomerPersistence.readFromFile();
        ArrayList<NADRARecord> nadraRecords = NADRADBPersistence.readFromFile();
        ArrayList<BillingRecord> billingRecords = BillingRecordPersistence.readFromFile();

        System.out.println(billingRecords);

        UserWrapper myUser = new UserWrapper();

        Scanner input = new Scanner(System.in);
        while (true) {
            int loginStatus = myUser.getLoginStatus();
            System.out.println(loginStatus);

            if (loginStatus == 1) {
                EmployeeMenu employeeMenu = new EmployeeMenu(myUser.getMyUser());
                employeeMenu.runMenu(input, customers, tariffs, nadraRecords, billingRecords);

            } else if (loginStatus == 2) {
                Customer myCustomer = (Customer) myUser.getMyUser();
                for (Customer customer : customers) {
                    if (customer.getCustomerID().equals(myCustomer.getCustomerID())) {
//                        System.out.println("My Models.Customer updated");
                        myCustomer = customer;
                        break;
                    }
                }
                CustomerMenu customerMenu = new CustomerMenu(myCustomer);
                customerMenu.runMenu(input, tariffs, nadraRecords, billingRecords);
            } else {
                System.out.println("Invalid login details! Please enter again: ");
                continue;
            }

            while (true) {
                System.out.println("Do you want to continue? (y/n)");
                char choice = input.next().charAt(0);
                if (choice == 'n' || choice == 'N') {
                    input.close();
                    TariffTaxPersistence.writeToFile(tariffs);
                    CustomerPersistence.writeToFile(customers);
                    NADRADBPersistence.writeToFile(nadraRecords);
                    BillingRecordPersistence.writeToFile(billingRecords);
                    System.out.println("All files updated. \nThank you for using the LESCO system!");
                    return;
                } else if (choice != 'y' && choice != 'Y') {
                    System.out.println("Invalid choice!");
                }
            }
        }

    }

}