package group3.bankingApp.controller;

import group3.bankingApp.model.ATMTransaction;
import group3.bankingApp.model.ATMSession;
import group3.bankingApp.services.ATMService;
import group3.bankingApp.util.JwtTokenParser;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import group3.bankingApp.model.enums.ATMTransactionStatus;
import group3.bankingApp.model.enums.ATMTransactionType;

import java.util.Map;

@RestController
@RequestMapping("/atm")
public class ATMController {

    private final ATMService atmService;
    private final JwtTokenParser jwtTokenParser;

    public ATMController(ATMService atmService, JwtTokenParser jwtTokenParser) {
        this.atmService = atmService;
        this.jwtTokenParser = jwtTokenParser;
    }

    @PostMapping("/session")
    public ResponseEntity<?> startSession(Authentication authentication,
                                          @RequestBody Map<String, Integer> payload) {
        Integer accountId = payload.get("accountId");
        int userId = jwtTokenParser.getTokenUserId(authentication);

        // Optional: add server-side ownership check here (future improvement)

        ATMSession session = atmService.startSession(accountId);
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getSessionId(),
                "message", "ATM session started"
        ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(Authentication authentication,
                                      @RequestBody Map<String, Integer> payload) {
        return handleTransaction(authentication, payload, ATMTransactionType.WITHDRAWAL);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(Authentication authentication,
                                     @RequestBody Map<String, Integer> payload) {
        return handleTransaction(authentication, payload, ATMTransactionType.DEPOSIT);
    }

    private ResponseEntity<?> handleTransaction(Authentication authentication,
                                                Map<String, Integer> payload,
                                                ATMTransactionType type) {
        Integer sessionId = payload.get("sessionId");
        Integer accountId = payload.get("accountId");
        Integer amount = payload.get("amount");
        int userId = jwtTokenParser.getTokenUserId(authentication);

        ATMTransaction txn = switch (type) {
            case WITHDRAWAL -> atmService.withdraw(sessionId, accountId, userId, amount);
            case DEPOSIT    -> atmService.deposit(sessionId, accountId, userId, amount);
        };

        return ResponseEntity.ok(Map.of(
                "status", txn.getStatus().name(),
                "message", txn.getReason() != null ? txn.getReason() : "Transaction complete",
                "amount", txn.getAmount(),
                "type", txn.getType().name()
        ));
    }
}
