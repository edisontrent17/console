package com.acme.data360agent.web;

import com.acme.data360agent.mcp.McpSettings;
import com.acme.data360agent.mcp.McpSettingsRequest;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.mcp.McpSettingsValidation;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp-settings")
public class McpSettingsController {
    private final McpSettingsService settings;
    private final McpToolRegistryService registry;

    public McpSettingsController(McpSettingsService settings, McpToolRegistryService registry) {
        this.settings = settings;
        this.registry = registry;
    }

    @GetMapping
    public McpSettings current() {
        return settings.current().redacted();
    }

    @PutMapping
    public McpSettings save(@Valid @RequestBody McpSettingsRequest request) {
        var saved = settings.save(request);
        registry.invalidate();
        return saved.redacted();
    }

    @PostMapping("/validate")
    public McpSettingsValidation validate() {
        return settings.validateCurrent();
    }

    @GetMapping("/tools")
    public McpToolRegistrySnapshot tools() {
        return registry.discover();
    }
}
