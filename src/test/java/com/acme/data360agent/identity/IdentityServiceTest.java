package com.acme.data360agent.identity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:identity-service-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false"
})
class IdentityServiceTest {
    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanIdentityTables() {
        jdbc.update("DELETE FROM user_sessions");
        jdbc.update("DELETE FROM llm_settings");
        jdbc.update("DELETE FROM app_users");
        jdbc.update("DELETE FROM organizations");
    }

    @Test
    void bootstrapsFirstOrganizationAndSession() {
        var login = identities.bootstrap(new IdentityRequests.BootstrapRequest(
                "Northern Trail",
                "admin@example.com",
                "Admin User",
                "password123"
        ));

        assertThat(login.token()).isNotBlank();
        assertThat(login.principal().user().organizationName()).isEqualTo("Northern Trail");
        assertThat(login.principal().getAuthorities())
                .extracting("authority")
                .contains("ROLE_DATA360_ADMIN", "SCOPE_data360.admin");
        assertThat(identities.principalForSession(login.token())).isPresent();
        assertThat(identities.setupRequired()).isFalse();
    }

    @Test
    void rejectsInvalidPassword() {
        identities.bootstrap(new IdentityRequests.BootstrapRequest(
                "Acme",
                "owner@example.com",
                "Owner",
                "password123"
        ));

        assertThatThrownBy(() -> identities.login("owner@example.com", "wrong-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
