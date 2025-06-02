package group3.bankingApp.controller;

import org.springframework.security.core.Authentication;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import group3.bankingApp.model.Account;
import group3.bankingApp.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import group3.bankingApp.util.JwtTokenParser;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public Account getAccount(@ParameterObject int id) {
        return accountService.findById(id);
    }

    @Operation(summary = "Get users account by the users ID")
    @GetMapping("user")
    public ResponseEntity<List<Account>> getAccountsByUserId(Authentication authentication) {

        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);

        System.out.println("accounts endpoint triggerd");
        List<Account> accounts = accountService.findUsersAccounts(userId);
        System.out.println(accounts);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "get all accounts")
    @GetMapping()
    public Page<Account> getAccounts(
            @ParameterObject Pageable pageable) {
        return accountService.findAll(pageable);
    }

}
