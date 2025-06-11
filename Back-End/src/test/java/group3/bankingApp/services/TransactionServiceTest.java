package group3.bankingApp.services;

import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import group3.bankingApp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedTransaction() {
        Transaction tx = new Transaction();
        tx.setAmount(100.0);

        when(transactionRepository.save(tx)).thenReturn(tx);

        Transaction result = transactionService.save(tx);
        assertEquals(100.0, result.getAmount());
        System.out.println("save_ShouldReturnSavedTransaction: PASSED");
    }

    @Test
    void createAndRecordFromRequest_ShouldSucceed_WhenValid() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setSenderIban("NL01INHO0001");
        request.setReceiverIban("NL01INHO0002");
        request.setAmount(100.0);
        request.setDescription("Transfer test");

        Account sender = new Account();
        sender.setAccountId(1);
        sender.setBalance(500);
        sender.setAbsoluteLimit(-100);

        Account receiver = new Account();
        receiver.setAccountId(2);
        receiver.setBalance(200);

        when(accountRepository.findByIBAN("NL01INHO0001")).thenReturn(Optional.of(sender));
        when(accountRepository.findByIBAN("NL01INHO0002")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createAndRecordFromRequest(request);

        assertEquals(1, result.getSenderAccount());
        assertEquals(2, result.getReceiverAccount());
        assertEquals(100.0, result.getAmount());
        System.out.println("createAndRecordFromRequest_ShouldSucceed_WhenValid: PASSED");
    }

    @Test
    void createAndRecordFromRequest_ShouldThrow_WhenAmountNegative() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setSenderIban("NL01");
        request.setReceiverIban("NL02");
        request.setAmount(-10.0);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createAndRecordFromRequest(request));

        System.out.println("createAndRecordFromRequest_ShouldThrow_WhenAmountNegative: PASSED");
    }

    @Test
    void transferFundsAsEmployee_ShouldSucceed_WhenValid() {
        EmployeeTransferRequest req = new EmployeeTransferRequest();
        req.setSenderIBAN("NL01INHO0001");
        req.setReceiverIBAN("NL01INHO0002");
        req.setAmount(100.0);
        req.setDescription("Employee TX");

        Account sender = new Account();
        sender.setAccountId(1);
        sender.setAccountType(AccountType.Checking);
        sender.setBalance(500);
        sender.setAbsoluteLimit(-100);

        Account receiver = new Account();
        receiver.setAccountId(2);
        receiver.setAccountType(AccountType.Checking);
        receiver.setBalance(200);

        when(accountRepository.findByIBAN("NL01INHO0001")).thenReturn(Optional.of(sender));
        when(accountRepository.findByIBAN("NL01INHO0002")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.transferFundsAsEmployee(req);

        assertEquals(1, result.getSenderAccount());
        assertEquals(2, result.getReceiverAccount());
        assertEquals(100.0, result.getAmount());
        System.out.println("transferFundsAsEmployee_ShouldSucceed_WhenValid: PASSED");
    }

    @Test
    void transferFundsAsEmployee_ShouldThrow_WhenSameAccount() {
        EmployeeTransferRequest req = new EmployeeTransferRequest();
        req.setSenderIBAN("NL01INHO0001");
        req.setReceiverIBAN("NL01INHO0001");
        req.setAmount(50.0);

        Account account = new Account();
        account.setAccountId(1);
        account.setAccountType(AccountType.Checking);
        account.setBalance(500);
        account.setAbsoluteLimit(-100);

        when(accountRepository.findByIBAN("NL01INHO0001")).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferFundsAsEmployee(req));

        System.out.println("transferFundsAsEmployee_ShouldThrow_WhenSameAccount: PASSED");
    }

    @Test
    void getPaginatedTransactionJoinDTOs_ShouldReturnPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 5);
        TransactionJoinDTO dto = new TransactionJoinDTO(
            1, "senderUser", "NL01INHO0001", "NL01INHO0002", "receiverUser",
            100.0, "Test Description", LocalDateTime.now());

        Page<TransactionJoinDTO> mockPage = new PageImpl<>(List.of(dto));

        when(transactionRepository.findAllJoinDTO(pageable)).thenReturn(mockPage);

        Page<TransactionJoinDTO> result = transactionService.getPaginatedTransactionJoinDTOs(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("senderUser", result.getContent().get(0).getSenderUsername());
    }

    @Test
    void getPaginatedTransactionJoinDTOsFiltered_ShouldReturnFilteredPage() {
        String query = "john";
        Pageable pageable = PageRequest.of(0, 5);
        TransactionJoinDTO dto = new TransactionJoinDTO(
            2, "john_doe", "NL01INHO0003", "NL01INHO0004", "alice",
            200.0, "Filtered Test", LocalDateTime.now());

        Page<TransactionJoinDTO> mockPage = new PageImpl<>(List.of(dto));

        when(transactionRepository.findBySenderOrReceiverUsernameJoinDTO(query, pageable)).thenReturn(mockPage);

        Page<TransactionJoinDTO> result = transactionService.getPaginatedTransactionJoinDTOsFiltered(query, pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getSenderUsername().toLowerCase().contains(query));
    }


}
