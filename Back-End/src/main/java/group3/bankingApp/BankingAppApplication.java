package group3.bankingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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
	
	@GetMapping("/customer/{id:[0-9]+}/account")
	public String getAccounts(@PathVariable("id") String id) {
    return id;
	}
	
}

