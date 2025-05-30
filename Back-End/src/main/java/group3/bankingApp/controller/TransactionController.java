package group3.bankingApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Endpoints for money transfers")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Create a new transaction (transfer money)", description = "sender, receiver, and records the transaction.")
    @PostMapping
    public ResponseEntity<Transaction> postTransaction(@RequestBody Transaction transaction) {
        Transaction createTransaction = transactionService.CreateTransaction(transaction);
        return new ResponseEntity<>(createTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/allTransactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactionDTOs();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }



}
