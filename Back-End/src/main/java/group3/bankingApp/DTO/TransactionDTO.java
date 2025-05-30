package group3.bankingApp.DTO;

import java.time.LocalDateTime;

public class TransactionDTO {
    private Integer transactionId;
    private String senderUsername;
    private String receiverUsername;
    private double amount;
    private String description;
    private LocalDateTime createdAt;

    // Getters & Setters
    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
