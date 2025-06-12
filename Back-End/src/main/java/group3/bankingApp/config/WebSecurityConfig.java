package group3.bankingApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import group3.bankingApp.security.CustomUserDetailsService;
import group3.bankingApp.security.JwtTokenFilter;
import group3.bankingApp.security.JwtTokenProvider;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) //
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/**").permitAll()
                        .requestMatchers("/api/user/auth/register").permitAll()
                        .requestMatchers("/api/user/auth/login").permitAll()
                        .requestMatchers("/api/user/auth/check-username").permitAll()
                        .requestMatchers("/api/user/auth/check-email").permitAll()
                        .requestMatchers("/api/user/auth/check-bsn").permitAll()
                        .requestMatchers("/atm/**").permitAll()
                        
                        .requestMatchers("/api/transactions/search-iban").hasAuthority("CUSTOMER")
                       // .requestMatchers("/api/transactions/**").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/employee/**").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/employee/users/paginated").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/employee/users/{userId}/verify").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/employee/accounts/{accountId}").hasAuthority("EMPLOYEE")
                       // .requestMatchers("/api/transactions/user/**").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/transactions/paginated").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/transactions/employee-transfer").hasAuthority("EMPLOYEE")
                        .requestMatchers("/api/transactions/user").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/transactions/Iban").hasAuthority("CUSTOMER")
                    

                       
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
