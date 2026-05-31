package com.acme.data360agent.web;

import com.acme.data360agent.archive.ArchiveService;
import com.acme.data360agent.archive.ExecutionLogArchive;
import com.acme.data360agent.archive.PlanSpecArchive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArchiveController {
    private final ArchiveService archives;

    public ArchiveController(ArchiveService archives) {
        this.archives = archives;
    }

    @GetMapping("/plans/{planId}/export")
    public PlanSpecArchive exportPlan(@PathVariable String planId) {
        return archives.planSpec(planId);
    }

    @GetMapping("/runs/{runId}/export")
    public ExecutionLogArchive exportRun(@PathVariable String runId) {
        return archives.executionLog(runId);
    }
}
