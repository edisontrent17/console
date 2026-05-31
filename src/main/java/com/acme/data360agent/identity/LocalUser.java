package com.acme.data360agent.identity;

import java.io.Serializable;
import java.time.Instant;

public record LocalUser(
        String id,
        String organizationId,
        String organizationName,
        String email,
        String displayName,
        String passwordHash,
        String role,
        String status,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
    public boolean active() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
