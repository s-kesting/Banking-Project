package group3.bankingApp.services;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.DTO.AccountSummaryDTO;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findByUserIdAndAccountType(int userId, AccountType accountType) {
        return accountRepository.findByUserIdAndAccountType(userId, accountType);
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

    public List<AccountSummaryDTO> getAccountSummaries(Integer userId) {
        return accountRepository.findByUserId(userId).stream()
            .map(acc -> new AccountSummaryDTO(
                acc.getAccountId(),
                acc.getBalance(),
                acc.getAccountType().toString()
            ))
            .collect(Collectors.toList());
    }

}
