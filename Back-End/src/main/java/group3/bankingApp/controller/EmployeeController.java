package group3.bankingApp.controller;

import group3.bankingApp.model.User;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.model.UserWithAccounts;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.repository.AccountRepository;

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

    public EmployeeController(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithAccounts>> getUsers(@RequestParam(required = false) String username) {
        List<User> users = (username == null || username.isEmpty())
                ? userRepository.findAll()
                : userRepository.findByUsernameContainingIgnoreCase(username);

        List<UserWithAccounts> result = users.stream()
                .map(user -> new UserWithAccounts(user, accountRepository.findByUserId(user.getUserId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer userId, @RequestBody Map<String, String> payload) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setVerifyUser(VerifyStatus.valueOf(payload.get("verifyUser")));
        userRepository.save(user);
        return ResponseEntity.ok("User updated");
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
