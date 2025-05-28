package group3.bankingApp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.enums.VerifyStatus;

public class UserTest {
    
    private User user;

    @BeforeEach
    void init() {
        user = new User();
    }

    @Test
    void newUserShouldNotBeNull() {
        assertNotNull(user, "A newly created User should not be null");
    }

    @Test
    void gettersAndSettersShouldWork() {
        user.setUserId(123);
        assertEquals(123, user.getUserId());

        user.setPassword("secret");
        assertEquals("secret", user.getPassword());

        user.setUsername("alice");
        assertEquals("alice", user.getUsername());

        user.setPhoneNumber("555-0101");
        assertEquals("555-0101", user.getPhoneNumber());

        user.setBsn("987654321");
        assertEquals("987654321", user.getBsn());

        user.setEmail("alice@example.com");
        assertEquals("alice@example.com", user.getEmail());

        user.setRole(Role.CUSTOMER);
        assertEquals(Role.CUSTOMER, user.getRole());

        user.setVerifyUser(VerifyStatus.PENDING);
        assertEquals(VerifyStatus.PENDING, user.getVerifyUser());
    }
}