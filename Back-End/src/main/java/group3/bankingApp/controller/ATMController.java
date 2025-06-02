package group3.bankingApp.controller;

import group3.bankingApp.model.ATMSession;
import group3.bankingApp.model.ATMTransaction;
import group3.bankingApp.model.Account;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.ATMService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atm")
public class ATMController {

    private final ATMService atmService;
    private final AccountRepository accountRepository;

    public ATMController(ATMService atmService, AccountRepository accountRepository) {
        this.atmService = atmService;
        this.accountRepository = accountRepository;
    }

    private Integer getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // In your JWT setup, auth.getName() holds the userId as a string
        return Integer.parseInt(auth.getName());
    }

    /**
     * GET /api/atm/accounts
     * Returns only the accounts owned by the currently authenticated user.
     * Each account is transformed into a Map<String,Object> so JSON serializes properly.
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<Map<String, Object>>> getAccountsForCurrentUser() {
        Integer userId = getUserId();
        List<Account> accounts = accountRepository.findByUserId(userId);

        // Build a List<Map<String,Object>> manually to guarantee correct JSON serialization
        List<Map<String, Object>> serialized = accounts.stream()
            .map(acc -> {
                Map<String, Object> m = new HashMap<>();
                m.put("accountId",   acc.getAccountId());
                m.put("balance",     acc.getBalance());
                m.put("accountType", acc.getAccountType().toString());
                return m;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(serialized);
    }

    /**
     * POST /api/atm/session
     * Creates an ATM session for a given account (only if it belongs to this user).
     */
    @PostMapping("/session")
    public ResponseEntity<?> startSession(@RequestBody Map<String, Object> requestBody) {
        Integer userId    = getUserId();
        Integer accountId = (Integer) requestBody.get("accountId");

        Optional<Account> accOpt = accountRepository.findById(accountId);
        if (accOpt.isEmpty() || !accOpt.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403)
                                 .body(Map.of("error", "Unauthorized access to account"));
        }

        ATMSession session = atmService.startSession(accountId);
        return ResponseEntity.ok(session);
    }

    /**
     * POST /api/atm/deposit
     * Deposits the specified amount into the specified account/session.
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> body) {
        Integer userId    = getUserId();
        Integer sessionId = (Integer) body.get("sessionId");
        Integer accountId = (Integer) body.get("accountId");
        Integer amount    = (Integer) body.get("amount");

        ATMTransaction txn = atmService.deposit(sessionId, accountId, userId, amount);
        return ResponseEntity.ok(txn);
    }

    /**
     * POST /api/atm/withdraw
     * Withdraws the specified amount from the specified account/session.
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> body) {
        Integer userId    = getUserId();
        Integer sessionId = (Integer) body.get("sessionId");
        Integer accountId = (Integer) body.get("accountId");
        Integer amount    = (Integer) body.get("amount");

        ATMTransaction txn = atmService.withdraw(sessionId, accountId, userId, amount);
        return ResponseEntity.ok(txn);
    }
}
