package group3.bankingApp.services;

import org.springframework.stereotype.Service;

import group3.bankingApp.repository.AccountRepository;

@Service
class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
