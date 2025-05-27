package group3.bankingApp.services;
import java.util.Random;
import java.awt.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account findById(int id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public Account findUsersAccounts(int userId) {
        return accountRepository.findByUserId(userId).orElseThrow();
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public void createDefaultAccountsForUser(Integer userId) {
    createAccount(userId, AccountType.CHECKING);
    createAccount(userId, AccountType.SAVING);
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

    // Example IBAN generator
    private String generateIban() {
        String prefix = "NL91ABNA";
        String numbers = String.format("%010d", new Random().nextInt(1_000_000_000));
        return prefix + numbers;
    }



}
