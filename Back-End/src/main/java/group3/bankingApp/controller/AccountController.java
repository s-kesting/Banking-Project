package group3.bankingApp.controller;

import org.springframework.security.core.Authentication;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @Operation(summary = "Get a users account by their usersId")
    @GetMapping("user")
    public ResponseEntity<List<Account>> getAccountsByUserId(Authentication authentication) {

        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        List<Account> accounts = accountService.findUsersAccounts(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Get a users savings account by their userId")
    @GetMapping("user/savings")
    public ResponseEntity<List<Account>> getSavingsAccountByUserId(Authentication authentication) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        List<Account> accounts = accountService.findByUserIdAndAccountType(userId, AccountType.Saving);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PostMapping("user/newAccount")
    public void CreateAccount(Authentication authentication, @RequestBody AccountType accountType) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        // FIXME:: get requestBody
        System.out.println(accountType);
        logger.info("new banking account request from: " + parser.getTokenUsername(authentication));
        // accountService.newAccountRequest(userId, accountType);
    }

    @Operation(summary = "Get a users checkings account by their userId")
    @GetMapping("user/checkings")
    public ResponseEntity<List<Account>> getCheckingsAccountByUserId(Authentication authentication) {
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        List<Account> accounts = accountService.findByUserIdAndAccountType(userId, AccountType.Checking);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "get all accounts")
    @GetMapping()
    public Page<Account> getAccounts(
            @ParameterObject Pageable pageable) {
        return accountService.findAll(pageable);
    }

}
