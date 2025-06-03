package group3.bankingApp.dto;

public class AccountSummaryDTO {

    private Integer accountId;
    private double  balance;
    private String  accountType;

    public AccountSummaryDTO() { }

    public AccountSummaryDTO(Integer accountId, double balance, String accountType) {
        this.accountId   = accountId;
        this.balance     = balance;
        this.accountType = accountType;
    }

    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
