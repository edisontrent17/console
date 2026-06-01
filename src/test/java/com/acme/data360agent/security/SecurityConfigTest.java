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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    void allowsPlanScopeToImportPlans() throws Exception {
        var body = """
                {
                  "schemaVersion": "data360-asl-profile-2026-05-31",
                  "id": "plan_security_import",
                  "goal": "Preview audience",
                  "context": { "org": "org", "dataspace": "default", "environment": "sandbox" },
                  "definition": {
                    "Version": "1.0",
                    "QueryLanguage": "JSONPath",
                    "StartAt": "preview",
                    "States": {
                      "preview": {
                        "Type": "Task",
                        "Comment": "Preview audience",
                        "Resource": "urn:salesforce:data360:capability:query",
                        "Parameters": { "sql": "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10" },
                        "ResultPath": "$.preview",
                        "End": true
                      }
                    }
                  }
                }
                """;

        mvc.perform(post("/api/plans/import")
                        .contentType("application/json")
                        .content(body)
                        .with(jwt().authorities(scope("data360.read"))))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/plans/import")
                        .contentType("application/json")
                        .content(body)
                        .with(jwt().jwt(token -> token.claim("organization_id", "org_security_test"))
                                .authorities(scope("data360.plan"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void separatesPlanApprovalFromRunExecutionScopes() throws Exception {
        mvc.perform(post("/api/plans/plan_security/approve").with(jwt().authorities(scope("data360.execute"))))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/plans/plan_security/approve")
                        .with(jwt().jwt(token -> token.claim("organization_id", "org_security_test"))
                                .authorities(scope("data360.approve"))))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/plans/plan_security/runs").with(jwt().authorities(scope("data360.approve"))))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/plans/plan_security/runs")
                        .with(jwt().jwt(token -> token.claim("organization_id", "org_security_test"))
                                .authorities(scope("data360.execute"))))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/runs/run_security/cancel").with(jwt().authorities(scope("data360.approve"))))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/runs/run_security/cancel")
                        .contentType("application/json")
                        .content("{\"reason\":\"test\"}")
                        .with(jwt().jwt(token -> token.claim("organization_id", "org_security_test"))
                                .authorities(scope("data360.execute"))))
                .andExpect(status().isBadRequest());
    }

    private SimpleGrantedAuthority scope(String scope) {
        return new SimpleGrantedAuthority("SCOPE_" + scope);
    }
}
