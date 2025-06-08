package group3.bankingApp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTIONID", nullable = false)
    private Integer transactionId;

    @Column(name = "SENDER_ACCOUNT", nullable = false)
    private Integer senderAccount; // FK to accounts.accountId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ACCOUNT", referencedColumnName = "ACCOUNTID", insertable = false, updatable = false)
    private Account sender;

    @Column(name = "RECEIVER_ACCOUNT", nullable = false)
    private Integer receiverAccount; // FK to accounts.accountId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_ACCOUNT", referencedColumnName = "ACCOUNTID", insertable = false, updatable = false)
    private Account receiver;

    @Column(nullable = false)
    private double amount;

    @Column(length = 255)
    private String description;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    // -- Getters & Setters --

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Integer senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Integer getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Integer receiverAccount) {
        this.receiverAccount = receiverAccount;
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

        public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

}
