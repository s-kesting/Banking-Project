package group3.bankingApp.controllers;

import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.enums.TransactionType;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.services.TransactionService;
import group3.bankingApp.util.JwtTokenParser;
import group3.bankingApp.controller.TransactionController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    private TransactionService transactionService;
    private AccountRepository accountRepository;
    private JwtTokenParser jwtTokenParser;
    private TransactionController transactionController;
    private Authentication authentication;
    private AccountService accountService;
    

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        accountRepository = mock(AccountRepository.class);
        jwtTokenParser = mock(JwtTokenParser.class);
        authentication = mock(Authentication.class);
        accountService = mock(AccountService.class);
        transactionController = new TransactionController(transactionService, accountRepository, jwtTokenParser, accountService);
    }

    @Test
    void testGetUserTransactions() {
        when(jwtTokenParser.getTokenUserId(authentication)).thenReturn(1);
        List<Transaction> mockList = List.of(new Transaction());
        when(transactionService.getUserTransactionBySenderOrReceiverAccount(1)).thenReturn(mockList);

        ResponseEntity<List<Transaction>> response = transactionController.getUserTransactions(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetTransactionsByIban() {
        when(jwtTokenParser.getTokenUserId(authentication)).thenReturn(1);
        when(accountService.checkIfIbanBelongsToUser(1, "NL01TEST0123456789")).thenReturn(true);

        TransactionJoinDTO mockDto = new TransactionJoinDTO(
        1, "senderIBAN", "receiverIBAN", "Alice", "Bob",
        100.0, "TRANSFER", LocalDateTime.now()
        );
        Page<TransactionJoinDTO> page = new PageImpl<>(List.of(mockDto));

        when(transactionService.getTransactionsByIbanWithFilter(
            eq("NL01TEST0123456789"),
            any(Pageable.class),
            isNull(), isNull(), eq(""), eq(""), eq(""), eq(""))
        ).thenReturn(page);

        ResponseEntity<Page<TransactionJoinDTO>> response = transactionController.getTransactionsByIban(
            authentication, "NL01TEST0123456789", null, null, "", "", "","",0     );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    void testCreateTransactionForUser_Valid() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderIban("NL01TEST0000123456");

        Account mockAccount = new Account();
        mockAccount.setUserId(1);
        when(jwtTokenParser.getTokenUserId(authentication)).thenReturn(1);
        when(accountRepository.findByIBAN("NL01TEST0000123456")).thenReturn(Optional.of(mockAccount));

        Transaction tx = new Transaction();
        tx.setTransactionId(99);
        when(transactionService.createAndRecordFromRequest(requestDTO)).thenReturn(tx);

        ResponseEntity<?> response = transactionController.createTransactionForUser(requestDTO, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Transaction result = (Transaction) response.getBody();
        assertEquals(tx.getTransactionId(), result.getTransactionId());   
    }

    @Test
    void testCreateTransactionForUser_InvalidIban() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderIban("INVALID_IBAN");

        when(jwtTokenParser.getTokenUserId(authentication)).thenReturn(1);
        when(accountRepository.findByIBAN("INVALID_IBAN")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            transactionController.createTransactionForUser(requestDTO, authentication);
        });
    }

    @Test
    void testEmployeeTransfer_Success() {
        EmployeeTransferRequest req = new EmployeeTransferRequest();
        Transaction tx = new Transaction();
        tx.setTransactionId(123);

        when(transactionService.transferFundsAsEmployee(req)).thenReturn(tx);

        ResponseEntity<?> response = transactionController.employeeTransfer(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tx, response.getBody());
    }

    @Test
    void testEmployeeTransfer_Failure() {
        EmployeeTransferRequest req = new EmployeeTransferRequest();
        when(transactionService.transferFundsAsEmployee(req))
                .thenThrow(new IllegalArgumentException("Insufficient funds"));

        ResponseEntity<?> response = transactionController.employeeTransfer(req);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).get("error").toString().contains("Insufficient funds"));
    }
}
