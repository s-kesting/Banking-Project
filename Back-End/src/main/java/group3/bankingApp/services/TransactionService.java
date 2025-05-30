package group3.bankingApp.services;
import group3.bankingApp.DTO.TransactionDTO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.User;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import group3.bankingApp.repository.UserRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(AccountRepository accountRepository,TransactionRepository transactionRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
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

    // Convert Transaction → TransactionDTO, Display username instead od userid
    public List<TransactionDTO> getAllTransactionDTOs() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction tx : transactions) {
            TransactionDTO dto = new TransactionDTO();
            dto.setTransactionId(tx.getTransactionId());
            dto.setAmount(tx.getAmount());
            dto.setDescription(tx.getDescription());
            dto.setCreatedAt(tx.getCreatedAt());

            // 🔄 Lookup sender and receiver usernames
            Account senderAcc = accountRepository.findById(tx.getSenderAccount()).orElse(null);
            Account receiverAcc = accountRepository.findById(tx.getReceiverAccount()).orElse(null);

            String senderUsername = senderAcc != null
                ? userRepository.findById(senderAcc.getUserId()).map(User::getUsername).orElse("Unknown")
                : "Unknown";

            String receiverUsername = receiverAcc != null
                ? userRepository.findById(receiverAcc.getUserId()).map(User::getUsername).orElse("Unknown")
                : "Unknown";

            dto.setSenderUsername(senderUsername);
            dto.setReceiverUsername(receiverUsername);

            dtoList.add(dto);
        }

        return dtoList;
    }

}
