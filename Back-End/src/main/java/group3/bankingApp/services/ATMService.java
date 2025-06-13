// src/main/java/group3/bankingApp/services/ATMService.java
package group3.bankingApp.services;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.TransactionType;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import group3.bankingApp.repository.UserRepository;

@Service
public class ATMService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository     accountRepository;
    private final UserRepository        userRepository;

    public ATMService(
        TransactionRepository transactionRepository,
        AccountRepository accountRepository,
        UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository     = accountRepository;
        this.userRepository        = userRepository;
    }

    public TransactionDTO deposit(Integer accountId, Integer userId, double amount) {
        Transaction txn = performTransaction(accountId, userId, amount, TransactionType.DEPOSIT);
        return mapToDto(txn);
    }

    public TransactionDTO withdraw(Integer accountId, Integer userId, double amount) {
        Transaction txn = performTransaction(accountId, userId, amount, TransactionType.WITHDRAW);
        return mapToDto(txn);
    }

    private Transaction performTransaction(
        Integer accountId,
        Integer userId,
        double amount,
        TransactionType type
    ) {
        User user = findUserOrThrow(userId);
        Account account = findAccountOrThrow(accountId);
        validateOwnership(user, account);
        validateAmount(amount);
        if (type == TransactionType.WITHDRAW) {
            validateFunds(account, amount);
        }

        updateAccountBalance(account, amount, type);
        return saveTransaction(accountId, amount, type);
    }

    private TransactionDTO mapToDto(Transaction txn) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(txn.getTransactionId());
        dto.setTransactionType(txn.getTransactionType());
        dto.setAmount(txn.getAmount());
        dto.setDescription(txn.getDescription());
        dto.setCreatedAt(txn.getCreatedAt());

        // resolve usernames
        String sender = "ATM";
        if (txn.getSenderAccount() != null) {
            Account acct = accountRepository.findById(txn.getSenderAccount()).orElse(null);
            if (acct != null) {
                sender = userRepository.findById(acct.getUserId())
                          .map(User::getUsername)
                          .orElse("ATM");
            }
        }
        dto.setSenderUsername(sender);

        String receiver = "ATM";
        if (txn.getReceiverAccount() != null) {
            Account acct = accountRepository.findById(txn.getReceiverAccount()).orElse(null);
            if (acct != null) {
                receiver = userRepository.findById(acct.getUserId())
                            .map(User::getUsername)
                            .orElse("ATM");
            }
        }
        dto.setReceiverUsername(receiver);

        return dto;
    }

    private User findUserOrThrow(Integer userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User not found."
        ));
}

    private Account findAccountOrThrow(Integer accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Account not found."
            ));
    }

    private void validateOwnership(User user, Account account) {
        if (!account.getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "User does not own this account."
            );
        }
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Amount must be greater than zero."
            );
        }
    }

    private void validateFunds(Account account, double amount) {
        if (account.getBalance() < amount) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Insufficient funds."
            );
        }
    }

    private void updateAccountBalance(Account account, double amount, TransactionType type) {
        double newBalance = (type == TransactionType.WITHDRAW)
            ? account.getBalance() - amount
            : account.getBalance() + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private Transaction saveTransaction(Integer accountId, double amount, TransactionType type) {
        Transaction txn = new Transaction();
        txn.setCreatedAt(LocalDateTime.now());
        txn.setTransactionType(type);
        txn.setAmount(amount);
        txn.setSenderAccount(type == TransactionType.WITHDRAW ? accountId : null);
        txn.setReceiverAccount(type == TransactionType.DEPOSIT ? accountId : null);
        txn.setDescription("ATM " + type);
        return transactionRepository.save(txn);
    }
}
