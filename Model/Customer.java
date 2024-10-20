package Model;/*

/**
 * @author Ali
 */
public class Customer implements User {

    protected final String customerID; //4-digit number
    protected final String CNIC; //13-digit without dashes
    protected String customerName;
    protected String address;
    protected String phone;
    protected Boolean isCommercial; //true for commercial, false for domestic 
    protected Boolean isThreePhase; //true for 3-phase, false for 1-phase
    protected float regularUnitsConsumed;
    protected float peakUnitsConsumed;
    private final String connectionDate;

    public Customer(String customerID, String CNIC, String customerName, String address, String phone, Boolean isCommercial, Boolean isThreePhase, String connectionDate) {
        this.customerID = customerID;
        this.CNIC = CNIC;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.isCommercial = isCommercial;
        this.isThreePhase = isThreePhase;
        this.connectionDate = connectionDate;
        this.regularUnitsConsumed = 0;
        this.peakUnitsConsumed = 0;
    }

    public Customer(String customerID, String CNIC, String customerName, String address, String phone, Boolean isCommercial, Boolean isThreePhase, String connectionDate, float regularUnitsConsumed, float peakUnitsConsumed) {
        this(customerID, CNIC, customerName, address, phone, isCommercial, isThreePhase, connectionDate);
        this.regularUnitsConsumed = regularUnitsConsumed;
        this.peakUnitsConsumed = peakUnitsConsumed;
    }

    public static User getMatchingCustomer(String username, String password) {
        for(Customer c: MasterPersistence.getInstance().getCustomers()) {
            if(c.getCustomerID().equals(username) && c.getPassword().equals(password)) {
                return c;
            }
        }
        return null;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCNIC() {
        return CNIC;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsCommercial() {
        return isCommercial;
    }

    public void setIsCommercial(Boolean isCommercial) {
        this.isCommercial = isCommercial;
    }

    public Boolean getThreePhase() {
        return isThreePhase;
    }

    public void setThreePhase(Boolean threePhase) {
        isThreePhase = threePhase;
    }

    public String getConnectionDate() {
        return connectionDate;
    }

    public float getRegularUnitsConsumed() {
        return regularUnitsConsumed;
    }

    public void setRegularUnitsConsumed(float regularUnitsConsumed) {
        this.regularUnitsConsumed = regularUnitsConsumed;
    }

    public float getPeakUnitsConsumed() {
        return peakUnitsConsumed;
    }

    public void setPeakUnitsConsumed(float peakUnitsConsumed) {
        this.peakUnitsConsumed = peakUnitsConsumed;
    }

    @Override
    public String toString() {
        try {
            return this.getCustomerID() + " " + this.getCNIC() + " " + this.getCustomerName() + " " + this.getAddress() + " " + this.getPhone() + " " + ((this.getIsCommercial()) ? "Commercial" : "Domestic") + " " + ((this.getThreePhase()) ? "3-phase" : "1-phase") + " " + this.getConnectionDate() + " " + this.getRegularUnitsConsumed() + " " + this.getPeakUnitsConsumed();
        } catch (Exception e) {
            return "NULL";
        }
    }

    public String toFileString() {
        return this.getCustomerID() + "," + this.getCNIC() + "," + this.getCustomerName() + "," + this.getAddress() + "," + this.getPhone() + "," + ((this.getIsCommercial()) ? "C" : "D") + "," + ((this.getThreePhase()) ? "3" : "1") + "," + this.getConnectionDate() + "," + this.getRegularUnitsConsumed() + "," + this.getPeakUnitsConsumed() + "\n";
    }

    @Override
    public String getUsername() {
        return this.getCustomerID();
    }

    @Override
    public String getPassword() {
        return this.getCNIC();
    }
}