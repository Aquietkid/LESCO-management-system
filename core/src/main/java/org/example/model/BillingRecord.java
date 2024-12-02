package org.example.model;

public class BillingRecord {
    private final String customerID;
    private final String billingMonth;
    private final float currentMeterReadingRegular;
    private final float currentMeterReadingPeak;
    private final String readingEntryDate;
    private final float costOfElectricity;
    private final float salesTaxAmount;
    private final float fixedCharges;
    private final float totalBillingAmount;
    private final String dueDate;
    private boolean billPaidStatus;
    private String billPaymentDate;

    public BillingRecord(String customerID, String billingMonth, float currentMeterReadingRegular, float currentMeterReadingPeak, String readingEntryDate, float costOfElectricity, float salesTaxAmount, float fixedCharges, float totalBillingAmount, String dueDate, boolean billPaidStatus, String billPaymentDate) {
        this.customerID = customerID;
        this.billingMonth = billingMonth;
        this.currentMeterReadingRegular = currentMeterReadingRegular;
        this.currentMeterReadingPeak = currentMeterReadingPeak;
        this.readingEntryDate = readingEntryDate;
        this.costOfElectricity = costOfElectricity;
        this.salesTaxAmount = salesTaxAmount;
        this.fixedCharges = fixedCharges;
        this.totalBillingAmount = totalBillingAmount;
        this.dueDate = dueDate;
        this.billPaidStatus = billPaidStatus;
        this.billPaymentDate = billPaymentDate;
    }

    public BillingRecord(String customerID, String billingMonth, float currentMeterReadingRegular, float currentMeterReadingPeak, String readingEntryDate, float costOfElectricity, float salesTaxAmount, float fixedCharges, float totalBillingAmount, String dueDate) {
        this.customerID = customerID;
        this.billingMonth = billingMonth;
        this.currentMeterReadingRegular = currentMeterReadingRegular;
        this.currentMeterReadingPeak = currentMeterReadingPeak;
        this.readingEntryDate = readingEntryDate;
        this.costOfElectricity = costOfElectricity;
        this.salesTaxAmount = salesTaxAmount;
        this.fixedCharges = fixedCharges;
        this.totalBillingAmount = totalBillingAmount;
        this.dueDate = dueDate;
        this.billPaidStatus = false;
        this.billPaymentDate = "";
    }

    public String toFileString() {
        return customerID + "," + billingMonth + "," + currentMeterReadingRegular + "," + currentMeterReadingPeak + "," + readingEntryDate + "," + costOfElectricity + "," + salesTaxAmount + "," + fixedCharges + "," + totalBillingAmount + "," + dueDate + "," + billPaidStatus + "," + billPaymentDate + "\n";
    }

    @Override
    public String toString() {
        return "Models.Customer ID: " + customerID + ", Billing Month: " + billingMonth + ", Regular Reading: " + currentMeterReadingRegular + ", Peak Reading: " + currentMeterReadingPeak + ", Reading Entry Date: " + readingEntryDate + ", Cost of Electricity: " + costOfElectricity + ", Sales Tax: " + salesTaxAmount + ", Fixed Charges: " + fixedCharges + ", Total Billing: " + totalBillingAmount + ", Due Date: " + dueDate + ", Status: " + billPaidStatus + ", Payment Date: " + billPaymentDate;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public float getCurrentMeterReadingRegular() {
        return currentMeterReadingRegular;
    }

    public float getCurrentMeterReadingPeak() {
        return currentMeterReadingPeak;
    }

    public float getTotalBillingAmount() {
        return totalBillingAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean getBillPaidStatus() {
        return billPaidStatus;
    }

    public String getReadingEntryDate() {
        return readingEntryDate;
    }

    public float getCostOfElectricity() {
        return costOfElectricity;
    }

    public float getSalesTaxAmount() {
        return salesTaxAmount;
    }

    public float getFixedCharges() {
        return fixedCharges;
    }

    public boolean isBillPaidStatus() {
        return billPaidStatus;
    }

    public String getBillPaymentDate() {
        return billPaymentDate;
    }

    public void setBillPaidStatus(boolean billPaidStatus) {
        this.billPaidStatus = billPaidStatus;
    }

    public void setBillPaymentDate(String billPaymentDate) {
        this.billPaymentDate = billPaymentDate;
    }


}
