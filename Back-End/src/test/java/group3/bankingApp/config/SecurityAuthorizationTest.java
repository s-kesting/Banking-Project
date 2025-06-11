package group3.bankingApp.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAuthorizationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private String employeeToken;
    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure employee1 exists
        if (userRepository.findByUsername("employee1").isEmpty()) {
            User employee = new User();
            employee.setUsername("employee1");
            employee.setEmail("employee@example.com");
            employee.setPassword(passwordEncoder.encode("password123"));
            employee.setRole(Role.EMPLOYEE);
            employee.setVerifyUser(VerifyStatus.ACTIVE);
            employee.setBsn("111111111");
            userRepository.save(employee);
        }

        // Ensure customer1 exists
        if (userRepository.findByUsername("customer1").isEmpty()) {
            User customer = new User();
            customer.setUsername("customer1");
            customer.setEmail("customer@example.com");
            customer.setPassword(passwordEncoder.encode("password123"));
            customer.setRole(Role.CUSTOMER);
            customer.setVerifyUser(VerifyStatus.ACTIVE);
            customer.setBsn("222222222");
            userRepository.save(customer);
        }

        employeeToken = loginAndGetToken("employee1", "password123");
        customerToken = loginAndGetToken("customer1", "password123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String loginRequest = String.format("""
            {
                "username": "%s",
                "password": "%s"
            }
            """, username, password);

        MvcResult result = mockMvc.perform(post("/api/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> responseMap = objectMapper.readValue(responseJson, Map.class);
        return (String) responseMap.get("token");
    }

    @Test
    void employeeCanAccessEmployeeApi() throws Exception {
        mockMvc.perform(get("/api/employee/users/paginated")
                .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    void customerCannotAccessEmployeeApi() throws Exception {
        mockMvc.perform(get("/api/employee/users/paginated")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerCanAccessCustomerTransactionApi() throws Exception {
        mockMvc.perform(get("/api/transactions/user")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }

    @Test
    void employeeCannotAccessCustomerTransactionApi() throws Exception {
        mockMvc.perform(get("/api/transactions/user")
                .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicCanAccessRegister() throws Exception {
        String registerRequest = """
            {
                "username": "testuser_" ,
                "email": "test@example.com",
                "password": "password123",
                "bsn": "999999999"
            }
            """;

        mockMvc.perform(post("/api/user/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isOk());
    }
}
