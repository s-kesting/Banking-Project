package group3.bankingApp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group3.bankingApp.model.Account;
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

    // public Page<Account> findUsersAccounts(){
    // return accountRepository.findBy().t
    // }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

}
