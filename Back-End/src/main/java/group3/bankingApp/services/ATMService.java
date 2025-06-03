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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ATMService {

    private final ATMSessionRepository      atmSessionRepository;
    private final ATMTransactionRepository  atmTransactionRepository;
    private final AccountRepository         accountRepository;
    private final UserRepository            userRepository;

    public ATMService(
            ATMSessionRepository atmSessionRepository,
            ATMTransactionRepository atmTransactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository
    ) {
        this.atmSessionRepository     = atmSessionRepository;
        this.atmTransactionRepository = atmTransactionRepository;
        this.accountRepository        = accountRepository;
        this.userRepository           = userRepository;
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
        // 1) Lookup session or throw 400
        ATMSession session = atmSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid ATM session."
            ));

        // 2) Create and attach session
        ATMTransaction txn = new ATMTransaction();
        txn.setSession(session);
        txn.setType(type);
        txn.setAmount(amount);

        // 3) Lookup account or throw 400
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Account not found."
            ));

        // 4) Lookup user or throw 400
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "User not found."
            ));

        // 5) Verify ownership
        if (!account.getUserId().equals(user.getUserId())) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            return atmTransactionRepository.save(txn);
        }

        // 6) Validate amount > 0
        if (amount <= 0) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            return atmTransactionRepository.save(txn);
        }

        // 7) Check sufficient funds if withdrawing
        double currentBalance = account.getBalance();
        if (type == ATMTransactionType.WITHDRAWAL && currentBalance < amount) {
            txn.setStatus(ATMTransactionStatus.FAILED);
            return atmTransactionRepository.save(txn);
        }

        // 8) Apply balance change
        double newBalance = (type == ATMTransactionType.WITHDRAWAL)
                ? (currentBalance - amount)
                : (currentBalance + amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // 9) Record a successful transaction
        txn.setStatus(ATMTransactionStatus.SUCCESS);
        return atmTransactionRepository.save(txn);
    }
}
