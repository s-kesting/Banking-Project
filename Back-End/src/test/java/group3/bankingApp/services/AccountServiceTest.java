package group3.bankingApp.services;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.DTO.AccountUpdateDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedAccount() {
        Account account = new Account();
        account.setUserId(1);

        when(accountRepository.save(account)).thenReturn(account);

        Account saved = accountService.save(account);
        assertEquals(1, saved.getUserId());

        System.out.println("save_ShouldReturnSavedAccount: PASSED");
    }

    @Test
    void findById_ShouldReturnAccount_WhenExists() {
        Account account = new Account();
        account.setAccountId(100);

        when(accountRepository.findById(100)).thenReturn(Optional.of(account));

        Account found = accountService.findById(100);
        assertEquals(100, found.getAccountId());

        System.out.println("findById_ShouldReturnAccount_WhenExists: PASSED");
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(accountRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> accountService.findById(999));

        System.out.println("findById_ShouldThrow_WhenNotFound: PASSED");
    }

    @Test
    void findUsersAccounts_ShouldReturnList_WhenAccountsExist() {
        List<Account> accounts = List.of(new Account(), new Account());
        when(accountRepository.findByUserId(1)).thenReturn(accounts);

        List<Account> result = accountService.findUsersAccounts(1);
        assertEquals(2, result.size());

        System.out.println("findUsersAccounts_ShouldReturnList_WhenAccountsExist: PASSED");
    }

    @Test
    void findUsersAccounts_ShouldThrow_WhenNoAccountsExist() {
        when(accountRepository.findByUserId(5)).thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> accountService.findUsersAccounts(5));

        System.out.println("findUsersAccounts_ShouldThrow_WhenNoAccountsExist: PASSED");
    }

    @Test
    void updateAccount_ShouldModifyAndSaveAccount() {
        Account existing = new Account();
        existing.setAccountId(10);

        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.setAbsoluteLimit(-200.0);
        dto.setDailyLimit(1500.0);
        dto.setVerifyAccount("ACTIVE");


        when(accountRepository.findById(10)).thenReturn(Optional.of(existing));

        accountService.updateAccount(10, dto);

        verify(accountRepository).save(existing);
        assertEquals(-200, existing.getAbsoluteLimit());
        assertEquals(1500, existing.getDailyLimit());
        assertEquals(VerifyStatus.ACTIVE, existing.getVerifyAccount());

        System.out.println("updateAccount_ShouldModifyAndSaveAccount: PASSED");
    }

    @Test
    void generateIban_ShouldReturnUniqueIban() {
        when(accountRepository.existsByIBAN(anyString())).thenReturn(false);

        String iban = accountService.generateIban();
        assertTrue(iban.startsWith("NL") && iban.length() > 10);

        System.out.println("generateIban_ShouldReturnUniqueIban: PASSED");
    }

    @Test
    void findAll_ShouldReturnPagedAccounts() {
        List<Account> accounts = List.of(new Account());
        Page<Account> page = new PageImpl<>(accounts);

        when(accountRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Account> result = accountService.findAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());

        System.out.println("findAll_ShouldReturnPagedAccounts: PASSED");
    }
}
