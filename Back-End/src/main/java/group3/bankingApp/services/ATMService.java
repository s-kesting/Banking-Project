package group3.bankingApp.services;

import group3.bankingApp.model.ATMSession;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.repository.ATMRepository;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ATMService {

    private final ATMRepository atmRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ATMService(ATMRepository atmRepository,
                      AccountRepository accountRepository,
                      TransactionRepository transactionRepository) {
        this.atmRepository = atmRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ATMSession startSession(Integer accountId) {
        ATMSession session = new ATMSession();
        session.setAccountId(accountId);
        session.setLoginTime(LocalDateTime.now());
        return atmRepository.save(session);
    }

    public Transaction transfer(Integer sessionId, Integer targetAccountId, double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        ATMSession session = atmRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session ID."));

        Integer senderAccountId = session.getAccountId();

        if (senderAccountId.equals(targetAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account sender = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found."));

        Account receiver = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient account not found."));

        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(senderAccountId);
        transaction.setReceiverAccount(targetAccountId);
        transaction.setAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }
}
