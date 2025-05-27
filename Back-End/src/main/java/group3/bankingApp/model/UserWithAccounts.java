package group3.bankingApp.model;

import java.util.List;
import group3.bankingApp.model.User;
import group3.bankingApp.model.Account;


public class UserWithAccounts {
    private User user;
    private List<Account> accounts;

    public UserWithAccounts(User user, List<Account> accounts) {
        this.user = user;
        this.accounts = accounts;
    }

    public User getUser() {
        return user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}

