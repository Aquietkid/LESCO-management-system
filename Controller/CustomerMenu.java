package Controller;

import Model.*;
import View.BillEstimator;
import View.BillViewer;
import View.CNICUpdateScreen;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomerMenu extends Menu {

    private final Customer myCustomer;

    public CustomerMenu(User customer) {
        this.message = """
                Customer Menu
                1. View Bills
                2. Estimate Upcoming Bill
                3. Update CNIC Expiry
                4. Exit
                """;
        this.myCustomer = (Customer) customer;
    }

    @Override
    public void displayMenu() {
        System.out.println(this.message);
    }

    public void runMenu(Scanner input, ArrayList<TariffTax> tariffs, ArrayList<NADRARecord> NADRARecords, ArrayList<BillingRecord> billingRecords) {
        int choice;
        do {
            this.displayMenu();
            System.out.println("Enter your choice");
            choice = input.nextInt();
            if (choice > 4 || choice < 0) {
                System.out.println("Invalid choice!");
            } else if (choice == 4) {
                return;
            } else this.executeMenuTask(choice, tariffs, NADRARecords, billingRecords);
        } while (true);
    }

    public void runMenuGUI() {
        View.CustomerMenu customerMenu = new View.CustomerMenu(this);
    }

    public void executeMenuTask(int choice, JFrame customerMenu) { //FOR GUI
        switch (choice) {
            case View.CustomerMenu.VIEW_BILL:
                viewBills(MasterPersistence.getInstance().getBillingRecords(), customerMenu);
                break;
            case View.CustomerMenu.ESTIMATE_UPCOMING_BILL:
                estimateUpcomingBills(MasterPersistence.getInstance().getTariffTaxes(), customerMenu);
                break;
            case View.CustomerMenu.UPDATE_CNIC_EXPIRY:
                // TODO: create GUI version
                updateCNICExpiry(MasterPersistence.getInstance().getNadraRecords(), customerMenu);
                break;
            default:
                JOptionPane.showMessageDialog(customerMenu, "Invalid choice!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCNICExpiry(ArrayList<NADRARecord> nadraRecords, JFrame customerMenu) {
        CNICUpdateScreen cnicUpdateScreen = new CNICUpdateScreen(nadraRecords, myCustomer);

    }

    private void viewBills(ArrayList<BillingRecord> billingRecords, JFrame customerMenu) {
        BillViewer billViewer = new BillViewer();
        for (BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(myCustomer.getUsername())) {

                String row = br.toFileString();
                String[] rowData = row.trim().split(",");
                billViewer.addRow(rowData);
            }
        }
    }

    public void estimateUpcomingBills(ArrayList<TariffTax> tariffTaxes, JFrame customerMenu) {
        TariffTax myTariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);
        boolean isPeak = myTariffTax.getPeakHourUnitPrice() != null;
        new BillEstimator(myCustomer.getRegularUnitsConsumed(), myCustomer.getPeakUnitsConsumed(), isPeak, myTariffTax);
    }

    // TODO: remove upon successful refactoring of new method
    public void executeMenuTask(int choice, final ArrayList<TariffTax> tariffs, ArrayList<NADRARecord> NADRARecords, ArrayList<BillingRecord> billingRecords) {
        switch (choice) {
            case 1:
                viewBills(billingRecords);
                break;
            case 2:
                estimateUpcomingBills(tariffs);
                break;
            case 3:
                updateCNICExpiry(NADRARecords);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    public void viewBills(ArrayList<BillingRecord> billingRecords) {
        System.out.println("Viewing bills");
        for (BillingRecord br : billingRecords) {
            if (br.getCustomerID().equals(myCustomer.getUsername())) {
                System.out.println(br);
            }
        }
    }

    public void estimateUpcomingBills(ArrayList<TariffTax> tariffTaxes) {
        Scanner input = new Scanner(System.in);
        float currentRegularUnits;
        float currentPeakUnits = 0.0f;
        while (true) {
            try {
                System.out.println("Enter the current regular units reading: ");
                currentRegularUnits = input.nextFloat();
                if (currentRegularUnits > 0.0f) {
                    break;
                } else if (currentRegularUnits < myCustomer.getRegularUnitsConsumed()) {
                    System.out.println("Current reading cannot be less than previous reading!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Value must be a number! ");
            }
        }

        TariffTax myTariffTax = TariffTax.getTariffTax(tariffTaxes, myCustomer);

        if (myTariffTax.getPeakHourUnitPrice() != null) {
            while (true) {
                try {

                    System.out.println("Enter the current peak hour units reading: ");
                    currentPeakUnits = input.nextFloat();
                    if (currentPeakUnits > 0.0f) {
                        break;
                    } else if (currentPeakUnits < myCustomer.getPeakUnitsConsumed()) {
                        System.out.println("Current peak hour reading cannot be less than previous peak hour reading!");
                    } else System.out.println("Incorrect reading! Please enter again: ");
                } catch (NumberFormatException e) {
                    System.out.println("Value must be a number! ");
                }
            }
        }

        float costOfElectricity = (float) (currentRegularUnits * myTariffTax.getRegularUnitPrice()) + (float) ((myTariffTax.getPeakHourUnitPrice() != null) ? (currentPeakUnits * myTariffTax.getPeakHourUnitPrice()) : 0.0);
        System.out.println("Cost of electricity = " + costOfElectricity);
        float salesTaxAmount = (float) (costOfElectricity * (myTariffTax.getTaxPercentage() / 100));
        System.out.println("Sales tax amount  = " + salesTaxAmount);
        float fixedCharges = (float) myTariffTax.getFixedCharges();
        System.out.println("Fixed charges = " + fixedCharges);
        float totalBillingAmount = costOfElectricity + salesTaxAmount + fixedCharges;
        System.out.println("Total Billing Amount: " + totalBillingAmount);
    }

    public void updateCNICExpiry(ArrayList<NADRARecord> nadraRecords) {
        Scanner input = new Scanner(System.in);
        String CNIC;
        String currentExpiry = null;
        NADRARecord myNADRARecord = null;

        while (true) {
            System.out.println("Enter your CNIC: ");
            CNIC = input.nextLine().trim();
            if (CNIC.length() != 13) {
                System.out.println("CNIC must be 13-digits long!");
            } else if (CNIC.contains("-")) {
                System.out.println("Please enter CNIC without dashes!");
            } else if (CNIC.equals(myCustomer.getCNIC())) {
                break;
            } else System.out.println("Your CNIC does not match the entered value. ");
        }

        for (NADRARecord nr : nadraRecords) {
            if (nr.getCNIC().equals(CNIC)) {
                currentExpiry = nr.getExpiryDate();
                myNADRARecord = nr;
                break;
            }
        }

        if (myNADRARecord != null && currentExpiry != null) {

            while (true) {
                int day;
                int month;
                int year;
                LocalDate now = LocalDate.now();
                try {
                    while (true) {
                        System.out.print("Enter the year (YYYY): ");
                        year = Integer.parseInt(input.nextLine());
                        if (year < 1900 || year > 2100) {
                            System.out.println("Invalid year. Please enter a value between 1900 and 2100.");
                            continue;
                        }
                        if (now.getYear() > year) { //Year is in the future
                            System.out.println("Expiry date can not be in past!");
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
                        if ((year == now.getYear()) && month < now.getMonthValue()) { //Month is in the past
                            System.out.println("Expiry date can not be in past!");
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
                        if ((year == now.getYear()) && month == now.getMonthValue() && day < now.getDayOfMonth()) { //Day is in the past
                            System.out.println("Expiry date can not be in past!");
                        }
                        break;
                    }


                    myNADRARecord.setExpiryDate(String.format("%02d-%02d-%04d", day, month, year));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}