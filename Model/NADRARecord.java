package Model;

public class NADRARecord {
    private final String CNIC;
    private final String issuanceDate;  // Format: DD/MM/YYYY
    private String expiryDate;    // Format: DD/MM/YYYY

    public NADRARecord(String CNIC, String issuanceDate, String expiryDate) {
        this.CNIC = CNIC;
        this.issuanceDate = issuanceDate;
        this.expiryDate = expiryDate;
    }

    public String getCNIC() {
        return CNIC;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return CNIC + "," + issuanceDate + "," + expiryDate;
    }
}
