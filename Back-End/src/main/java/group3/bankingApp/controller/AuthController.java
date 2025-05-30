package group3.bankingApp.controller;

import group3.bankingApp.model.*;
import group3.bankingApp.model.enums.*;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.security.JwtTokenProvider;
import group3.bankingApp.services.AccountService;

import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/user/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public AuthController(UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            BCryptPasswordEncoder passwordEncoder,
            AccountService accountService) {

        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username already taken");
            }

            // Set user defaults
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.CUSTOMER);
            user.setVerifyUser(VerifyStatus.PENDING);

            // Save user
            User savedUser = userRepository.save(user);
            userRepository.flush(); // Ensures data is written to DB immediately

            // Create default accounts (Checking + Saving)
            accountService.createDefaultAccountsForUser(savedUser.getUserId());

            // Return success response
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User registered successfully"));
        } catch (Exception e) {
            e.printStackTrace(); // Good for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        // FIXME: dont call repository through the controller, use a service class

        String username = request.get("username");
        String password = request.get("password");

        System.out.println("Login attempt for username: '" + username + "'");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password must not be null");
        }

        username = username.trim(); // safe default

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            System.out.println("Username not found in database.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
        }

        User user = optionalUser.get();
        System.out.println("Found user: " + user.getUsername());
        System.out.println("VerifyStatus: " + user.getVerifyUser());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Incorrect password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        if (user.getVerifyUser() != VerifyStatus.ACTIVE) {
            System.out.println("User not verified: " + user.getVerifyUser());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not yet verified");
        }

        String token = jwtTokenProvider.createToken(username, user.getRole(), user.getUserId().longValue());
        System.out.println("[INFO] Token generated successfully for: " + username);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "username", username,
                "role", user.getRole(),
                "userId", user.getUserId()));

    }

}
