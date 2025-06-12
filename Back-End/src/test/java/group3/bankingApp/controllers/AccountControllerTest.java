package group3.bankingApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.bankingApp.DTO.AccountRequest;
import group3.bankingApp.controller.AccountController;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.util.JwtTokenParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AccountService accountService;
    @MockBean private JwtTokenParser parser;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void testGetAccountById() throws Exception {
        Account acc = new Account();
        acc.setAccountId(100);
        acc.setBalance(1234.56);

        when(accountService.findById(100)).thenReturn(acc);

        mockMvc.perform(get("/api/account/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(100));
    }

    @Test
    void testGetAccountsByUserId() throws Exception {
        Account acc1 = new Account(); acc1.setAccountId(1);
        Account acc2 = new Account(); acc2.setAccountId(2);

        when(parser.getTokenUserId(any())).thenReturn(42);
        when(accountService.findUsersAccounts(42)).thenReturn(Arrays.asList(acc1, acc2));

        mockMvc.perform(get("/api/account/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetPendingAccounts() throws Exception {
        Page<Account> page = new PageImpl<>(Collections.singletonList(new Account()));
        when(parser.getTokenUserId(any())).thenReturn(42);
        when(accountService.findByUserIdAndVerifyStatus(eq(42), eq(VerifyStatus.PENDING), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/account/user/pending?page=0&pageSize=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetSavingsAccounts() throws Exception {
        Page<Account> page = new PageImpl<>(Collections.singletonList(new Account()));
        when(parser.getTokenUserId(any())).thenReturn(42);
        when(accountService.findByUserIdAndAccountType(eq(42), eq(AccountType.Saving), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/account/user/savings?page=0&pageSize=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetCheckingsAccounts() throws Exception {
        Page<Account> page = new PageImpl<>(Collections.singletonList(new Account()));
        when(parser.getTokenUserId(any())).thenReturn(42);
        when(accountService.findByUserIdAndAccountType(eq(42), eq(AccountType.Checking), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/account/user/checkings?page=0&pageSize=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testCreateAccountWhenAlreadyPending() throws Exception {
        when(parser.getTokenUserId(any())).thenReturn(42);
        when(parser.getTokenUsername(any())).thenReturn("testuser");
        when(accountService.checkIfUserHasPendingAccount(42)).thenReturn(true);

        AccountRequest req = new AccountRequest();
        req.accountType = AccountType.Saving;

        mockMvc.perform(post("/api/account/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("cannot request account: already have account pending verification"));
    }

    @Test
    void testCreateAccountWhenAllowed() throws Exception {
        when(parser.getTokenUserId(any())).thenReturn(42);
        when(parser.getTokenUsername(any())).thenReturn("testuser");
        when(accountService.checkIfUserHasPendingAccount(42)).thenReturn(false);

        AccountRequest req = new AccountRequest();
        req.accountType = AccountType.Saving;

        mockMvc.perform(post("/api/account/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Account request generated"));
    }

    @Test
    void testGetAllAccountsPaged() throws Exception {
        Page<Account> page = new PageImpl<>(List.of(new Account()));
        when(accountService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/account?page=0&size=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }
}
