package group3.bankingApp.security;

import group3.bankingApp.model.User;
import group3.bankingApp.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Fetch from DB
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert our custom User to Spring’s UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
