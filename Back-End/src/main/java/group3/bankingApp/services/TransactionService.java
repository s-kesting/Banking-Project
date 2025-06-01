package group3.bankingApp.services;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.User;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import group3.bankingApp.repository.UserRepository;
import jakarta.transaction.Transactional;

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
    public Transaction createAndRecordFromRequest(TransactionRequestDTO request) {
        validateRequest(request);

        Account sender = searchAccountByIban(request.getSenderIban(), "Sender IBAN not found");
        Account receiver = searchAccountByIban(request.getReceiverIban(), "Receiver IBAN not found");

        preventSelfTransfer(sender, receiver);
        checkAbsoluteLimit(sender, request.getAmount());

        updateBalances(sender, receiver, request.getAmount());

        Transaction transaction = buildTransaction(sender, receiver, request);
        return transactionRepository.save(transaction);
    }
    //private helper method for transaction
    //check IBAN and amount
     private void validateRequest(TransactionRequestDTO request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (request.getSenderIban() == null || request.getSenderIban().isBlank()) {
            throw new IllegalArgumentException("Sender IBAN must be provided");
        }
        if (request.getReceiverIban() == null || request.getReceiverIban().isBlank()) {
            throw new IllegalArgumentException("Receiver IBAN must be provided");
        }
    }
    //search for IBAN
     private Account searchAccountByIban(String iban, String errorMessage) {
        return accountRepository
            .findByIBAN(iban)
            .orElseThrow(() -> new NoSuchElementException(errorMessage));
    }

    //avoid sending fund to yourself
     private void preventSelfTransfer(Account sender, Account receiver) {
        if (sender.getAccountId().equals(receiver.getAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
    }

     private void checkAbsoluteLimit(Account sender, double amount) {
        double newBalance = sender.getBalance() - amount;
        if (newBalance < sender.getAbsoluteLimit()) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    private void updateBalances(Account sender, Account receiver, double amount) {
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

   private Transaction buildTransaction(Account sender, Account receiver, TransactionRequestDTO request) {
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(sender.getAccountId());
        transaction.setReceiverAccount(receiver.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
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
