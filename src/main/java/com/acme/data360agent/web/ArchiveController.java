package com.acme.data360agent.web;

import com.acme.data360agent.archive.ArchiveService;
import com.acme.data360agent.archive.ExecutionLogArchive;
import com.acme.data360agent.archive.PlanSpecArchive;
import com.acme.data360agent.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArchiveController {
    private final ArchiveService archives;
    private final CurrentUserService users;

    public ArchiveController(ArchiveService archives, CurrentUserService users) {
        this.archives = archives;
        this.users = users;
    }

    @GetMapping("/plans/{planId}/export")
    public PlanSpecArchive exportPlan(@PathVariable String planId) {
        return archives.planSpec(users.organizationId(), planId);
    }

    @GetMapping("/runs/{runId}/export")
    public ExecutionLogArchive exportRun(@PathVariable String runId) {
        return archives.executionLog(users.organizationId(), runId);
    }
}
