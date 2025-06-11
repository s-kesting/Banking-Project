package group3.bankingApp.controller;

import group3.bankingApp.model.User;
import group3.bankingApp.DTO.AccountUpdateDTO;
import group3.bankingApp.DTO.UserWithAccountsDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.AccountService;
import group3.bankingApp.services.UserService;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EmployeeController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
     private final AccountService accountService;
     private final UserService userService;

    public EmployeeController(UserRepository userRepository, AccountRepository accountRepository,
            AccountService accountService, UserService userService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.userService = userService;
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

    // Robben---Pagination User with Account
    @GetMapping("/users/paginated")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username) {
        return ResponseEntity.ok(userService.getPaginatedUserDTOs(page, size, username));
    }


    //Verify User Status 
    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<?> updateUserVerification(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newStatus = body.get("verifyUser");
            userService.updateUserVerificationStatus(userId, newStatus);
            return ResponseEntity.ok(Map.of("success", true, "message", "User status updated"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Update failed: " + e.getMessage()));
        }
    }



    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<?> updateAccount(
            @PathVariable Integer accountId,
            @RequestBody AccountUpdateDTO dto
    ) {
        try {
            accountService.updateAccount(accountId, dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "Account updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

}
