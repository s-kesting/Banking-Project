package group3.bankingApp.services;
import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.HashMap;




import org.springframework.stereotype.Service;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.AccountType;
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

    @Transactional
    public Transaction transferFundsAsEmployee(EmployeeTransferRequest req) {
        Account sender = accountRepository.findByIBAN(req.getSenderIBAN())
            .orElseThrow(() -> new IllegalArgumentException("Sender IBAN is invalid"));

        Account receiver = accountRepository.findByIBAN(req.getReceiverIBAN())
            .orElseThrow(() -> new IllegalArgumentException("Receiver IBAN is invalid"));

        if (sender.getAccountType() != AccountType.Checking || receiver.getAccountType() != AccountType.Checking) {
            throw new IllegalArgumentException("Only CHECKING accounts are allowed for transfers");
        }

        double remainingBalance = sender.getBalance() - req.getAmount();
        if (remainingBalance < sender.getAbsoluteLimit()) {
            throw new IllegalArgumentException("Sender balance is not sufficient for this transaction");
        }

        // Withdraw and deposit
        sender.setBalance(sender.getBalance() - req.getAmount());
        receiver.setBalance(receiver.getBalance() + req.getAmount());

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Save transaction
        Transaction tx = new Transaction();
        tx.setSenderAccount(sender.getAccountId());
        tx.setReceiverAccount(receiver.getAccountId());
        tx.setAmount(req.getAmount());
        tx.setDescription(req.getDescription());
        tx.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(tx);
    }

       public Page<TransactionDTO> getPaginatedTransactionDTOs(Pageable pageable, String query) {
    Page<Transaction> transactionPage;

    if (query != null && !query.trim().isEmpty()) {
        transactionPage = transactionRepository.findBySenderOrReceiverUsername(query.toLowerCase(), pageable);
    } else {
        transactionPage = transactionRepository.findAll(pageable);
    }

    return transactionPage.map(this::convertToDTO);
}



    private TransactionDTO convertToDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tx.getTransactionId());
        dto.setAmount(tx.getAmount());
        dto.setDescription(tx.getDescription());
        dto.setCreatedAt(tx.getCreatedAt());

        accountRepository.findById(tx.getSenderAccount()).ifPresent(senderAcc ->
            userRepository.findById(senderAcc.getUserId()).ifPresent(senderUser ->
                dto.setSenderUsername(senderUser.getUsername())
            )
        );

        accountRepository.findById(tx.getReceiverAccount()).ifPresent(receiverAcc ->
            userRepository.findById(receiverAcc.getUserId()).ifPresent(receiverUser ->
                dto.setReceiverUsername(receiverUser.getUsername())
            )
        );

        return dto;
    }

}
