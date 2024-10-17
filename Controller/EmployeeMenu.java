package Controller;

import Model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeMenu extends Menu {

    private final User myEmployee;

    public static final int USERNAME_MISMATCH = -1;
    public static final int PASSWORD_MISMATCH = -2;
    public static final int CONFIRM_PASSWORD_MISMATCH = -3;
    public static final int OLD_NEW_PASSWORDS_SAME = -4;
    public static final int PASSWORD_TOO_SHORT = -5;


    public EmployeeMenu(User employee) {
        this.message = """
                Employee Menu
                1. Create Models.Bill
                2. Modify Bills
                3. Add New Models.Customer
                4. Update a Models.Customer
                5. Record Models.Bill Payment
                6. Update Tariffs and Taxes
                7. View Models.Customer Bills
                8. View Models.Bill Reports
                9. View Customers with CNICs Expiring Soon
                10. View All Customers
                11. Change Password
                12. Exit
                """;
        this.myEmployee = employee;
    }

    public static void addBillingRecord(ArrayList<BillingRecord> billingRecords, ArrayList<Customer> customers, ArrayList<TariffTax> tariffTaxes) {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");
        String customerID, billingMonth, readingEntryDate, dueDate;
        float currentMeterReadingRegular, currentMeterReadingPeak = 0.0f, costOfElectricity, salesTaxAmount, fixedCharges, totalBillingAmount;
        LocalDate readingDate, today = LocalDate.now();

        Customer myCustomer = null;
        TariffTax myTariffTax;


        while (true) {
            System.out.print("Enter 4-digit Models.Customer ID: ");
            customerID = scanner.nextLine().trim();
            if (customerID.matches("\\d{4}")) {
                for (Customer c : customers) {
                    if (c.getCustomerID().equals(customerID)) {
                        myCustomer = c;
                        break;
                    }
                }
                if (myCustomer != null) break;
                else
                    System.out.println("Models.Customer ID does not exist in the records. Please enter a valid customer ID!");
            } else System.out.println("Invalid Models.Customer ID. It must be a 4-digit number.");
        }

        myTariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);

        while (true) {
            System.out.print("Enter Billing Month (MM-YYYY): ");
            billingMonth = scanner.nextLine().trim();
            if (billingMonth.matches("\\d{2}-\\d{4}")) {
                boolean billExists = false;
                LocalDate latestBillDate = null;

                for (BillingRecord b : billingRecords) {
                    if (b.getCustomerID().equals(myCustomer.getCustomerID())) {
                        LocalDate billDate = LocalDate.parse("01-" + b.getBillingMonth(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        if (b.getBillingMonth().equals(billingMonth)) {
                            billExists = true;
                            break;
                        }

                        // Check for the latest bill date in the records
                        if (latestBillDate == null || billDate.isAfter(latestBillDate)) {
                            latestBillDate = billDate;
                        }
                    }
                }
                int enteredMonth = Integer.parseInt(billingMonth.substring(0, 2));
                int enteredYear = Integer.parseInt(billingMonth.substring(3, 7));
                int currentMonth = today.getMonthValue();
                int currentYear = today.getYear();

                LocalDate enteredBillDate = LocalDate.of(enteredYear, enteredMonth, 1);

                if (billExists) {
                    System.out.println("A bill for the entered month already exists.");
                    if (enteredMonth == today.getMonthValue() && enteredYear == today.getYear()) {
                        System.out.println("No new bill can be added! Returning...");
                        return;
                    }

                } else if (enteredYear > currentYear || (enteredYear == currentYear && enteredMonth > currentMonth)) {
                    System.out.println("Billing Month cannot be in the future.");
                } else if (enteredMonth < 1 || enteredMonth > 12) {
                    System.out.println("Invalid month. Please enter a valid month between 01 and 12.");
                } else if (latestBillDate != null && enteredBillDate.isBefore(latestBillDate)) {
                    System.out.println("A newer bill already exists. Please enter a valid billing month.");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid format. Billing Month must be in MM-YYYY format.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter Current Meter Reading Regular: ");
                currentMeterReadingRegular = Float.parseFloat(scanner.nextLine().trim());
                if (currentMeterReadingRegular >= 0) {
                    if (currentMeterReadingRegular < myCustomer.getRegularUnitsConsumed()) {
                        System.out.println("New reading can not be less than the previous reading! ");
                    } else break;
                }
                System.out.println("Meter reading cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        if (myTariffTax.getPeakHourUnitPrice() != null) {
            while (true) {
                try {
                    System.out.print("Enter Current Meter Reading Peak: ");
                    currentMeterReadingPeak = Float.parseFloat(scanner.nextLine().trim());
                    if (currentMeterReadingPeak >= 0) {
                        if (currentMeterReadingPeak < myCustomer.getPeakUnitsConsumed()) {
                            System.out.println("New reading can not be less than the previous reading! ");
                        } else break;
                    }
                    System.out.println("Meter reading cannot be negative.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
        } else {
            System.out.println("No price is charged for peak hour units for this type of meter. ");
        }

        while (true) {
            try {
                System.out.print("Enter Reading Date (DD-MM-YYYY): ");
                readingEntryDate = scanner.nextLine().trim();
                readingDate = LocalDate.parse(readingEntryDate, dateFormatter);
                if (!readingDate.isAfter(today)) {
                    if (readingDate.isBefore(LocalDate.parse(myCustomer.getConnectionDate(), dateFormatter))) {
                        System.out.println("Reading Entry Date cannot be before the connection date (" + myCustomer.getConnectionDate() + ").");
                        continue;
                    }
                    break;
                } else {
                    System.out.println("Reading Entry Date cannot be in the future.");
                }

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date.");
            }
        }

        costOfElectricity = (float) (((currentMeterReadingRegular - myCustomer.getRegularUnitsConsumed()) * myTariffTax.getRegularUnitPrice())
                + ((myTariffTax.getPeakHourUnitPrice() != null) ? ((currentMeterReadingPeak - myCustomer.getPeakUnitsConsumed()) * myTariffTax.getPeakHourUnitPrice()) : 0.0));

        salesTaxAmount = (float) (costOfElectricity * (myTariffTax.getTaxPercentage() / 100));

        fixedCharges = (float) myTariffTax.getFixedCharges();

        totalBillingAmount = costOfElectricity + salesTaxAmount + fixedCharges;
        System.out.println("Total Billing Amount: " + totalBillingAmount);

        dueDate = dateFormatter.format(today.plusDays(7));

        BillingRecord newRecord = new BillingRecord(customerID, billingMonth, currentMeterReadingRegular, currentMeterReadingPeak, readingEntryDate, costOfElectricity, salesTaxAmount, fixedCharges, totalBillingAmount, dueDate);
        billingRecords.add(newRecord);

        System.out.println("Billing record added successfully.");
    }

    @Override
    public void displayMenu() {
        System.out.println(this.message);
    }

    /**
     * Takes the list of customers and the scanner,
     * asks the user to enter a choice variable,
     * and executes the chosen menu function on the
     * basis of the choice variable in an infinite
     * loop. The menu can be exited using the choice
     * value corresponding to exit.
     */
    public void runMenu(Scanner input, ArrayList<Customer> customers, ArrayList<TariffTax> tariffs, ArrayList<NADRARecord> NADRARecords, ArrayList<BillingRecord> billingRecords) {
        int choice;
        do {
            this.displayMenu();
            System.out.println("Enter your choice");
            choice = input.nextInt();
            if (choice > 12 || choice < 0) {
                System.out.println("Invalid choice!");
            } else if (choice == 12) {
                return;
            } else this.executeMenuTask(choice, customers, tariffs, NADRARecords, billingRecords);
        } while (true);
    }

    public void executeMenuTask(int choice, ArrayList<Customer> customers, ArrayList<TariffTax> tariffs, ArrayList<NADRARecord> NADRARecords, ArrayList<BillingRecord> billingRecords) {
        switch (choice) {
            case 1:
                addBillingRecord(billingRecords, customers, tariffs);
                break;
            case 2:
                modifyBill();
                break;
            case 3:
                addNewCustomer(customers);
                break;
            case 4:
                updateCustomerInfo(customers);
                break;
            case 5:
                recordBillPayment(customers, billingRecords);
                break;
            case 6:
                updateTariffTaxInfo(tariffs);
                break;
            case 7:
                viewCustomerBills(customers, billingRecords);
                break;
            case 8:
                viewBillReports(billingRecords);
                break;
            case 9:
                viewCNICCustomers(customers, NADRARecords);
                break;
            case 10:
                viewAllCustomers(customers);
                break;
            case 11:
                changePassword();
                break;
            default:
                System.out.println("Incorrect choice!");
                break;
        }
    }

    private void viewBillReports(ArrayList<BillingRecord> billingRecords) {
        int countPaidBills = 0;
        int countUnpaidBills = 0;
        float unpaidAmount = 0.0f;
        float paidAmount = 0.0f;
        for (BillingRecord br : billingRecords) {
            if (br.getBillPaidStatus()) {
                countPaidBills++;
                paidAmount += br.getTotalBillingAmount();
            } else {
                countUnpaidBills++;
                unpaidAmount += br.getTotalBillingAmount();
            }
        }
        System.out.println("Models.Bill report: ");
        System.out.println("Total unpaid bills: " + countUnpaidBills + "\nTotal amount unpaid: " + unpaidAmount);
        System.out.println("Total paid bills: " + countPaidBills + "\nTotal amount paid: " + paidAmount);
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private void viewAllCustomers(ArrayList<Customer> customers) {
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }

    public void modifyBill() {
        // TODO Create method
    }

    public void addNewCustomer(ArrayList<Customer> customers) {
        Scanner input = new Scanner(System.in);
        String customerID = "1000"; //Initial minimum value
        for (Customer c : customers) {
            if (Integer.parseInt(c.getCustomerID()) > Integer.parseInt(customerID))
                customerID = c.getCustomerID();
        }
        customerID = String.valueOf(Integer.parseInt(customerID) + 1);

        String customerCNIC;
        while (true) {
            System.out.print("Enter Models.Customer CNIC (13-digit, without dashes):");
            customerCNIC = input.nextLine();
            if (customerCNIC.length() != 13) {
                System.out.println("Enter CNIC again!");
            } else {
                break;
            }
        }

        int CNICcount = 0;
        for (Customer customer : customers) {
            if (customer.getCNIC().equals(customerCNIC)) {
                CNICcount++;
            }
            if (CNICcount >= 3) {
                break;
            }
        }
        if (CNICcount == 3) {
            System.out.println("More than 3 meters can not be added for one CNIC! Aborting addition...");
            return;
        }

        System.out.println("Enter customer name: ");
        String customerName = input.nextLine();

        System.out.println("Enter customer address: ");
        String customerAddress = input.nextLine();

        System.out.println("Enter customer phone number: ");
        String phoneNumber = input.nextLine();

        System.out.println("Is the customer commercial? (Y/N): ");
        boolean isCommercial = getBooleanInput(input);

        System.out.println("Is the customer's meter 3-phase? (Y/N): ");
        boolean isThreePhase = getBooleanInput(input);

        String connectionDate = getConnectionDate(input);
        System.out.println("Enter connection date (DD-MM-YYYY): ");

        Customer newCustomer = new Customer(customerID, customerCNIC, customerName, customerAddress, phoneNumber, isCommercial, isThreePhase, connectionDate);
        customers.add(newCustomer);
        System.out.println("Models.Customer added successfully!");
    }

    private boolean getBooleanInput(Scanner input) {
        while (true) {
            String commercial = input.nextLine();
            if (commercial.equals("Y") || commercial.equals("y")) {
                return true;
            } else if (commercial.equals("N") || commercial.equals("n")) {
                return false;
            } else {
                System.out.println("Incorrect choice!");
            }
        }
    }

    private String getConnectionDate(Scanner input) {
        System.out.println("Enter the connection date as follows: ");
        while (true) {
            int day;
            int month;
            int year;
            LocalDate now = LocalDate.now();
            System.out.println("Date should not be after " + now);
            try {
                while (true) {
                    System.out.print("Enter the year (YYYY): ");
                    year = Integer.parseInt(input.nextLine());
                    if (year < 1900 || year > 2100) {
                        System.out.println("Invalid year. Please enter a value between 1900 and 2100.");
                        continue;
                    }
                    if (now.getYear() < year) { //Year is in the future
                        System.out.println("Connection date can not be in future!");
                    }
                    break;
                }

                while (true) {
                    System.out.print("Enter the month (MM): ");
                    month = Integer.parseInt(input.nextLine());
                    if (month < 1 || month > 12) {
                        System.out.println("Invalid month. Please enter a value between 1 and 12.");
                        continue;
                    }
                    if ((year == now.getYear()) && month > now.getMonthValue()) { //Month is in the future
                        System.out.println("Connection date can not be in future!");
                    }
                    break;
                }

                while (true) {
                    System.out.print("Enter the day (DD): ");
                    day = Integer.parseInt(input.nextLine());
                    if (day < 1 || day > 31) {
                        System.out.println("Invalid day. Please enter a value between 1 and 31.");
                        continue;
                    }
                    if ((year == now.getYear()) && month == now.getMonthValue() && day > now.getDayOfMonth()) { //Day is in the future
                        System.out.println("Connection date can not be in future!");
                    }
                    break;
                }


                return String.format("%02d-%02d-%04d", day, month, year);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values.");
            }
        }
    }

    public void recordBillPayment(ArrayList<Customer> customers, ArrayList<BillingRecord> billingRecords) {
        Scanner scanner = new Scanner(System.in);
        String customerID;
        Customer myCustomer = null;

        // Step 1: Get a valid customer ID
        while (true) {
            System.out.print("Enter 4-digit Models.Customer ID: ");
            customerID = scanner.nextLine().trim();
            if (customerID.matches("\\d{4}")) {
                for (Customer c : customers) {
                    if (c.getCustomerID().equals(customerID)) {
                        myCustomer = c;
                        break;
                    }
                }
                if (myCustomer != null) break;
                else
                    System.out.println("Models.Customer ID does not exist in the records. Please enter a valid customer ID!");
            } else {
                System.out.println("Invalid Models.Customer ID. It must be a 4-digit number.");
            }
        }

        // Step 2: Find all unpaid bills
        ArrayList<BillingRecord> unpaidBills = new ArrayList<>();
        for (BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(myCustomer.getCustomerID()) && !br.getBillPaidStatus()) {
                unpaidBills.add(br);
            }
        }

        // Step 3: Handle the case where no unpaid bills exist
        if (unpaidBills.isEmpty()) {
            System.out.println("No unpaid bills found for this customer.");
            return;
        }

        // Step 4: List all unpaid bills and let the user select which one to pay
        System.out.println("Unpaid Bills for Models.Customer ID: " + myCustomer.getCustomerID());
        for (int i = 0; i < unpaidBills.size(); i++) {
            BillingRecord br = unpaidBills.get(i);
            System.out.println((i + 1) + ". Billing Month: " + br.getBillingMonth() + ", Amount Due: " + br.getTotalBillingAmount());
        }

        int selectedBillIndex;
        while (true) {
            try {
                System.out.print("Enter the number of the bill you want to pay (1-" + unpaidBills.size() + "): ");
                selectedBillIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (selectedBillIndex >= 0 && selectedBillIndex < unpaidBills.size()) {
                    break;
                } else {
                    System.out.println("Invalid selection. Please select a number between 1 and " + unpaidBills.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }


        LocalDate today = LocalDate.now();

        // Step 5: Mark the selected bill as paid and update customer’s unit consumption
        BillingRecord selectedBill = unpaidBills.get(selectedBillIndex);
        LocalDate dueDate = LocalDate.parse(selectedBill.getDueDate(), DateTimeFormatter.ofPattern("d-M-yyyy"));
        if (dueDate.isBefore(today)) {
            System.out.println("Models.Bill due date passed. ");
            return;
        }
        selectedBill.setBillPaidStatus(true);  // Mark the bill as paid

        System.out.println("Recording payment for the billing month: " + selectedBill.getBillingMonth());

        // Update regular units consumed
        float currentMeterReadingRegular = selectedBill.getCurrentMeterReadingRegular();
        myCustomer.setRegularUnitsConsumed(currentMeterReadingRegular);

        // Update peak units consumed if applicable
        if (selectedBill.getCurrentMeterReadingPeak() > 0.0f) {
            float currentMeterReadingPeak = selectedBill.getCurrentMeterReadingPeak();
            myCustomer.setPeakUnitsConsumed(currentMeterReadingPeak);
        } else {
            System.out.println("No peak hour charges apply for this customer.");
        }

        // Step 6: Set the bill payment date
        String paymentDate = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        selectedBill.setBillPaymentDate(paymentDate);

        System.out.println("Payment recorded successfully for the billing month: " + selectedBill.getBillingMonth());
        System.out.println("Payment Date: " + paymentDate);
    }

    public void viewCustomerBills(ArrayList<Customer> customers, ArrayList<BillingRecord> billingRecords) {
        Scanner scanner = new Scanner(System.in);
        String customerID;
        while (true) {
            System.out.print("Enter 4-digit Models.Customer ID: ");
            customerID = scanner.nextLine().trim();
            boolean customerExists = false;
            if (customerID.matches("\\d{4}")) {
                for (Customer c : customers) {
                    if (c.getCustomerID().equals(customerID)) {
                        customerExists = true;
                        break;
                    }
                }
                if (customerExists) break;
                else
                    System.out.println("Models.Customer ID does not exist in the records. Please enter a valid customer ID!");
            } else System.out.println("Invalid Models.Customer ID. It must be a 4-digit number.");
        }

        System.out.println("Bills of Models.Customer " + customerID + ":");
        for (BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(customerID)) {
                System.out.println(br);
            }
        }
        System.out.println("-------------------------------------");

    }

    public void viewCNICCustomers(ArrayList<Customer> customers, ArrayList<NADRARecord> nadraRecords) {
        if (customers != null) {
            LocalDate now = LocalDate.now();
            System.out.println("Displaying customers with CNICs expiring soon: ");
            for (Customer c : customers) {
                for (NADRARecord n : nadraRecords) {
                    if (n.getCNIC().equals(c.getCNIC())) {
                        //Check if date is within the next 30 days
                        LocalDate expiry = getExpiryDate(n);
                        if (!expiry.isBefore(now) && !expiry.isAfter(now.plusDays(30))) {
                            System.out.println(c);
                        }

                    }
                }
            }
        } else System.out.println("There are no customers! ");
        System.out.println("--------------------------");
    }

    private LocalDate getExpiryDate(NADRARecord n) {
        String expiryDate = n.getExpiryDate().trim();

        int day = Integer.parseInt(expiryDate.substring(0, 2).trim());
        int month = Integer.parseInt(expiryDate.substring(3, 5).trim());
        int year = Integer.parseInt(expiryDate.substring(6, 10).trim());

        return LocalDate.of(year, month, day);
    }

    public int changePassword(String password, String newPassword, String confirmPassword) {
        if (!password.equals(myEmployee.getPassword())) {
            return EmployeeMenu.PASSWORD_MISMATCH;
        } else if (newPassword.equals(myEmployee.getPassword())) {
            return EmployeeMenu.OLD_NEW_PASSWORDS_SAME;
        } else if (newPassword.length() < 6) {
            return EmployeeMenu.PASSWORD_TOO_SHORT;
        } else if (!confirmPassword.equals(newPassword)) {
            return EmployeeMenu.CONFIRM_PASSWORD_MISMATCH;
        }
        EmployeePersistence.writeToFile(myEmployee.getUsername(), newPassword);
        return 0;
    }


    // TODO: Remove after successful conversion
    public void changePassword() {
        Scanner input = new Scanner(System.in);
        String checkUsername;
        String checkPassword;

        //Enter current username
        System.out.print("Enter your username: ");
        while (true) {
            checkUsername = input.nextLine();
            if (checkUsername.equals(myEmployee.getUsername())) {
                break;
            } else System.out.print("Incorrect username! Enter again: ");
        }

        //Enter current password
        System.out.print("Enter your current password: ");
        while (true) {
            checkPassword = input.nextLine();
            if (checkPassword.equals(myEmployee.getPassword())) {
                break;
            } else System.out.print("Incorrect password! Enter again: ");
        }

        //Enter new password
        String newPassword;
        System.out.print("Enter new password: ");
        while (true) {
            newPassword = input.nextLine();
            if (newPassword.equals(myEmployee.getPassword())) {
                System.out.print("Old password is the same as new password! Please enter a different password :");
            } else if (newPassword.length() < 6) {
                System.out.print("Error! Password is too short! Enter again: ");
            } else break;
        }
        //Repeat new password
        String newPasswordRepeat;
        System.out.print("Enter new password again: ");
        while (true) {
            newPasswordRepeat = input.nextLine();
            if (newPasswordRepeat.equals(newPassword)) break;
            else System.out.print("Passwords do not match! Enter again: ");
        }
        EmployeePersistence.writeToFile(checkUsername, newPassword);

        //Display confirmation message
        System.out.println("Password updated successfully!");
    }

    void updateTariffTaxInfo(List<TariffTax> tariffs) {
        Scanner input = new Scanner(System.in);

        for (TariffTax tariffTax : tariffs) {
            System.out.println("Updating details for meter type: " + tariffTax.getMeterType() + ", customer type: " + tariffTax.getCustomerType());

            String choice;
            boolean flag;
            while (true) {
                System.out.println("Do you want to update tariffs for this type of customers? (Y/N)");
                choice = input.nextLine();
                if (choice.charAt(0) == 'Y' || choice.charAt(0) == 'y') {
                    flag = true;
                    break;
                } else if (choice.charAt(0) == 'N' || choice.charAt(0) == 'n') {
                    flag = false;
                    break;
                } else System.out.println("Invalid choice! ");
            }
            if (!flag) continue;


            System.out.print("Enter new regular unit price (current: " + tariffTax.getRegularUnitPrice() + "): ");
            double regularUnitPrice = Double.parseDouble(input.nextLine());
            tariffTax.setRegularUnitPrice(regularUnitPrice);

            System.out.print("Enter new peak hour unit price (current: " + ((tariffTax.getPeakHourUnitPrice() == null) ? "NULL for 1-phase meter" : tariffTax.getPeakHourUnitPrice()) + "): ");
            Double peakHourUnitPrice;
            String peakHourPrice = input.nextLine();
            try { //Try catch in case
                peakHourUnitPrice = Double.parseDouble(peakHourPrice);
            } catch (Exception e) {
                peakHourUnitPrice = null;
            }
            tariffTax.setPeakHourUnitPrice(peakHourUnitPrice);

            System.out.print("Enter new tax percentage (current: " + tariffTax.getTaxPercentage() + "): ");
            double taxPercentage = Double.parseDouble(input.nextLine());
            tariffTax.setTaxPercentage(taxPercentage);

            System.out.print("Enter new fixed charges (current: " + tariffTax.getFixedCharges() + "): ");
            double fixedCharges = Double.parseDouble(input.nextLine());
            tariffTax.setFixedCharges(fixedCharges);

        }
        System.out.println("Tariff details updated!\n");
    }

    public void updateCustomerInfo(ArrayList<Customer> customers) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the ID of the customer you want to update (-1 to print all customers): ");
        String customerID = scanner.nextLine();
        if (customerID.equals("-1")) {
            this.viewAllCustomers(customers);
            updateCustomerInfo(customers);
            return;
        }
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                try {
                    System.out.println("Updating details for Models.Customer ID: " + customer.getCustomerID());

                    // Update customer name
                    System.out.print("Enter new customer name (current: " + customer.getCustomerName() + "): ");
                    String customerName = scanner.nextLine();
                    if (!customerName.trim().isEmpty()) {
                        customer.setCustomerName(customerName);
                    }

                    // Update address
                    System.out.print("Enter new address (current: " + customer.getAddress() + "): ");
                    String address = scanner.nextLine();
                    if (!address.trim().isEmpty()) {
                        customer.setAddress(address);
                    }

                    // Update phone number
                    System.out.print("Enter new phone number (current: " + customer.getPhone() + "): ");
                    String phone = scanner.nextLine();
                    if (!phone.trim().isEmpty()) {
                        customer.setPhone(phone);
                    }

                    // Update commercial status
                    System.out.print("Is the customer commercial (true/false)? (current: " + customer.getIsCommercial() + "): ");
                    String isCommercial = scanner.nextLine();
                    if (!isCommercial.trim().isEmpty()) {
                        customer.setIsCommercial(Boolean.parseBoolean(isCommercial));
                    }

                    // Update phase type
                    System.out.print("Is the connection 3-phase (true/false)? (current: " + customer.getThreePhase() + "): ");
                    String isThreePhase = scanner.nextLine();
                    if (!isThreePhase.trim().isEmpty()) {
                        customer.setThreePhase(Boolean.parseBoolean(isThreePhase));
                    }

                    // Update regular units consumed
                    System.out.print("Enter new regular units consumed (current: " + customer.getRegularUnitsConsumed() + "): ");
                    String regularUnits = scanner.nextLine();
                    if (!regularUnits.trim().isEmpty()) {
                        customer.setRegularUnitsConsumed(Float.parseFloat(regularUnits));
                    }

                    // Update peak units consumed
                    System.out.print("Enter new peak units consumed (current: " + customer.getPeakUnitsConsumed() + "): ");
                    String peakUnits = scanner.nextLine();
                    if (!peakUnits.trim().isEmpty()) {
                        customer.setPeakUnitsConsumed(Float.parseFloat(peakUnits));
                    }

                    System.out.println("Models.Customer details updated successfully!\n");

                } catch (NumberFormatException e) {
                    System.err.println("Invalid input. Please enter numbers for numeric fields.");
                } catch (Exception e) {
                    System.err.println("An error occurred while updating customer details: " + e.getMessage());
                } finally {
//                    scanner.close();
                    System.out.println("Models.Customer details updated!\n");
                }
            }
        }
    }

}
