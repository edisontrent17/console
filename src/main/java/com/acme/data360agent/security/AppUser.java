package com.acme.data360agent.security;

import com.acme.data360agent.identity.LocalUserPrincipal;

import java.util.Set;
import java.util.stream.Collectors;

public record AppUser(
        String userId,
        String organizationId,
        String organizationName,
        String username,
        String email,
        Set<String> authorities,
        boolean authenticated
) {
    public AppUser(String username, Set<String> authorities, boolean authenticated) {
        this(null, null, null, username, username, authorities, authenticated);
    }

    public static AppUser anonymous() {
        return new AppUser(null, null, null, "anonymous", null, Set.of(), false);
    }

    public static AppUser from(LocalUserPrincipal principal) {
        var user = principal.user();
        return new AppUser(
                user.id(),
                user.organizationId(),
                user.organizationName(),
                user.displayName(),
                user.email(),
                principal.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.toUnmodifiableSet()),
                true
        );
    }
}
