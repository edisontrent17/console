package com.acme.data360agent.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserServiceTest {
    private final CurrentUserService users = new CurrentUserService();

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
}
