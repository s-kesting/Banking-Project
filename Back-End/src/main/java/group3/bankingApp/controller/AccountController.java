package group3.bankingApp.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group3.bankingApp.model.Account;
import group3.bankingApp.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    
    @Operation(summary = "Get accounts")
    @GetMapping
    public Page<Account> getAccounts(
    @ParameterObject Pageable pageable) {
        return accountService.findAll(pageable);
    }

}
