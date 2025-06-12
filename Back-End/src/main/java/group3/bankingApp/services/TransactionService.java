package group3.bankingApp.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import group3.bankingApp.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
            UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getUserTransactionBySenderOrReceiverAccount(int userId) {
        return transactionRepository.findBySender_UserIdOrReceiver_UserId(userId, userId);
    }

    public Page<TransactionJoinDTO> getTransactionsByIban(String Iban, Pageable pageable) {
        Page<TransactionJoinDTO> transactions = transactionRepository.findBySender_IBANOrReceiver_IBAN(Iban, Iban,
                pageable);

        return transactions;
    }

    public Page<TransactionJoinDTO> getTransactionsByIbanWithFilter(String Iban, Pageable pageable,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String minAmount,
            String maxAmount,
            String exactAmount,
            String filterIban) {
        Page<TransactionJoinDTO> transactions = transactionRepository.findBySender_IBANOrReceiver_IBANWithFilter(Iban,
                Iban,
                startDate,
                endDate,
                minAmount,
                maxAmount,
                exactAmount,
                filterIban,
                pageable);

        return transactions;
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction createAndRecordFromRequest(TransactionRequestDTO request) {
        // dto validation
        validateRequest(request);
        // load accounts by IBAN
        Account sender = searchAccountByIban(request.getSenderIban(), "Sender IBAN not found");
        Account receiver = searchAccountByIban(request.getReceiverIban(), "Receiver IBAN not found");

        preventSelfTransfer(sender, receiver);
        checkAbsoluteLimit(sender, request.getAmount());

        // update balance
        sender.setBalance(sender.getBalance() - request.getAmount());
        receiver.setBalance(receiver.getBalance() + request.getAmount());
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // build and save transaction record
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(sender.getAccountId());
        transaction.setReceiverAccount(receiver.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return transaction;

    }

    // private helper method for transaction
    // check IBAN and amount
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

    // search for IBAN
    private Account searchAccountByIban(String iban, String errorMessage) {
        return accountRepository.findByIBAN(iban).orElseThrow(() -> new NoSuchElementException(errorMessage));
    }

    // avoid sending fund to yourself
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

    // search iban base on username
    public List<String> searchCounterpartyIbans(int userId, String name) {
        return transactionRepository.findCounterpartyIbansByName(userId, name.toLowerCase());

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

    // private helpers to search IBAN based user name
    private List<Integer> getMyAccountIds(int userId) {
        return accountRepository.findByUserId(userId).stream().map(Account::getAccountId).collect(Collectors.toList());
    }

    private Set<Integer> getCounterpartyAccountIds(List<Integer> myAccountIds) {
        List<Transaction> relatedTxs = transactionRepository.findByAccountIds(myAccountIds);
        Set<Integer> counterIds = new HashSet<>();

        for (Transaction tx : relatedTxs) {
            int senderId = tx.getSenderAccount();
            int receiverId = tx.getReceiverAccount();

            if (myAccountIds.contains(senderId)) {
                counterIds.add(receiverId);
            } else if (myAccountIds.contains(receiverId)) {
                counterIds.add(senderId);
            }
        }
        return counterIds;
    }

    private List<String> filterIbansByName(Set<Integer> counterAccountIds, String searchName) {
        List<Account> counterAccounts = accountRepository.findAllById(counterAccountIds);
        String lowerName = searchName.toLowerCase();

        return counterAccounts.stream().map(Account::getUserId)
                .distinct()
                .flatMap(uid -> userRepository.findById(uid).stream())
                .filter(user -> user.getUsername().toLowerCase().contains(lowerName))
                .flatMap(user -> accountRepository.findByUserId(user.getUserId()).stream().map(Account::getIBAN))
                .distinct()
                .collect(Collectors.toList());
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

        if (sender.getAccountId().equals(receiver.getAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
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

    //////////////////////////////////////////////// Robben - Employee List
    //////////////////////////////////////////////// Pagination Transaction
    public Page<TransactionJoinDTO> getPaginatedTransactionJoinDTOs(Pageable pageable) {
        return transactionRepository.findAllJoinDTO(pageable);
    }

    public Page<TransactionJoinDTO> getPaginatedTransactionJoinDTOsFiltered(String query, Pageable pageable) {
        return transactionRepository.findBySenderOrReceiverUsernameJoinDTO(query.toLowerCase(), pageable);
    }

    ////////////////////////////////////////////////////////////////////////////////////

}
