package com.acme.data360agent.plan;

import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class PlanValidator {
    private static final Pattern SQL_LIMIT = Pattern.compile("\\blimit\\s+\\d+\\b", Pattern.CASE_INSENSITIVE);
    private final OperationRegistry operations;

    public PlanValidator(OperationRegistry operations) {
        this.operations = operations;
    }

    public PlanValidationResult validate(PlanSpec plan) {
        var issues = new ArrayList<ValidationIssue>();
        if (plan == null) {
            return new PlanValidationResult(List.of(ValidationIssue.error(null, "Plan is missing.")));
        }

        if (plan.context() == null) {
            issues.add(ValidationIssue.error(null, "Plan context is required."));
        } else if ("production".equals(plan.context().environment())) {
            issues.add(ValidationIssue.warning(null, "Production environment selected. Every write/publish/activation step must require approval."));
        }

        var seen = new HashSet<String>();
        for (var step : plan.steps()) {
            if (!seen.add(step.id())) {
                issues.add(ValidationIssue.error(step.id(), "Duplicate step id."));
            }
            for (var dependency : step.dependsOn()) {
                if (!seen.contains(dependency)) {
                    issues.add(ValidationIssue.error(step.id(), "Dependency must refer to an earlier step: " + dependency));
                }
            }

            var definition = operations.require(step.action());
            var input = step.input();

            for (var required : definition.requiredAllOf()) {
                if (!hasNonBlank(input.get(required))) {
                    issues.add(ValidationIssue.error(step.id(), "Missing required input: " + required));
                }
            }

            if (!definition.requiredAnyOf().isEmpty()) {
                var hasOne = definition.requiredAnyOf().stream().anyMatch(key -> hasNonBlank(input.get(key)));
                if (!hasOne) {
                    issues.add(ValidationIssue.error(step.id(), "Requires one of: " + String.join(", ", definition.requiredAnyOf())));
                }
            }

            if (definition.alwaysRequiresApproval() && !step.needsApproval()) {
                issues.add(ValidationIssue.error(step.id(), definition.effect().name().toLowerCase(Locale.ROOT) + " step must set needsApproval=true."));
            }

            if (definition.effect() != Effect.READ && "production".equals(plan.context().environment()) && !step.needsApproval()) {
                issues.add(ValidationIssue.error(step.id(), "Production mutation steps require explicit approval."));
            }

            if (step.action() == Data360Action.QUERY) {
                validateQuery(step, issues);
            }
        }
        return new PlanValidationResult(List.copyOf(issues));
    }

    private void validateQuery(PlanStep step, ArrayList<ValidationIssue> issues) {
        var sql = String.valueOf(step.input().getOrDefault("sql", ""));
        if (sql.isBlank()) {
            return;
        }
        if (!sql.trim().toLowerCase(Locale.ROOT).startsWith("select")) {
            issues.add(ValidationIssue.error(step.id(), "Only SELECT queries are allowed in the MVP."));
        }
        if (!SQL_LIMIT.matcher(sql).find() && !step.input().containsKey("limit")) {
            issues.add(ValidationIssue.error(step.id(), "Query steps must include a LIMIT or a limit input."));
        }
        if (sql.contains(";")) {
            issues.add(ValidationIssue.error(step.id(), "SQL must contain a single statement without semicolons."));
        }
    }

    private boolean hasNonBlank(Object value) {
        if (value == null) {
            return false;
        }
        return !(value instanceof String string) || !string.isBlank();
    }
}
