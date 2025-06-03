package group3.bankingApp.controller;

import group3.bankingApp.model.User;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.AccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EmployeeController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
     private final AccountService accountService;

    public EmployeeController(UserRepository userRepository, AccountRepository accountRepository,
            AccountService accountService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers(@RequestParam(required = false) String username) {
        List<User> users = (username == null || username.isEmpty())
                ? userRepository.findAll()
                : userRepository.findByUsernameContainingIgnoreCase(username);

        List<Map<String, Object>> result = users.stream()
                .map(user -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("user", user);
                    data.put("accounts", accountRepository.findByUserId(user.getUserId()));
                    return data;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<?> updateUserVerification(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> body
    ) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = optionalUser.get();
            String newStatus = body.get("verifyUser");

            // Only trigger account creation if status is changed to ACTIVE
            boolean wasPending = user.getVerifyUser() != VerifyStatus.ACTIVE;
            user.setVerifyUser(VerifyStatus.valueOf(newStatus));
            userRepository.save(user);

            if (newStatus.equals("ACTIVE") && wasPending) {
                List<Account> existingAccounts = accountRepository.findByUserId(userId);
                if (existingAccounts.isEmpty()) {
                    accountService.createDefaultAccountsForUser(userId);
                }
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "User status updated"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Update failed: " + e.getMessage()));
        }
    }


    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Integer accountId, @RequestBody Account updated) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = optionalAccount.get();
        account.setVerifyAccount(updated.getVerifyAccount());
        account.setDailyLimit(updated.getDailyLimit());
        account.setAbsoluteLimit(updated.getAbsoluteLimit());

        accountRepository.save(account);
        return ResponseEntity.ok("Account updated");
    }
}
