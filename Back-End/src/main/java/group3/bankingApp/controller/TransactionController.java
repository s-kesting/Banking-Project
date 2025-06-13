package group3.bankingApp.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group3.bankingApp.DTO.EmployeeTransferRequest;
import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.DTO.TransactionRequestDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.AccountService;
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
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService transactionService, 
                             AccountRepository accountRepository,
                             JwtTokenParser jwtTokenParser,
                             AccountService accountService) {
    this.transactionService = transactionService;
    this.accountRepository = accountRepository;
        this.accountService = accountService;
    this.jwtParser = jwtTokenParser;
}


    @GetMapping("/user")
    public ResponseEntity<List<Transaction>> getUserTransactions(Authentication authentication) {
        int userid = jwtParser.getTokenUserId(authentication);
        List<Transaction> transactions = transactionService.getUserTransactionBySenderOrReceiverAccount(userid);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/Iban")
    public ResponseEntity<Page<TransactionJoinDTO>> getTransactionsByIban(Authentication authentication,
            @RequestParam String Iban,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String minAmount,
            @RequestParam(required = false) String maxAmount,
            @RequestParam(required = false) String exactAmount,
            @RequestParam(required = false) String filterIban,
            @RequestParam int page) {
        int userId = jwtParser.getTokenUserId(authentication);
        try {
            if (accountService.checkIfIbanBelongsToUser(userId, Iban)) {
                int size = 5;
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<TransactionJoinDTO> transactions = transactionService.getTransactionsByIbanWithFilter(Iban,
                        pageable,
                        startDate,
                        endDate,
                        minAmount,
                        maxAmount,
                        exactAmount,
                        filterIban);
                return new ResponseEntity<>(transactions, HttpStatus.OK);

            } else {
                this.logger.info("Request rejected userId and Iban did not match");
                throw new BadRequestException();
            }
        } catch (BadRequestException err) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Error err) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Create a new transaction (transfer money)", description = "sender, receiver, and records the transaction.")
    @PostMapping("/user")
    public ResponseEntity<?> createTransactionForUser(@RequestBody TransactionRequestDTO requestDto,
            Authentication authentication) {

        int userId = jwtParser.getTokenUserId(authentication);
        // verify sender IBAN exists and belongs to this user
        Account sender = accountRepository.findByIBAN(requestDto.getSenderIban())
                .orElseThrow(() -> new NoSuchElementException("Sender IBAN not found"));
        if (!sender.getUserId().equals(userId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don’t own the sender account"));
        }
        // attempt the transfer, catching any validation exceptions
        try {
            Transaction saved = transactionService.createAndRecordFromRequest(requestDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(saved);
        } catch (NoSuchElementException | IllegalArgumentException ex) {
            // covers "Receiver IBAN not found", "Insufficient funds", etc.
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // Robben - Pagination the Transaction
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TransactionJoinDTO> pagedTransactions;

        if (query != null && !query.trim().isEmpty()) {
            pagedTransactions = transactionService.getPaginatedTransactionJoinDTOsFiltered(query, pageable);
        } else {
            pagedTransactions = transactionService.getPaginatedTransactionJoinDTOs(pageable);
        }

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

    @GetMapping("/search-iban")
    public ResponseEntity<List<String>> searchIbans(@RequestParam String name, Authentication authentication) {

        int userId = jwtParser.getTokenUserId(authentication);
        List<String> ibans = transactionService.searchCounterpartyIbans(userId, name);
        return ResponseEntity.ok(ibans);
    }

}
