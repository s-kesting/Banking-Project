package group3.bankingApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group3.bankingApp.model.User;
import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.enums.VerifyStatus;
import group3.bankingApp.repository.UserRepository;

@SpringBootApplication
@RestController
public class BankingAppApplication {


	public static void main(String[] args) {
		SpringApplication.run(BankingAppApplication.class, args);

	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "BANANA") String name) {
		return String.format("ROBBEN SUCK MY %s!", name);
		

	}

	@Bean
    public CommandLineRunner demo(UserRepository repository) {
        return (args) -> {
			Role role = Role.CUSTOMER;
			VerifyStatus status = VerifyStatus.ACTIVE;
            // Create and save a user
            User user = new User();
			user.setEmail("test");
			user.setPassword("test");
			user.setPhoneNumber("test");
			user.setUsername("o");
			user.setRole(role);
			user.setBsn("myass");
			user.setVerifyUser(status);
			

            repository.save(user);
            System.out.println("User saved with ID: " + user.getUserId());
        };
	}
}
