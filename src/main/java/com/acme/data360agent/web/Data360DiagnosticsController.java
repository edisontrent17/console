package com.acme.data360agent.web;

import com.acme.data360agent.data360.Data360DiagnosticsResult;
import com.acme.data360agent.data360.Data360DiagnosticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data360/diagnostics")
public class Data360DiagnosticsController {
    private final Data360DiagnosticsService diagnostics;

    public Data360DiagnosticsController(Data360DiagnosticsService diagnostics) {
        this.diagnostics = diagnostics;
    }

    @GetMapping
    public Data360DiagnosticsResult diagnostics() {
        return diagnostics.diagnostics();
    }

    @PostMapping("/smoke")
    public Data360DiagnosticsResult smoke() {
        return diagnostics.smokeMetadata();
    }
}
