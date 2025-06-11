package group3.bankingApp.controller;

import org.springframework.security.core.Authentication;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import group3.bankingApp.model.Account;
import group3.bankingApp.DTO.AccountRequest;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import group3.bankingApp.util.JwtTokenParser;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public Account getAccount(@ParameterObject int id) {
        return accountService.findById(id);
    }

    @Operation(summary = "Get all users account by usersId")
    @GetMapping("user")
    public ResponseEntity<List<Account>> getAccountsByUserId(Authentication authentication) {

        System.out.println("");
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        List<Account> accounts = accountService.findUsersAccounts(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Get a users savings account by their userId")
    @GetMapping("user/pending")
    public ResponseEntity<Page<Account>> getPendingAccountByUserId(Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("AccountId").descending());
        Page<Account> accounts = accountService.findByUserIdAndVerifyStatus(userId, VerifyStatus.PENDING, pageable);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Get a users savings account by their userId")
    @GetMapping("user/savings")
    public ResponseEntity<Page<Account>> getSavingsAccountByUserId(Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("Balance").descending());
        Page<Account> accounts = accountService.findByUserIdAndAccountType(userId, AccountType.Saving, pageable);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Get a users checkings account by their userId")
    @GetMapping("user/checkings")
    public ResponseEntity<Page<Account>> getCheckingsAccountByUserId(Authentication authentication,
            @RequestParam int page,
            @RequestParam int pageSize) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("Balance").descending());
        Page<Account> accounts = accountService.findByUserIdAndAccountType(userId, AccountType.Checking, pageable);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Post endpoint for requesting a new account for a user")
    @PostMapping("user")
    public ResponseEntity<Map<String, String>> CreateAccount(Authentication authentication,
            @RequestBody AccountRequest accountType) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);
        logger.info("new banking account request from: " + parser.getTokenUsername(authentication));

        Map<String, String> message = new HashMap<String, String>();
        if (accountService.checkIfUserHasPendingAccount(userId)) {
            message.put("message", "cannot request account: already have account pending verification");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else {
            message.put("message", "Account request generated");
            accountService.newAccountRequest(userId, accountType.accountType);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @Operation(summary = "get all accounts")
    @GetMapping()
    public Page<Account> getAccounts(
            @ParameterObject Pageable pageable) {
        return accountService.findAll(pageable);
    }

}
