package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        Boolean enabled,
        String mode,
        String authorityClaim,
        String organizationClaim,
        String requiredAudience,
        List<String> allowedOrigins,
        List<String> allowedOrganizations
) {
    public boolean resolvedEnabled() {
        return enabled == null || enabled;
    }

    public String resolvedAuthorityClaim() {
        return authorityClaim == null || authorityClaim.isBlank() ? "scope" : authorityClaim;
    }

    public String resolvedOrganizationClaim() {
        return organizationClaim == null || organizationClaim.isBlank() ? "organization_id" : organizationClaim;
    }

    public String resolvedMode() {
        if (!resolvedEnabled()) {
            return "disabled";
        }
        return mode == null || mode.isBlank() ? "jwt" : mode;
    }

    public List<String> resolvedAllowedOrigins() {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return List.of("http://localhost:8080");
        }
        return List.copyOf(allowedOrigins);
    }

    public List<String> resolvedAllowedOrganizations() {
        if (allowedOrganizations == null || allowedOrganizations.isEmpty()) {
            return List.of();
        }
        return List.copyOf(allowedOrganizations);
    }
}
