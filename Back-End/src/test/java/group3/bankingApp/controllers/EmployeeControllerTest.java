package group3.bankingApp.controller;

import group3.bankingApp.DTO.AccountUpdateDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.User;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private AccountService accountService;
    private UserService userService;

    private EmployeeController employeeController;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        accountRepository = mock(AccountRepository.class);
        accountService = mock(AccountService.class);
        userService = mock(UserService.class);

        employeeController = new EmployeeController(userRepository, accountRepository, accountService, userService);
    }

    @Test
    public void testGetUsers_NoUsername() {
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");

        List<User> users = Collections.singletonList(mockUser);
        List<Account> accounts = Collections.singletonList(new Account());

        when(userRepository.findAll()).thenReturn(users);
        when(accountRepository.findByUserId(1)).thenReturn(accounts);

        ResponseEntity<List<Map<String, Object>>> response = employeeController.getUsers(null);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetUsers_WithUsername() {
        User mockUser = new User();
        mockUser.setUserId(2);
        mockUser.setUsername("searchUser");

        List<User> users = Collections.singletonList(mockUser);
        List<Account> accounts = Collections.singletonList(new Account());

        when(userRepository.findByUsernameContainingIgnoreCase("search")).thenReturn(users);
        when(accountRepository.findByUserId(2)).thenReturn(accounts);

        ResponseEntity<List<Map<String, Object>>> response = employeeController.getUsers("search");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetUsersPaginated() {
        Map<String, Object> mockPageResult = new HashMap<>();
        mockPageResult.put("users", List.of());

        when(userService.getPaginatedUserDTOs(0, 10, null)).thenReturn(mockPageResult);

        ResponseEntity<Map<String, Object>> response = employeeController.getUsers(0, 10, null);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("users"));
    }

    @Test
    public void testUpdateUserVerification_Success() {
        Map<String, String> request = Map.of("verifyUser", "VERIFIED");

        doNothing().when(userService).updateUserVerificationStatus(1, "VERIFIED");

        ResponseEntity<?> response = employeeController.updateUserVerification(1, request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateUserVerification_NotFound() {
        Map<String, String> request = Map.of("verifyUser", "VERIFIED");

        doThrow(new NoSuchElementException()).when(userService).updateUserVerificationStatus(99, "VERIFIED");

        ResponseEntity<?> response = employeeController.updateUserVerification(99, request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateAccount_Success() {
        AccountUpdateDTO dto = new AccountUpdateDTO();
        doNothing().when(accountService).updateAccount(10, dto);

        ResponseEntity<?> response = employeeController.updateAccount(10, dto);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateAccount_Failure() {
        AccountUpdateDTO dto = new AccountUpdateDTO();
        doThrow(new RuntimeException("DB error")).when(accountService).updateAccount(10, dto);

        ResponseEntity<?> response = employeeController.updateAccount(10, dto);
        assertEquals(500, response.getStatusCodeValue());
    }
}
