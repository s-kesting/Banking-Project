package group3.bankingApp.controller;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.TransactionService;
import group3.bankingApp.util.JwtTokenParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Endpoints for money transfers")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtTokenParser jwtParser;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionService transactionService, AccountRepository accountRepository) {
        this.transactionService = transactionService;
        this.jwtParser = new JwtTokenParser();
        this.accountRepository = accountRepository;
    }

    @Operation(summary = "Create a new transaction (transfer money)", description = "sender, receiver, and records the transaction.")
    @PostMapping("/user")
    public ResponseEntity<Transaction> createTransactionForUser(@RequestBody TransactionRequestDTO requestDto, Authentication authentication) {

        // extract the userId from the JWT
        int userId = jwtParser.getTokenUserId(authentication);
        System.out.println(requestDto);
         // verify that the senderIban belongs to this user
        Account sender = accountRepository
            .findByIBAN(requestDto.getSenderIban())
            .orElseThrow(() -> new NoSuchElementException("Sender IBAN not found"));
        if (!sender.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // proceed with transaction
        Transaction savedTransaction = transactionService.createAndRecordFromRequest(requestDto);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/allTransactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactionDTOs();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

        @PostMapping("/employee-transfer")
    public ResponseEntity<?> employeeTransfer(@RequestBody EmployeeTransferRequest req) {
        try {
            Transaction tx = transactionService.transferFundsAsEmployee(req);
            return ResponseEntity.ok(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }




}
