package group3.bankingApp.model;

import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNTID")
    private Integer accountId;

    @Column(name = "USERID", nullable = false)
    private Integer userId; // FK to users.userId

    @Column(nullable = false, unique = true, length = 34)
    private String IBAN;

    @Column(nullable = false)
    private double balance;

    @Column(name = "DAILY_LIMIT", nullable = false)
    private double dailyLimit;

    @Column(name = "ABSOLUTE_LIMIT", nullable = false)
    private double absoluteLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "VERIFY_ACCOUNT", nullable = false)
    private VerifyStatus verifyAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE", nullable = false)
    private AccountType accountType;

    // -- Getters & Setters --

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public double getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(double absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public VerifyStatus getVerifyAccount() {
        return verifyAccount;
    }

    public void setVerifyAccount(VerifyStatus verifyAccount) {
        this.verifyAccount = verifyAccount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
