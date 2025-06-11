package group3.bankingApp.services;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedUser() {
        User user = new User();
        user.setUsername("roben");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);
        assertEquals("roben", result.getUsername());

        System.out.println("Test: save_ShouldReturnSavedUser → SUCCESS");
;
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("roben");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);
        assertEquals("roben", result.getUsername());

        System.out.println("getUserById_ShouldReturnUser_WhenFound: PASSED");
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(99));

        System.out.println("getUserById_ShouldThrowException_WhenNotFound: PASSED");
    }

    @Test
    void updateUserVerificationStatus_ShouldSyncAccountsAndCreateAccountsIfEmpty() {
        User user = new User();
        user.setUserId(1);
        user.setVerifyUser(VerifyStatus.PENDING);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(1)).thenReturn(Collections.emptyList());

        userService.updateUserVerificationStatus(1, "ACTIVE");

        verify(userRepository).save(user);
        verify(accountService).createDefaultAccountsForUser(1);

        System.out.println("pdateUserVerificationStatus_ShouldSyncAccountsAndCreateAccountsIfEmpty: PASSED");
    }

    @Test
    void updateUserVerificationStatus_ShouldSyncAccountStatuses() {
        User user = new User();
        user.setUserId(1);
        user.setVerifyUser(VerifyStatus.ACTIVE);

        Account account = new Account();
        List<Account> accounts = List.of(account);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(1)).thenReturn(accounts);

        userService.updateUserVerificationStatus(1, "REJECTED");

        verify(accountRepository).saveAll(accounts);
        assertEquals(VerifyStatus.REJECTED, account.getVerifyAccount());

        System.out.println("updateUserVerificationStatus_ShouldSyncAccountStatuses: PASSED");
    }
}
