package com.acme.data360agent.web;

import com.acme.data360agent.monitor.MonitorDefinition;
import com.acme.data360agent.monitor.MonitorRecommendation;
import com.acme.data360agent.monitor.MonitorRun;
import com.acme.data360agent.monitor.MonitorService;
import com.acme.data360agent.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {
    private final MonitorService monitors;
    private final CurrentUserService users;

    public MonitorController(MonitorService monitors, CurrentUserService users) {
        this.monitors = monitors;
        this.users = users;
    }

    @GetMapping
    public List<MonitorDefinition> all() {
        return monitors.all(users.organizationId());
    }

    @GetMapping("/recommendations")
    public List<MonitorRecommendation> recommendations() {
        return monitors.recommendations(users.organizationId());
    }

    @PostMapping("/recommendations/{recommendationId}/approve")
    public MonitorRecommendation approveRecommendation(@PathVariable String recommendationId) {
        return monitors.approveRecommendation(users.organizationId(), recommendationId, users.actor());
    }

    @PostMapping("/recommendations/{recommendationId}/reject")
    public MonitorRecommendation rejectRecommendation(@PathVariable String recommendationId) {
        return monitors.rejectRecommendation(users.organizationId(), recommendationId, users.actor());
    }

    @GetMapping("/{monitorId}")
    public MonitorDefinition definition(@PathVariable String monitorId) {
        return monitors.definition(users.organizationId(), monitorId);
    }

    @GetMapping("/{monitorId}/runs")
    public List<MonitorRun> runs(@PathVariable String monitorId) {
        return monitors.runsFor(users.organizationId(), monitorId);
    }

    @PostMapping("/{monitorId}/run-now")
    public MonitorRun runNow(@PathVariable String monitorId) {
        return monitors.runNow(users.organizationId(), monitorId);
    }
}
