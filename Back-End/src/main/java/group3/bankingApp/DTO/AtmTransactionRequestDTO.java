package group3.bankingApp.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AtmTransactionRequestDTO {
    @NotNull
    private Integer accountId;

    @NotNull 
    @DecimalMin("0.01")
    private Double amount;

    // getters/setters
    public Integer getAccountId() { 
        return accountId; 
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId; 
    }

    public Double getAmount() { 
        return amount; 
    }

    public void setAmount(Double amount) { 
        this.amount = amount; 
    }
}
