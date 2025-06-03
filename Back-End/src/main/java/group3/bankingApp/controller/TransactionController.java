package group3.bankingApp.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.HashMap;


import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;

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

    //Pagination the Transaction
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> pagedTransactions = transactionService.getPaginatedTransactionDTOs(pageable, query);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", pagedTransactions.getContent());
        response.put("currentPage", pagedTransactions.getNumber());
        response.put("totalItems", pagedTransactions.getTotalElements());
        response.put("totalPages", pagedTransactions.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
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
