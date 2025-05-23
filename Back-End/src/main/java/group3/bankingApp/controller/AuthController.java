package group3.bankingApp.controller;

import group3.bankingApp.model.*;
import group3.bankingApp.model.enums.*;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.security.JwtTokenProvider;
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

    public AuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER); // default or based on your form
        user.setVerifyUser(VerifyStatus.PENDING); // sets verification status
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

        @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
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

        String token = jwtTokenProvider.createToken(username, user.getRole());
        System.out.println("Token generated successfully for: " + username);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username,
                "role", user.getRole()
        ));
    }


}
