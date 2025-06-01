package group3.bankingApp.DTO;

public class EmployeeTransferRequest {
    private String senderIBAN;
    private String receiverIBAN;
    private double amount;
    private String description;

    // Constructor (optional)
    public EmployeeTransferRequest() {
    }

    public EmployeeTransferRequest(String senderIBAN, String receiverIBAN, double amount, String description) {
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public String getSenderIBAN() {
        return senderIBAN;
    }

    public void setSenderIBAN(String senderIBAN) {
        this.senderIBAN = senderIBAN;
    }

    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    public void setReceiverIBAN(String receiverIBAN) {
        this.receiverIBAN = receiverIBAN;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
