package group3.bankingApp.DTO;

public class AccountUpdateDTO {
     private Double absoluteLimit;
    private Double dailyLimit;
    private String verifyAccount; // Expect values like "ACTIVE", "REJECTED", etc.

    // Getters and Setters
    public Double getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(Double absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public Double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public String getVerifyAccount() {
        return verifyAccount;
    }

    public void setVerifyAccount(String verifyAccount) {
        this.verifyAccount = verifyAccount;
    }
}
