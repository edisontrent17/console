package com.acme.data360agent.web;

import com.acme.data360agent.llm.LlmSettings;
import com.acme.data360agent.llm.LlmSettingsRequest;
import com.acme.data360agent.llm.LlmSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llm-settings")
public class LlmSettingsController {
    private final LlmSettingsService settings;

    public LlmSettingsController(LlmSettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public LlmSettings current() {
        return settings.current();
    }

    @PutMapping
    public LlmSettings save(@Valid @RequestBody LlmSettingsRequest request) {
        return settings.save(request);
    }
}
