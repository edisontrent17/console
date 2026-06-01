package com.acme.data360agent.identity;

import java.io.Serializable;
import java.time.Instant;

public record Organization(
        String id,
        String name,
        Instant createdAt
) implements Serializable {
}
