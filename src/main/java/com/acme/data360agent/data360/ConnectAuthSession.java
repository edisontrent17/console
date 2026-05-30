package com.acme.data360agent.data360;

import java.time.Instant;

public record ConnectAuthSession(
        String instanceUrl,
        String accessToken,
        Instant expiresAt
) {
    public boolean expiresSoon(int skewSeconds) {
        return expiresAt != null && expiresAt.minusSeconds(skewSeconds).isBefore(Instant.now());
    }
}
