package group3.bankingApp.controller;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import group3.bankingApp.DTO.ATMOperationRequestDTO;
import group3.bankingApp.DTO.ATMSessionRequestDTO;
import group3.bankingApp.DTO.ATMSessionResponseDTO;
import group3.bankingApp.DTO.ATMTransactionResponseDTO;
import group3.bankingApp.DTO.AccountSummaryDTO;
import group3.bankingApp.model.ATMSession;
import group3.bankingApp.model.ATMTransaction;
import group3.bankingApp.model.Account;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.services.ATMService;
import group3.bankingApp.util.JwtTokenParser;

@RestController
@RequestMapping("/api/atm")
public class ATMController {

    private final ATMService        atmService;
    private final AccountRepository accountRepository;
    private final JwtTokenParser    parser;

    public ATMController(ATMService atmService, AccountRepository accountRepository) {
        this.atmService = atmService;
        this.accountRepository = accountRepository;
        this.parser = new JwtTokenParser();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountSummaryDTO>> getAccountsForCurrentUser(Authentication auth) {
        Integer userId = parser.getTokenUserId(auth);

        List<AccountSummaryDTO> summaries = accountRepository.findByUserId(userId)
            .stream()
            .map(acc -> new AccountSummaryDTO(
                acc.getAccountId(),
                acc.getBalance(),
                acc.getAccountType().toString()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    @PostMapping("/session")
    public ResponseEntity<ATMSessionResponseDTO> startSession(
            @RequestBody ATMSessionRequestDTO request,
            Authentication auth
    ) {
        Integer userId    = parser.getTokenUserId(auth);
        Integer accountId = request.getAccountId();

        Account account = accountRepository.findById(accountId)
            .filter(a -> a.getUserId().equals(userId))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Unauthorized access to account"
            ));

        ATMSession session = atmService.startSession(account.getAccountId());

        ATMSessionResponseDTO response = new ATMSessionResponseDTO(
            session.getSessionId(),
            session.getAccountId(),
            session.getLoginTime().atZone(ZoneId.systemDefault()).toInstant()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ATMTransactionResponseDTO> deposit(
            @RequestBody ATMOperationRequestDTO request,
            Authentication auth
    ) {
        Integer userId    = parser.getTokenUserId(auth);
        Integer sessionId = request.getSessionId();
        Integer accountId = request.getAccountId();
        Integer amount    = request.getAmount();

        ATMTransaction txn = atmService.deposit(sessionId, accountId, userId, amount);

        ATMTransactionResponseDTO response = new ATMTransactionResponseDTO(
            txn.getId(),
            txn.getSession().getSessionId(),
            txn.getType(),
            txn.getAmount(),
            txn.getStatus()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ATMTransactionResponseDTO> withdraw(
            @RequestBody ATMOperationRequestDTO request,
            Authentication auth
    ) {
        Integer userId    = parser.getTokenUserId(auth);
        Integer sessionId = request.getSessionId();
        Integer accountId = request.getAccountId();
        Integer amount    = request.getAmount();

        ATMTransaction txn = atmService.withdraw(sessionId, accountId, userId, amount);

        ATMTransactionResponseDTO response = new ATMTransactionResponseDTO(
            txn.getId(),
            txn.getSession().getSessionId(),
            txn.getType(),
            txn.getAmount(),
            txn.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}
