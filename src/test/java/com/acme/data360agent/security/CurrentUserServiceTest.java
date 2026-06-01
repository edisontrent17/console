package com.acme.data360agent.security;

import com.acme.data360agent.config.SecurityProperties;
import com.acme.data360agent.identity.IdentityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentUserServiceTest {
    private IdentityService identities;
    private CurrentUserService users;

    @BeforeEach
    void setUp() {
        identities = mock(IdentityService.class);
        users = new CurrentUserService(new SecurityProperties(null, null, null, null, null, null, null), identities);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void treatsAnonymousAuthenticationAsDemoMode() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        ));

        assertThat(users.currentUser()).isEqualTo(AppUser.anonymous());
    }

    @Test
    void usesDefaultOrganizationForAnonymousMode() {
        when(identities.defaultOrganizationId()).thenReturn("org_default");

        assertThat(users.organizationId()).isEqualTo("org_default");
    }

    @Test
    void readsOrganizationFromJwtClaim() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtWith("organization_id", "org_acme"),
                List.of(new SimpleGrantedAuthority("SCOPE_data360.plan"))));

        var user = users.currentUser();

        assertThat(user.organizationId()).isEqualTo("org_acme");
        assertThat(user.username()).isEqualTo("agent@example.com");
        assertThat(users.organizationId()).isEqualTo("org_acme");
    }

    @Test
    void failsClosedWhenAuthenticatedJwtIsMissingOrganizationClaim() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtWith(null, null),
                List.of(new SimpleGrantedAuthority("SCOPE_data360.plan"))));

        assertThatThrownBy(users::organizationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("organization_id");
    }

    @Test
    void honorsConfiguredOrganizationClaim() {
        users = new CurrentUserService(new SecurityProperties(null, null, null, "tenant", null, null, null), identities);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtWith("tenant", "tenant_1"),
                List.of(new SimpleGrantedAuthority("SCOPE_data360.plan"))));

        assertThat(users.organizationId()).isEqualTo("tenant_1");
    }

    @Test
    void rejectsJwtOrganizationOutsideAllowlist() {
        users = new CurrentUserService(new SecurityProperties(null, null, null, null, null, null, List.of("org_allowed")), identities);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtWith("organization_id", "org_blocked"),
                List.of(new SimpleGrantedAuthority("SCOPE_data360.plan"))));

        assertThatThrownBy(users::organizationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not allowed");
    }

    private Jwt jwtWith(String organizationClaim, String organizationId) {
        var builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("subject-1")
                .claim("preferred_username", "agent@example.com");
        if (organizationClaim != null) {
            builder.claim(organizationClaim, organizationId);
        }
        return builder.build();
    }
}
