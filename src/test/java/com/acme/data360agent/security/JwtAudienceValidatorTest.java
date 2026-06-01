package com.acme.data360agent.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAudienceValidatorTest {
    @Test
    void rejectsMissingAudience() {
        var validator = new JwtAudienceValidator("data360-agent-console");

        assertThat(validator.validate(jwt(List.of("other"))).hasErrors()).isTrue();
        assertThat(validator.validate(jwt(List.of("data360-agent-console"))).hasErrors()).isFalse();
    }

    private Jwt jwt(List<String> audience) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of("sub", "user@example.com", "aud", audience)
        );
    }
}
