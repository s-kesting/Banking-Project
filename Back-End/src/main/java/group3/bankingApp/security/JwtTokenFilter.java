package group3.bankingApp.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.qos.logback.core.filter.Filter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(1)
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            System.out.println("[INFO] recieved JWT Bearer token");
            String token = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token, userDetailsService);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("[INFO] token was rejected");
            }
            // System.out.println("Authenticated: " + auth.getName() + ", Authorities: " +
            // auth.getAuthorities());
        }

        System.out.println(response.getStatus());
        System.out.println(request);
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
