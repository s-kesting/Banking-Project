package group3.bankingApp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.TransactionType;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.TransactionRepository;
import group3.bankingApp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ATMServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ATMService atmService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("john");

        testAccount = new Account();
        testAccount.setAccountId(100);
        testAccount.setUserId(1);
        testAccount.setBalance(500.0);
    }

    @Test
    void testSuccessfulDeposit() {
        when(accountRepository.findById(100)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO result = atmService.deposit(100, 1, 200.0);

        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertEquals(200.0, result.getAmount());
        assertEquals("john", result.getReceiverUsername());

        verify(accountRepository).save(argThat(account -> account.getBalance() == 700.0));
    }

    @Test
    void testSuccessfulWithdrawal() {
        when(accountRepository.findById(100)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO result = atmService.withdraw(100, 1, 300.0);

        assertEquals(TransactionType.WITHDRAW, result.getTransactionType());
        assertEquals(300.0, result.getAmount());
        assertEquals("john", result.getSenderUsername());

        verify(accountRepository).save(argThat(account -> account.getBalance() == 200.0));
    }

    @Test
    void testWithdrawInsufficientFunds() {
        when(accountRepository.findById(100)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> atmService.withdraw(100, 1, 1000.0));

        assertEquals("400 BAD_REQUEST \"Insufficient funds.\"", exception.getMessage());
    }

    @Test
    void testNegativeDepositFails() {
        when(accountRepository.findById(100)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> atmService.deposit(100, 1, -100.0));

        assertEquals("400 BAD_REQUEST \"Amount must be greater than zero.\"", exception.getMessage());
    }

    @Test
    void testUserNotOwnerFails() {
        testAccount.setUserId(999); // wrong owner
        when(accountRepository.findById(100)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> atmService.deposit(100, 1, 100.0));

        assertEquals("400 BAD_REQUEST \"User does not own this account.\"", exception.getMessage());
    }
}
