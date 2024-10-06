import View.LoginScreen;

public class Main {

    public static void main(String[] args) {
        new LoginScreen();


//        while (true) {
//            int loginStatus = myUser.getLoginStatus();
//            System.out.println(loginStatus);
//
//            if (loginStatus == 1) {
//                EmployeeMenu employeeMenu = new EmployeeMenu(myUser.getMyUser());
//                employeeMenu.runMenu(input, customers, tariffs, nadraRecords, billingRecords);
//
//            } else if (loginStatus == 2) {
//                Customer myCustomer = (Customer) myUser.getMyUser();
//                for (Customer customer : customers) {
//                    if (customer.getCustomerID().equals(myCustomer.getCustomerID())) {
//                        myCustomer = customer;
//                        break;
//                    }
//                }
//                CustomerMenu customerMenu = new CustomerMenu(myCustomer);
//                customerMenu.runMenu(input, tariffs, nadraRecords, billingRecords);
//            } else {
//                System.out.println("Invalid login details! Please enter again: ");
//                continue;
//            }


        // TODO: Go back to login screen after exiting menu

//            while (true) {
//                System.out.println("Do you want to continue? (y/n)");
//                char choice = input.next().charAt(0);
//                if (choice == 'n' || choice == 'N') {
//                    input.close();

        // TODO: Write all data to files at program termination

//                    TariffTaxPersistence.writeToFile(tariffs);
//                    CustomerPersistence.writeToFile(customers);
//                    NADRADBPersistence.writeToFile(nadraRecords);
//                    BillingRecordPersistence.writeToFile(billingRecords);
//                    System.out.println("All files updated. \nThank you for using the LESCO system!");
//                    return;
//                } else if (choice != 'y' && choice != 'Y') {
//                    System.out.println("Invalid choice!");
//                }
//            }
//    }

    }

}