package com.acme.data360agent.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class IdentityRequests {
    private IdentityRequests() {
    }

    public record BootstrapRequest(
            @NotBlank @Size(max = 255) String organizationName,
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(max = 255) String displayName,
            @NotBlank @Size(min = 8, max = 200) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(max = 200) String password
    ) {
    }

    public record CreateOrganizationRequest(
            @NotBlank @Size(max = 255) String name
    ) {
    }

    public record CreateUserRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(max = 255) String displayName,
            @NotBlank @Size(min = 8, max = 200) String password,
            String role
    ) {
    }
}
