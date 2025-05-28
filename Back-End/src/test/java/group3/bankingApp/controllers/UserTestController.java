package group3.bankingApp.controllers;

import group3.bankingApp.controller.UserController;
import group3.bankingApp.model.User;
import group3.bankingApp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@WithMockUser
public class UserTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void testGetAllUsers() throws Exception {
        User u1 = new User(); u1.setUserId(1); u1.setUsername("alice");
        User u2 = new User(); u2.setUserId(2); u2.setUsername("bob");
        when(userService.findAll()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/user")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].username").value("alice"))
           .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        User u = new User(); u.setUserId(42); u.setUsername("charlie");
        when(userService.getUserById(42)).thenReturn(u);

        mockMvc.perform(get("/api/user/42")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.userId").value(42)); 
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(99)).thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: 99")
        );

        mockMvc.perform(get("/api/user/99")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }
}