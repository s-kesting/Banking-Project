package group3.bankingApp.routing;

import org.springframework.web.bind.annotation.GetMapping;

public class Authentication{

	@GetMapping("/auth/register")
	public String register() {
    return "testing authentication path";
	}

}
