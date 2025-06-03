package group3.bankingApp.services;

import group3.bankingApp.model.ATMSession;
import group3.bankingApp.model.ATMTransaction;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.ATMTransactionStatus;
import group3.bankingApp.model.enums.ATMTransactionType;
import group3.bankingApp.repository.ATMSessionRepository;
import group3.bankingApp.repository.ATMTransactionRepository;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ATMService {

    private final ATMSessionRepository atmSessionRepository;
    private final ATMTransactionRepository atmTransactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public ATMService(ATMSessionRepository atmSessionRepository,
                      ATMTransactionRepository atmTransactionRepository,
                      AccountRepository accountRepository,
                      UserRepository userRepository) {
        this.atmSessionRepository = atmSessionRepository;
        this.atmTransactionRepository = atmTransactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public ATMSession startSession(Integer accountId) {
        ATMSession session = new ATMSession();
        session.setAccountId(accountId);
        session.setLoginTime(java.time.LocalDateTime.now());
        return atmSessionRepository.save(session);
    }

    public ATMTransaction withdraw(Integer sessionId, Integer accountId, Integer userId, int amount) {
        return performTransaction(sessionId, accountId, userId, amount, ATMTransactionType.WITHDRAWAL);
    }

    public ATMTransaction deposit(Integer sessionId, Integer accountId, Integer userId, int amount) {
        return performTransaction(sessionId, accountId, userId, amount, ATMTransactionType.DEPOSIT);
    }

    private ATMTransaction performTransaction(
            Integer sessionId,
            Integer accountId,
            Integer userId,
            int amount,
            ATMTransactionType type
    ) {
        ATMTransaction txn = new ATMTransaction();

        // Validate session
        Optional<ATMSession> sessionOpt = atmSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            txn.setReason("Invalid ATM session.");
            return atmTransactionRepository.save(txn);
        }
        txn.setSession(sessionOpt.get());

        // Validate account & user existence
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (accountOpt.isEmpty() || userOpt.isEmpty()) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            txn.setReason("Account or user not found.");
            return atmTransactionRepository.save(txn);
        }
        Account account = accountOpt.get();

        // Validate amount > 0
        if (amount <= 0) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            txn.setReason("Amount must be greater than zero.");
            return atmTransactionRepository.save(txn);
        }

        // Check sufficient funds if withdrawing
        double currentBalance = account.getBalance();
        if (type == ATMTransactionType.WITHDRAWAL && currentBalance < amount) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            txn.setReason("Insufficient funds.");
            return atmTransactionRepository.save(txn);
        }

        // Apply balance change
        double newBalance = (type == ATMTransactionType.WITHDRAWAL)
                ? currentBalance - amount
                : currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Record a successful transaction
        txn.setAmount(amount);
        txn.setType(type);
        txn.setStatus(ATMTransactionStatus.SUCCESS);
        txn = atmTransactionRepository.save(txn);

        return txn;
    }
}
