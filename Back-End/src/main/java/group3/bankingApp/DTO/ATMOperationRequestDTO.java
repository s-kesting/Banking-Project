package group3.bankingApp.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ATMOperationRequestDTO {

    @NotNull(message = "sessionId is required")
    private Integer sessionId;

    @NotNull(message = "accountId is required")
    private Integer accountId;

    @NotNull(message = "amount is required")
    @Min(value = 1, message = "amount must be at least 1")
    private Integer amount;

    public ATMOperationRequestDTO() { }

    public ATMOperationRequestDTO(Integer sessionId, Integer accountId, Integer amount) {
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.amount    = amount;
    }

    public Integer getSessionId() {
        return sessionId;
    }
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
