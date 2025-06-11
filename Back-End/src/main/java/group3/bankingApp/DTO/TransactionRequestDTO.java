package group3.bankingApp.DTO;

public class TransactionRequestDTO {

    private String senderIban;

    private String receiverIban;

    private double amount;

    private String description;

    public TransactionRequestDTO() {}

    public TransactionRequestDTO(String senderIban, String receiverIban, double amount, String description) {
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.description = description;
    }

    // Getters & setters
    public String getSenderIban() { return senderIban; }
    public void setSenderIban(String senderIban) {
        this.senderIban = senderIban;
    }

    public String getReceiverIban() { return receiverIban; }
    public void setReceiverIban(String receiverIban) {
        this.receiverIban = receiverIban;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
