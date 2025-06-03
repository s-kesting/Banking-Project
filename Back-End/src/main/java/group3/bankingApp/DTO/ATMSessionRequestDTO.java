package group3.bankingApp.dto;

import jakarta.validation.constraints.NotNull;

public class ATMSessionRequestDTO {

    @NotNull(message = "accountId is required")
    private Integer accountId;

    public ATMSessionRequestDTO() { }

    public ATMSessionRequestDTO(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
