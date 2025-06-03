package group3.bankingApp.dto;

import group3.bankingApp.model.enums.ATMTransactionType;
import group3.bankingApp.model.enums.ATMTransactionStatus;

public class ATMTransactionResponseDTO {

    private Integer               transactionId;
    private Integer               sessionId;
    private ATMTransactionType    type;
    private Integer               amount;
    private ATMTransactionStatus  status;

    public ATMTransactionResponseDTO() { }

    public ATMTransactionResponseDTO(
        Integer transactionId,
        Integer sessionId,
        ATMTransactionType type,
        Integer amount,
        ATMTransactionStatus status
    ) {
        this.transactionId = transactionId;
        this.sessionId     = sessionId;
        this.type          = type;
        this.amount        = amount;
        this.status        = status;
    }

    public Integer getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getSessionId() {
        return sessionId;
    }
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
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
}
