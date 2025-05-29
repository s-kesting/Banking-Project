package group3.bankingApp.security;

import group3.bankingApp.model.User;
import group3.bankingApp.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;
import group3.bankingApp.security.CustomUserDetails;

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

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(authority);
        return new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(), roles);
        // Convert our custom User to Spring’s UserDetails
    }
}
