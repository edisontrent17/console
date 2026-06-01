package com.acme.data360agent.monitor;

import com.acme.data360agent.security.CurrentUserService;
import com.acme.data360agent.web.MonitorController;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitorControllerTest {
    @Test
    void usesCurrentOrganizationForMonitorAccess() {
        var monitors = mock(MonitorService.class);
        var users = mock(CurrentUserService.class);
        var controller = new MonitorController(monitors, users);
        var definition = definition("mon_1", "org_a");
        var run = new MonitorRun("monrun_1", "mon_1", 0.2, true, MonitorStatus.ATTENTION_REQUIRED, "Review", Map.of(), Instant.now());
        var recommendation = recommendation("monrec_1", "org_a");

        when(users.organizationId()).thenReturn("org_a");
        when(users.actor()).thenReturn("reviewer");
        when(monitors.all("org_a")).thenReturn(List.of(definition));
        when(monitors.recommendations("org_a")).thenReturn(List.of(recommendation));
        when(monitors.definition("org_a", "mon_1")).thenReturn(definition);
        when(monitors.runsFor("org_a", "mon_1")).thenReturn(List.of(run));
        when(monitors.runNow("org_a", "mon_1")).thenReturn(run);
        when(monitors.approveRecommendation("org_a", "monrec_1", "reviewer")).thenReturn(recommendation);
        when(monitors.rejectRecommendation("org_a", "monrec_1", "reviewer")).thenReturn(recommendation);

        controller.all();
        controller.recommendations();
        controller.definition("mon_1");
        controller.runs("mon_1");
        controller.runNow("mon_1");
        controller.approveRecommendation("monrec_1");
        controller.rejectRecommendation("monrec_1");

        verify(monitors).all("org_a");
        verify(monitors).recommendations("org_a");
        verify(monitors).definition("org_a", "mon_1");
        verify(monitors).runsFor("org_a", "mon_1");
        verify(monitors).runNow("org_a", "mon_1");
        verify(monitors).approveRecommendation("org_a", "monrec_1", "reviewer");
        verify(monitors).rejectRecommendation("org_a", "monrec_1", "reviewer");
    }

    private MonitorDefinition definition(String monitorId, String organizationId) {
        return new MonitorDefinition(
                monitorId,
                organizationId,
                "plan_1",
                "run_1",
                "step_1",
                "activation_rate",
                "daily",
                Map.of("operator", "<", "value", 0.13),
                MonitorStatus.ACTIVE,
                Instant.now(),
                null
        );
    }

    private MonitorRecommendation recommendation(String recommendationId, String organizationId) {
        return new MonitorRecommendation(
                recommendationId,
                organizationId,
                "mon_1",
                "monrun_1",
                "activation_rate",
                0.2,
                Map.of("operator", "<", "value", 0.13),
                "Review",
                MonitorRecommendationStatus.PENDING_APPROVAL,
                Instant.now(),
                null
        );
    }
}
