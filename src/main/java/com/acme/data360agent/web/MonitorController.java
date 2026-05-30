package com.acme.data360agent.web;

import com.acme.data360agent.monitor.MonitorDefinition;
import com.acme.data360agent.monitor.MonitorRun;
import com.acme.data360agent.monitor.MonitorService;
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

    public MonitorController(MonitorService monitors) {
        this.monitors = monitors;
    }

    @GetMapping
    public List<MonitorDefinition> all() {
        return monitors.all();
    }

    @GetMapping("/{monitorId}")
    public MonitorDefinition definition(@PathVariable String monitorId) {
        return monitors.definition(monitorId);
    }

    @GetMapping("/{monitorId}/runs")
    public List<MonitorRun> runs(@PathVariable String monitorId) {
        return monitors.runsFor(monitorId);
    }

    @PostMapping("/{monitorId}/run-now")
    public MonitorRun runNow(@PathVariable String monitorId) {
        return monitors.runNow(monitorId);
    }
}
