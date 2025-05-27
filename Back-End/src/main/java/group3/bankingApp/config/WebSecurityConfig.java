package group3.bankingApp.config;

import group3.bankingApp.security.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    //FIXEME: make sure to change the security filter
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors() // enable global CORS (your CorsConfig)
            .and()
            .csrf().disable()
            .headers().frameOptions().disable() //allow H2 console (uses frames)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/h2-console/**").permitAll() //allow H2 console
                .requestMatchers("/api/user/auth/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtTokenFilter(jwtTokenProvider, userDetailsService),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
