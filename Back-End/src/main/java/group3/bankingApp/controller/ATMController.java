package group3.bankingApp.controller;

import group3.bankingApp.model.ATMSession;
import group3.bankingApp.services.ATMService;

import group3.bankingApp.model.Transaction;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/ATM")
public class ATMController {

    private final ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/start-session")
    public ResponseEntity<ATMSession> startSession(@RequestBody Map<String, Object> request) {
        Integer accountId = (Integer) request.get("accountId");
        System.out.println("Attempting to create ATM session for account: " + accountId);
        ATMSession session = atmService.startSession(accountId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/transfer")
public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request) {
    try {
        Integer sessionId = (Integer) request.get("sessionId");
        Integer targetAccountId = (Integer) request.get("targetAccountId");
        Double amount = ((Number) request.get("amount")).doubleValue();
        String description = (String) request.getOrDefault("description", "");

        Transaction transaction = atmService.transfer(sessionId, targetAccountId, amount, description);
        return ResponseEntity.ok(transaction);
    } catch (Exception e) {
        // 🔍 Enhanced logging:
        System.err.println("ATM transfer failed:");
        e.printStackTrace(); // This logs the full stack trace to the console
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

}
