package group3.bankingApp.services;
import java.util.List;

import org.springframework.stereotype.Service;

import group3.bankingApp.model.Transaction;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(AccountRepository accountRepository,TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction CreateTransaction(Transaction transaction){
        //sender
        accountRepository.withdraw(transaction.getSenderAccount(), transaction.getAmount());
        //receiver
        accountRepository.deposit(transaction.getReceiverAccount(),transaction.getAmount());
        //save transaction record
        return transactionRepository.save(transaction);
        
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
