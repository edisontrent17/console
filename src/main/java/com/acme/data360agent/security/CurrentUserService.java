package com.acme.data360agent.security;

import com.acme.data360agent.config.SecurityProperties;
import com.acme.data360agent.identity.IdentityService;
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
    private final SecurityProperties properties;
    private final IdentityService identities;

    public CurrentUserService(SecurityProperties properties, IdentityService identities) {
        this.properties = properties;
        this.identities = identities;
    }

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
        return new AppUser(null, organizationId(authentication), null, name, name, authorities, true);
    }

    public String actor() {
        return currentUser().username();
    }

    public String organizationId() {
        var user = currentUser();
        if (user.organizationId() != null && !user.organizationId().isBlank()) {
            return user.organizationId();
        }
        if (user.authenticated()) {
            throw new IllegalStateException("Authenticated user is missing organization claim: " + properties.resolvedOrganizationClaim());
        }
        return identities.defaultOrganizationId();
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

    private String organizationId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            var organizationId = Optional.ofNullable(jwt.getClaimAsString(properties.resolvedOrganizationClaim()))
                    .filter(value -> !value.isBlank())
                    .orElse(null);
            if (organizationId != null && !properties.resolvedAllowedOrganizations().isEmpty()
                    && !properties.resolvedAllowedOrganizations().contains(organizationId)) {
                throw new IllegalStateException("Authenticated user organization is not allowed: " + organizationId);
            }
            return organizationId;
        }
        return null;
    }
}
