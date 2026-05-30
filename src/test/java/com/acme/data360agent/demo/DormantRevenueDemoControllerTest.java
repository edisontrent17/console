package com.acme.data360agent.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DormantRevenueDemoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DormantRevenueDemoControllerTest {
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void resetDemo() throws Exception {
        mvc.perform(post("/api/demo/dormant-revenue-recovery/reset"))
                .andExpect(status().isOk());
    }

    @Test
    void returnsDormantRevenueDemo() throws Exception {
        mvc.perform(get("/api/demo/dormant-revenue-recovery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.title").value("Dormant Revenue Recovery"))
                .andExpect(jsonPath("$.summary.recoverableRevenue").value(1850000))
                .andExpect(jsonPath("$.summary.accountsIdentified").value(12650))
                .andExpect(jsonPath("$.accounts", hasSize(5)))
                .andExpect(jsonPath("$.actions", hasSize(11)))
                .andExpect(jsonPath("$.impact", hasSize(11)))
                .andExpect(jsonPath("$.emailActivation.provider").value("SendGrid"))
                .andExpect(jsonPath("$.emailActivation.sequence", hasSize(3)))
                .andExpect(jsonPath("$.recoveryFunnel", hasSize(10)))
                .andExpect(jsonPath("$.goalRunSpec.id").value("goal-run-dormant-recovery-q2"))
                .andExpect(jsonPath("$.goalRunSpec.plan.steps", hasSize(3)))
                .andExpect(jsonPath("$.goalRunSpec.execute.actions", hasSize(11)))
                .andExpect(jsonPath("$.goalRunSpec.monitor.metrics", hasSize(8)))
                .andExpect(jsonPath("$.goalRunSpec.monitor.state.status").value("waiting_for_activation"));
    }

    @Test
    void approvalTransitionUpdatesActionStatus() throws Exception {
        mvc.perform(post("/api/demo/dormant-revenue-recovery/actions/acme-task/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actions[?(@.id == 'acme-task')].status").value("Approved"));

        mvc.perform(post("/api/demo/dormant-revenue-recovery/actions/acme-task/execute"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actions[?(@.id == 'acme-task')].status").value("Executed"));
    }

    @Test
    void monitorSpecUpdatesAfterEmailExecution() throws Exception {
        mvc.perform(post("/api/demo/dormant-revenue-recovery/actions/northstar-email/execute"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalRunSpec.monitor.state.status").value("monitoring"))
                .andExpect(jsonPath("$.goalRunSpec.monitor.state.eventsIngested").value(true))
                .andExpect(jsonPath("$.goalRunSpec.monitor.metrics[?(@.key == 'clicked')].current").value(626.0))
                .andExpect(jsonPath("$.goalRunSpec.monitor.metrics[?(@.key == 'revenue_recovered')].current").value(275000.0));
    }
}
