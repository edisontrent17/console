package com.acme.data360agent.web;

import com.acme.data360agent.scenario.CustomerScenario;
import com.acme.data360agent.scenario.ScenarioLibrary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
public class ScenarioController {
    private final ScenarioLibrary scenarios;

    public ScenarioController(ScenarioLibrary scenarios) {
        this.scenarios = scenarios;
    }

    @GetMapping
    public List<CustomerScenario> all() {
        return scenarios.all();
    }
}
