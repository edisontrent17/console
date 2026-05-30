package com.acme.data360agent.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.enabled=true",
        "app.security.required-audience=data360-agent-console",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example.com",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://issuer.example.com/jwks.json",
        "spring.datasource.url=jdbc:h2:mem:data360-agent-security-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void rejectsAnonymousApiRequestsWhenSecurityIsEnabled() throws Exception {
        mvc.perform(get("/api/scenarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requiresReadAuthorityForReadApis() throws Exception {
        mvc.perform(get("/api/scenarios").with(jwt()))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/scenarios").with(jwt().authorities(scope("data360.read"))))
                .andExpect(status().isOk());
    }

    @Test
    void keepsDemoMutationsSeparateFromReadAccess() throws Exception {
        mvc.perform(post("/api/demo/dormant-revenue-recovery/reset").with(jwt().authorities(scope("data360.read"))))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/demo/dormant-revenue-recovery/reset").with(jwt().authorities(scope("data360.demo"))))
                .andExpect(status().isOk());
    }

    @Test
    void restrictsDiagnosticsToAdminAuthority() throws Exception {
        mvc.perform(get("/api/data360/diagnostics").with(jwt().authorities(scope("data360.read"))))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/data360/diagnostics").with(jwt().authorities(scope("data360.admin"))))
                .andExpect(status().isOk());
    }

    private SimpleGrantedAuthority scope(String scope) {
        return new SimpleGrantedAuthority("SCOPE_" + scope);
    }
}
