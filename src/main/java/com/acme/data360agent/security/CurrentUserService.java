package com.acme.data360agent.security;

import com.acme.data360agent.identity.LocalUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CurrentUserService {
    public AppUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return AppUser.anonymous();
        }
        if (authentication.getPrincipal() instanceof LocalUserPrincipal principal) {
            return AppUser.from(principal);
        }
        var name = principalName(authentication);
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());
        return new AppUser(name, authorities, true);
    }

    public String actor() {
        return currentUser().username();
    }

    private String principalName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
                    .or(() -> Optional.ofNullable(jwt.getClaimAsString("email")))
                    .or(() -> Optional.ofNullable(jwt.getSubject()))
                    .orElse(authentication.getName());
        }
        return authentication.getName();
    }
}
