package com.acme.data360agent.plan;

import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
        var isProduction = plan.context() != null && "production".equals(plan.context().environment());

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
            for (var binding : step.inputBindings().entrySet()) {
                if (!seen.contains(binding.getValue().fromStep())) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding must refer to an earlier step: " + binding.getValue().fromStep()));
                }
                if (!binding.getValue().path().startsWith("$.")) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding path must start with $."));
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

            if (definition.effect() != Effect.READ && isProduction && !step.needsApproval()) {
                issues.add(ValidationIssue.error(step.id(), "Production mutation steps require explicit approval."));
            }

            if (step.action() == Data360Action.QUERY) {
                validateQuery(step, issues);
            }
            validatePhase(step, definition, issues);
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

    private void validatePhase(PlanStep step, com.acme.data360agent.operation.OperationDefinition definition, ArrayList<ValidationIssue> issues) {
        if (step.phase() == PlanPhase.MONITOR && step.action() != Data360Action.MONITOR_METRIC) {
            issues.add(ValidationIssue.error(step.id(), "Monitor phase currently allows only data360.monitor.metric."));
        }
        if (step.action() == Data360Action.MONITOR_METRIC && step.phase() != PlanPhase.MONITOR) {
            issues.add(ValidationIssue.error(step.id(), "Monitor metric steps must use phase=monitor."));
        }
        if (step.phase() == PlanPhase.MONITOR && definition.effect() != Effect.READ) {
            issues.add(ValidationIssue.error(step.id(), "Monitor phase cannot contain mutation or activation actions."));
        }
        if (step.action() == Data360Action.MONITOR_METRIC) {
            validateThreshold(step, issues);
        }
    }

    private void validateThreshold(PlanStep step, ArrayList<ValidationIssue> issues) {
        var threshold = step.input().get("threshold");
        if (!(threshold instanceof Map<?, ?> map)) {
            issues.add(ValidationIssue.error(step.id(), "Monitor threshold must be an object with operator and numeric value."));
            return;
        }
        var operator = String.valueOf(map.get("operator"));
        if (!Set.of("<", "<=", ">", ">=", "==").contains(operator)) {
            issues.add(ValidationIssue.error(step.id(), "Monitor threshold operator must be one of <, <=, >, >=, ==."));
        }
        if (!(map.get("value") instanceof Number)) {
            issues.add(ValidationIssue.error(step.id(), "Monitor threshold value must be numeric."));
        }
    }

    private boolean hasNonBlank(Object value) {
        if (value == null) {
            return false;
        }
        return !(value instanceof String string) || !string.isBlank();
    }
}
