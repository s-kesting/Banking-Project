package group3.bankingApp.services;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

import group3.bankingApp.DTO.UserWithAccountsDTO;
import group3.bankingApp.model.Account;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    //R/////////////Robben ---- List their user and accounts
    public Map<String, Object> getPaginatedUserDTOs(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserWithAccountsDTO> pageResult;

        if (username == null || username.isBlank()) {
            // Fallback to original query without filtering
            pageResult = userRepository.findAllUsersWithAccounts(pageable);  
        } else {
            pageResult = userRepository.findUsersWithAccountsByUsername(username, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", pageResult.getContent());
        response.put("totalPages", pageResult.getTotalPages());
        return response;
    }


    //////////////Robben ---- UPDATE VARIFICATION STATUS
    public void updateUserVerificationStatus(Integer userId, String newStatusStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        VerifyStatus newStatus = VerifyStatus.valueOf(newStatusStr);
        boolean wasPending = user.getVerifyUser() != VerifyStatus.ACTIVE;

        user.setVerifyUser(newStatus);
        userRepository.save(user);

        List<Account> accounts = accountRepository.findByUserId(userId);

        // Sync account status with user
        if (newStatus == VerifyStatus.PENDING || newStatus == VerifyStatus.REJECTED || newStatus == VerifyStatus.ACTIVE) {
            for (Account acc : accounts) {
                acc.setVerifyAccount(newStatus); // Matching enum!
            }
            accountRepository.saveAll(accounts);
        }

        //Create default accounts only on first-time ACTIVE
        if (newStatus == VerifyStatus.ACTIVE && wasPending && accounts.isEmpty()) {
            accountService.createDefaultAccountsForUser(userId);
        }
    }



}
