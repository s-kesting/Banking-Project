package group3.bankingApp.services;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import group3.bankingApp.DTO.AccountUpdateDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Page<Account> findByUserIdAndAccountType(int userId, AccountType accountType, Pageable pageable) {
        return accountRepository.findByUserIdAndAccountTypeAndVerifyAccount(userId, accountType, VerifyStatus.ACTIVE,
                pageable);
    }

    public List<Account> findByUserIdAndAccountType(int userId, AccountType accountType) {
        return accountRepository.findByUserIdAndAccountType(userId, accountType);
    }

    public boolean checkIfUserHasPendingAccount(int userId) {
        return this.accountRepository.existsByUserIdAndVerifyAccount(userId, VerifyStatus.PENDING);
    }

    public Page<Account> findByUserIdAndVerifyStatus(int userId, VerifyStatus verifyStatus, Pageable pageable) {

        return this.accountRepository.findByUserIdAndVerifyAccount(userId, verifyStatus, pageable);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account findById(int id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public List<Account> findUsersAccounts(int userId) {
        // return accountRepository.findByUserId(userId);
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new NoSuchElementException("No accounts found for user ID: " + userId);
        }
        return accounts;
    }

    public Page<Account> findUsersAccounts(int userId, Pageable pageable) {
        // return accountRepository.findByUserId(userId);
        Page<Account> accounts = accountRepository.findByUserId(userId, pageable);
        if (accounts.isEmpty()) {
            throw new NoSuchElementException("No accounts found for user ID: " + userId);
        }
        return accounts;
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public void newAccountRequest(int userId, AccountType accountType) {
        createAccount(userId, accountType);
    }

    public void createDefaultAccountsForUser(Integer userId) {
        createAccount(userId, AccountType.Checking);
        createAccount(userId, AccountType.Saving);
    }

    private void createAccount(Integer userId, AccountType type) {
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountType(type);
        account.setBalance(0.00);
        account.setDailyLimit(1000);
        account.setAbsoluteLimit(-100);
        account.setVerifyAccount(VerifyStatus.PENDING);
        account.setIBAN(generateIban());
        accountRepository.save(account);
    }

    // IBAN generator
    public String generateIban() {
        String bankCode = "INHO0";
        Random random = new Random();
        String iban;

        do {
            String checkDigits = String.format("%02d", random.nextInt(100)); // Random 2 digits
            String accountNumber = String.format("%09d", random.nextInt(1_000_000_000)); // 9 digits
            iban = "NL" + checkDigits + bankCode + accountNumber;
        } while (accountRepository.existsByIBAN(iban)); // ensure uniqueness

        return iban;
    }

    ////////////// Robben ----- Update Account Status and
    ////////////// information///////////////////
    public void updateAccount(Integer accountId, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Optional: add business rule validation here

        account.setAbsoluteLimit(dto.getAbsoluteLimit());
        account.setDailyLimit(dto.getDailyLimit());
        account.setVerifyAccount(VerifyStatus.valueOf(dto.getVerifyAccount()));

        accountRepository.save(account);
    }

}
