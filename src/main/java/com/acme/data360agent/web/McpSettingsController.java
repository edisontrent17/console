package com.acme.data360agent.web;

import com.acme.data360agent.mcp.McpSettings;
import com.acme.data360agent.mcp.McpSettingsRequest;
import com.acme.data360agent.mcp.McpSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp-settings")
public class McpSettingsController {
    private final McpSettingsService settings;

    public McpSettingsController(McpSettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public McpSettings current() {
        return settings.current();
    }

    @PutMapping
    public McpSettings save(@Valid @RequestBody McpSettingsRequest request) {
        return settings.save(request);
    }
}
