package group3.bankingApp.dto;

import java.time.Instant;

public class ATMSessionResponseDTO {

    private Integer sessionId;
    private Integer accountId;
    private Instant loginTime;

    public ATMSessionResponseDTO() { }

    public ATMSessionResponseDTO(Integer sessionId, Integer accountId, Instant loginTime) {
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.loginTime = loginTime;
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

    public Instant getLoginTime() {
        return loginTime;
    }
    public void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }
}
