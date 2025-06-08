// src/main/java/group3/bankingApp/DTO/UserWithAccountsDTO.java
package group3.bankingApp.DTO;

import group3.bankingApp.model.User;
import group3.bankingApp.model.Account;

import java.util.List;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;

public class UserWithAccountsDTO {
    private Integer accountId;
    private Integer userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String bsn;
    private VerifyStatus verifyUser;
    private AccountType accountType;
    private String IBAN;
    private double balance;
    private double dailyLimit;
    private double absoluteLimit;
    private VerifyStatus verifyAccount;

    public UserWithAccountsDTO(
    Integer userId, String username, String email, String phoneNumber, String bsn,
    VerifyStatus verifyUser, AccountType accountType, String IBAN,
    Double balance, Double dailyLimit, Double absoluteLimit, VerifyStatus verifyAccount, Integer accountId
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bsn = bsn;
        this.verifyUser = verifyUser;
        this.accountType = accountType;
        this.IBAN = IBAN;
        this.balance = balance != null ? balance : 0.0;
        this.dailyLimit = dailyLimit != null ? dailyLimit : 0.0;
        this.absoluteLimit = absoluteLimit != null ? absoluteLimit : 0.0;
        this.verifyAccount = verifyAccount;
        this.accountId = accountId;
    }


    // Getters only (optional: add setters)
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBsn() { return bsn; }
    public VerifyStatus getVerifyUser() { return verifyUser; }
    public AccountType getAccountType() { return accountType; }
    public String getIBAN() { return IBAN; }
    public double getBalance() { return balance; }
    public double getDailyLimit() { return dailyLimit; }
    public double getAbsoluteLimit() { return absoluteLimit; }
    public VerifyStatus getVerifyAccount() { return verifyAccount; }
    public Integer getAccountId() { return accountId; }
}