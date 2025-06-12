package group3.bankingApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.bankingApp.controller.AuthController;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.UserRepository;
import group3.bankingApp.security.JwtTokenProvider;
import group3.bankingApp.services.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserRepository userRepository;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private BCryptPasswordEncoder passwordEncoder;
    @MockBean private AccountService accountService;

    @Test
    void testRegisterSuccess() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("rawpass");
        user.setEmail("user@example.com");
        user.setBsn("123456789");
        user.setVerifyUser(VerifyStatus.PENDING);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByBsn("123456789")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(10);
            return u;
        });

        mockMvc.perform(post("/api/user/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {
        User user = new User();
        user.setUsername("taken");
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/user/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Username already taken"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedpass");
        user.setRole(Role.CUSTOMER);
        user.setUserId(123);
        user.setVerifyUser(VerifyStatus.ACTIVE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedpass")).thenReturn(true);
        when(jwtTokenProvider.createToken("user1", Role.CUSTOMER, 123L)).thenReturn("mocktoken");

        Map<String, String> req = Map.of("username", "user1", "password", "pass123");

        mockMvc.perform(post("/api/user/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("mocktoken"))
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedpass");
        user.setVerifyUser(VerifyStatus.ACTIVE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedpass")).thenReturn(false);

        Map<String, String> req = Map.of("username", "user1", "password", "wrongpass");

        mockMvc.perform(post("/api/user/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid password"));
    }

    @Test
    void testLoginUnverifiedUser() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedpass");
        user.setVerifyUser(VerifyStatus.PENDING);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedpass")).thenReturn(true);

        Map<String, String> req = Map.of("username", "user1", "password", "pass");

        mockMvc.perform(post("/api/user/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden())
            .andExpect(content().string("Account not yet verified"));
    }

    @Test
void testLoginMissingFields() throws Exception {
    Map<String, String> loginData = new HashMap<>();
    loginData.put("username", "");
    loginData.put("password", "");

    mockMvc.perform(post("/api/user/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginData)))
        .andExpect(status().isUnauthorized())  // Updated expectation
        .andExpect(content().string("Invalid username"));
}


    @Test
    void testCheckUsername() throws Exception {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(new User()));
        mockMvc.perform(get("/api/user/auth/check-username?username=bob"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void testCheckEmail() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/user/auth/check-email?email=test@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void testCheckBsn() throws Exception {
        when(userRepository.findByBsn("123456789")).thenReturn(Optional.of(new User()));
        mockMvc.perform(get("/api/user/auth/check-bsn?bsn=123456789"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(true));
    }
}
