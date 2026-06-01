package com.acme.data360agent.web;

import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.library.SolutionLibrary;
import com.acme.data360agent.library.SolutionTemplate;
import com.acme.data360agent.library.TemplatePlanRequest;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final SolutionLibrary library;
    private final PlanValidator validator;
    private final PlanStore store;
    private final CurrentUserService users;

    public LibraryController(SolutionLibrary library, PlanValidator validator, PlanStore store, CurrentUserService users) {
        this.library = library;
        this.validator = validator;
        this.store = store;
        this.users = users;
    }

    @GetMapping
    public List<SolutionTemplate> templates() {
        return library.all();
    }

    @GetMapping("/{templateId}")
    public SolutionTemplate template(@PathVariable String templateId) {
        return library.get(templateId);
    }

    @PostMapping("/{templateId}/plans")
    public PlanDraft instantiate(@PathVariable String templateId, @Valid @RequestBody TemplatePlanRequest request) {
        var plan = library.instantiate(templateId, request.context());
        var draft = new PlanDraft(plan, validator.validate(plan), List.of("solution_library:" + templateId, "validate_plan"));
        return store.saveDraft(users.organizationId(), draft);
    }
}
