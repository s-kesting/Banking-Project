
package group3.bankingApp.DTO;

import java.time.LocalDateTime;

public class TransactionJoinDTO {
    private Integer transactionId;
    private String senderUsername;
    private LocalDateTime createdAt;
    private String senderIban;
    private String receiverIban;
    private String receiverUsername;
    private double amount;
    private String description;

    public TransactionJoinDTO(Integer transactionId, String senderOwnerUsername, String senderIBAN, String receiverIBAN,
            String receiverOwnerUsername, double amount, String description, LocalDateTime createdAt) {
        this.description = description;
        this.amount = amount;
        this.receiverUsername = receiverOwnerUsername;
        this.receiverIban = receiverIBAN;
        this.senderIban = senderIBAN;
        this.createdAt = createdAt;
        this.senderUsername = senderOwnerUsername;
        this.transactionId = transactionId;
    }

    // Getters & Setters
    public String getSenderIban() {
        return this.senderIban;
    }

    public void setSenderIban(String iban) {
        this.senderIban = iban;
    }

    public String getReceiverIban() {
        return this.receiverIban;
    }

    public void setReceiverIban(String iban) {
        this.receiverIban = iban;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
