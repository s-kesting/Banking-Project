package group3.bankingApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.bankingApp.DTO.AccountSummaryDTO;
import group3.bankingApp.DTO.AtmTransactionRequestDTO;
import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.controller.ATMController;
import group3.bankingApp.model.enums.TransactionType;
import group3.bankingApp.services.ATMService;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.util.JwtTokenParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ATMControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ATMService atmService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenParser parser;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAccountsForCurrentUser() throws Exception {
        AccountSummaryDTO acc1 = new AccountSummaryDTO();
        acc1.setAccountId(1);
        acc1.setAccountType("Checking");
        acc1.setBalance(500.0);

        AccountSummaryDTO acc2 = new AccountSummaryDTO();
        acc2.setAccountId(2);
        acc2.setAccountType("Savings");
        acc2.setBalance(1500.0);

        when(parser.getTokenUserId(any())).thenReturn(5);
        when(accountService.getAccountSummaries(5)).thenReturn(Arrays.asList(acc1, acc2));

        mockMvc.perform(get("/api/atm/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[1].balance").value(1500.0));
    }

    @Test
    void testDeposit() throws Exception {
        AtmTransactionRequestDTO request = new AtmTransactionRequestDTO();
        request.setAccountId(1);
        request.setAmount(200.0);

        TransactionDTO response = new TransactionDTO();
        response.setTransactionType(TransactionType.DEPOSIT);
        response.setAmount(200.0);

        when(parser.getTokenUserId(any())).thenReturn(42);
        when(atmService.deposit(1, 42, 200.0)).thenReturn(response);

        mockMvc.perform(post("/api/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(200.0));
    }

    @Test
    void testWithdraw() throws Exception {
        AtmTransactionRequestDTO request = new AtmTransactionRequestDTO();
        request.setAccountId(2);
        request.setAmount(50.0);

        TransactionDTO response = new TransactionDTO();
        response.setTransactionType(TransactionType.WITHDRAW);
        response.setAmount(50.0);

        when(parser.getTokenUserId(any())).thenReturn(7);
        when(atmService.withdraw(2, 7, 50.0)).thenReturn(response);

        mockMvc.perform(post("/api/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("WITHDRAW"))
                .andExpect(jsonPath("$.amount").value(50.0));
    }
}
