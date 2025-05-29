package group3.bankingApp.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import group3.bankingApp.model.enums.Role;
import group3.bankingApp.security.CustomUserDetails;

public class JwtTokenParser {

    public int getTokenUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        } else {
            throw new Error("UserId not found in token");
        }

    }

    public String getTokenUsername(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        } else {
            throw new Error("Username not found in token");
        }
    }

    public boolean validateTokenRole(Authentication authentication, Role role) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Collection<? extends GrantedAuthority> tokenRoles = ((CustomUserDetails) authentication.getPrincipal())
                    .getAuthorities();
            return tokenRoles.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(role.toString()));
        } else {
            throw new Error("Username not found in token");
        }
    }
}
