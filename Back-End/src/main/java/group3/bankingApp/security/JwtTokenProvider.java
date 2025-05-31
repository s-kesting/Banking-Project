package group3.bankingApp.security;

import group3.bankingApp.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long jwtExpirationInMs = 3600000; // 1 hour
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public String createToken(String username, Role role, Long userId) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", role.getAuthority());
        claims.put("userId", userId);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationInMs);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            logger.info("JWT token validation succeded");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.info("JWT token validation failed");
            return false;
        }
    }

    public Authentication getAuthentication(String token, UserDetailsService userDetailsService) {
        String username = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
