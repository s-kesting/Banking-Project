package group3.bankingApp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import group3.bankingApp.model.enums.ATMTransactionType;
import group3.bankingApp.model.enums.ATMTransactionStatus;

@Entity
@Table(name = "ATMTransaction")
public class ATMTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessionID", referencedColumnName = "sessionID")
    private ATMSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ATMTransactionType type;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ATMTransactionStatus status = ATMTransactionStatus.SUCCESS;

    @Column
    private String reason;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    // -- Getters & Setters --

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ATMSession getSession() {
        return session;
    }

    public void setSession(ATMSession session) {
        this.session = session;
    }

    public ATMTransactionType getType() {
        return type;
    }

    public void setType(ATMTransactionType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public ATMTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(ATMTransactionStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
