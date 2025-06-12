// src/main/java/group3/bankingApp/controller/ATMController.java
package group3.bankingApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import group3.bankingApp.DTO.AccountSummaryDTO;
import group3.bankingApp.DTO.AtmTransactionRequestDTO;
import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.services.ATMService;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.util.JwtTokenParser;

@RestController
@RequestMapping("/api/atm")
public class ATMController {

    private final ATMService     atmService;
    private final AccountService accountService;
    private final JwtTokenParser parser;

    public ATMController(ATMService atmService, AccountService accountService) {
        this.atmService     = atmService;
        this.accountService = accountService;
        this.parser         = new JwtTokenParser();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountSummaryDTO>> getAccountsForCurrentUser(Authentication auth) {
        Integer userId = parser.getTokenUserId(auth);
        return ResponseEntity.ok(accountService.getAccountSummaries(userId));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(
        @RequestBody AtmTransactionRequestDTO request,
        Authentication auth
    ) {
        Integer userId = parser.getTokenUserId(auth);
        TransactionDTO dto = atmService.deposit(
            request.getAccountId(),
            userId,
            request.getAmount()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(
        @RequestBody AtmTransactionRequestDTO request,
        Authentication auth
    ) {
        Integer userId = parser.getTokenUserId(auth);
        TransactionDTO dto = atmService.withdraw(
            request.getAccountId(),
            userId,
            request.getAmount()
        );
        return ResponseEntity.ok(dto);
    }
}
