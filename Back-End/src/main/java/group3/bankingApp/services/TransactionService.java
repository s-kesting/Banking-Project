package group3.bankingApp.services;

import org.springframework.stereotype.Service;

import group3.bankingApp.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

}
