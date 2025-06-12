package group3.bankingApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import group3.bankingApp.DTO.AccountRequest;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.util.JwtTokenParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final JwtTokenParser parser;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService, JwtTokenParser parser) {
        this.accountService = accountService;
        this.parser = parser;
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable int id) {
        return accountService.findById(id);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Account>> getAccountsByUserId(Authentication authentication) {
        int userId = parser.getTokenUserId(authentication);
        List<Account> accounts = accountService.findUsersAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/pending")
    public ResponseEntity<Page<Account>> getPendingAccountByUserId(
            Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {

        int userId = parser.getTokenUserId(authentication);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("accountId").descending());
        Page<Account> accounts = accountService.findByUserIdAndVerifyStatus(
                userId, VerifyStatus.PENDING, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/savings")
    public ResponseEntity<Page<Account>> getSavingsAccountByUserId(
            Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {

        int userId = parser.getTokenUserId(authentication);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("balance").descending());
        Page<Account> accounts = accountService.findByUserIdAndAccountType(
                userId, AccountType.Saving, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/checkings")
    public ResponseEntity<Page<Account>> getCheckingsAccountByUserId(
            Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {

        int userId = parser.getTokenUserId(authentication);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("balance").descending());
        Page<Account> accounts = accountService.findByUserIdAndAccountType(
                userId, AccountType.Checking, pageable);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, String>> createAccount(
            Authentication authentication,
            @RequestBody AccountRequest request) {

        int userId = parser.getTokenUserId(authentication);
        logger.info("new banking account request from: {}", parser.getTokenUsername(authentication));

        Map<String, String> message = new HashMap<>();
        if (accountService.checkIfUserHasPendingAccount(userId)) {
            message.put("message", "cannot request account: already have account pending verification");
            return ResponseEntity.badRequest().body(message);
        } else {
            message.put("message", "Account request generated");
            // AccountRequest.accountType is a public field
            accountService.newAccountRequest(userId, request.accountType);
            return ResponseEntity.ok(message);
        }
    }

    @GetMapping
    public Page<Account> getAccounts(@ParameterObject Pageable pageable) {
        return accountService.findAll(pageable);
    }
}
