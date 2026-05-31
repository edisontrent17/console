package com.acme.data360agent.identity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public record PublicUser(
        String id,
        String organizationId,
        String organizationName,
        String email,
        String displayName,
        String role,
        String status,
        Set<String> authorities,
        Instant createdAt
) implements Serializable {
    public static PublicUser from(LocalUser user, Set<String> authorities) {
        return new PublicUser(
                user.id(),
                user.organizationId(),
                user.organizationName(),
                user.email(),
                user.displayName(),
                user.role(),
                user.status(),
                Set.copyOf(authorities),
                user.createdAt()
        );
    }
}
