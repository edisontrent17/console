package com.acme.data360agent.plan;

import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class PlanValidator {
    private static final int MAX_STEPS = 20;
    private static final int MAX_STEP_INPUT_CHARS = 8_192;
    private static final Pattern STEP_ID = Pattern.compile("[A-Za-z][A-Za-z0-9_-]{0,63}");
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9][A-Za-z0-9 _().-]{0,79}");
    private static final Pattern SQL_LIMIT = Pattern.compile("\\blimit\\s+\\d+\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_MUTATION = Pattern.compile("\\b(insert\\s+into|update\\s+\\w+\\s+set|delete\\s+from|merge\\s+into|drop\\s+(table|view|schema|database)|alter\\s+(table|view|schema|database)|create\\s+(table|view|schema|database)|truncate\\s+table|call\\s+|grant\\s+|revoke\\s+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_STRING_LITERAL = Pattern.compile("'([^']|'')*'");
    private static final Pattern URL_LIKE = Pattern.compile("(?i)(https?://|www\\.|[A-Za-z0-9.-]+\\.(com|net|org|io|co)(/|\\b))");
    private final OperationRegistry operations;

    public PlanValidator(OperationRegistry operations) {
        this.operations = operations;
    }

    public PlanValidationResult validate(PlanSpec plan) {
        var issues = new ArrayList<ValidationIssue>();
        if (plan == null) {
            return new PlanValidationResult(List.of(ValidationIssue.error(null, "Plan is missing.")));
        }

        if (!PlanSpec.CURRENT_SCHEMA_VERSION.equals(plan.schemaVersion())) {
            issues.add(ValidationIssue.error(null, "Unsupported PlanSpec schemaVersion: " + plan.schemaVersion()));
        }

        if (plan.steps().size() > MAX_STEPS) {
            issues.add(ValidationIssue.error(null, "Plan cannot contain more than " + MAX_STEPS + " steps."));
        }

        if (plan.context() == null) {
            issues.add(ValidationIssue.error(null, "Plan context is required."));
        } else if ("production".equals(plan.context().environment())) {
            issues.add(ValidationIssue.warning(null, "Production environment selected. Every write/publish/activation step must require approval."));
        }
        var isProduction = plan.context() != null && "production".equals(plan.context().environment());

        var seen = new HashSet<String>();
        for (var step : plan.steps()) {
            if (!hasNonBlank(step.id())) {
                issues.add(ValidationIssue.error(null, "Step id is required and cannot be blank."));
            } else if (!STEP_ID.matcher(step.id()).matches()) {
                issues.add(ValidationIssue.error(step.id(), "Step id must start with a letter and contain only letters, numbers, underscores, or hyphens."));
            }
            if (!seen.add(step.id())) {
                issues.add(ValidationIssue.error(step.id(), "Duplicate step id."));
            }
            for (var dependency : step.dependsOn()) {
                if (!hasNonBlank(dependency)) {
                    issues.add(ValidationIssue.error(step.id(), "Dependency id cannot be blank."));
                    continue;
                }
                if (!seen.contains(dependency)) {
                    issues.add(ValidationIssue.error(step.id(), "Dependency must refer to an earlier step: " + dependency));
                }
            }
            for (var binding : step.inputBindings().entrySet()) {
                if (!hasNonBlank(binding.getValue().fromStep())) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding source step cannot be blank."));
                } else if (!seen.contains(binding.getValue().fromStep())) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding must refer to an earlier step: " + binding.getValue().fromStep()));
                }
                if (!hasNonBlank(binding.getValue().path()) || !binding.getValue().path().startsWith("$.")) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding path must start with $."));
                }
            }

            var definition = operations.require(step.action());
            var input = step.input();
            if (estimatedSize(input) > MAX_STEP_INPUT_CHARS) {
                issues.add(ValidationIssue.error(step.id(), "Step input cannot exceed " + MAX_STEP_INPUT_CHARS + " characters."));
            }
            validateRawUrls(step, input, issues);

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
            validateActionInput(step, issues);
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
        if (SQL_MUTATION.matcher(stripSqlStringLiterals(sql)).find()) {
            issues.add(ValidationIssue.error(step.id(), "Query SQL must be read-only and cannot contain mutation or DDL keywords."));
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

    private void validateActionInput(PlanStep step, ArrayList<ValidationIssue> issues) {
        if ((step.action() == Data360Action.CREATE_SEGMENT || step.action() == Data360Action.UPDATE_SEGMENT)
                && step.input().containsKey("name")) {
            validateName(step, "Segment name", step.input().get("name"), issues);
        }
        if (step.action() == Data360Action.CREATE_ACTIVATION) {
            validateName(step, "Activation name", step.input().get("name"), issues);
            validateName(step, "Activation destination", step.input().get("destination"), issues);
        }
    }

    private void validateThreshold(PlanStep step, ArrayList<ValidationIssue> issues) {
        MonitorThreshold.validate(step.input().get("threshold"))
                .forEach(issue -> issues.add(ValidationIssue.error(step.id(), issue)));
    }

    private void validateName(PlanStep step, String label, Object value, ArrayList<ValidationIssue> issues) {
        if (value instanceof InputBinding) {
            return;
        }
        if (!(value instanceof String string) || string.isBlank()) {
            issues.add(ValidationIssue.error(step.id(), label + " must be nonblank text."));
            return;
        }
        if (!SAFE_NAME.matcher(string.trim()).matches()) {
            issues.add(ValidationIssue.error(step.id(), label + " must be 1-80 characters using letters, numbers, spaces, underscores, hyphens, periods, or parentheses."));
        }
    }

    private boolean hasNonBlank(Object value) {
        if (value == null) {
            return false;
        }
        return !(value instanceof String string) || !string.isBlank();
    }

    private void validateRawUrls(PlanStep step, Map<String, Object> input, ArrayList<ValidationIssue> issues) {
        collectRawUrls("", input, issues, step.id());
    }

    private void collectRawUrls(String path, Object value, ArrayList<ValidationIssue> issues, String stepId) {
        if (value instanceof InputBinding) {
            return;
        }
        if (value instanceof String string && URL_LIKE.matcher(string).find() && !allowsRawUrl(path)) {
            issues.add(ValidationIssue.error(stepId, "Raw URL-like input is not allowed at input" + path + ". Use a named Data 360 resource or binding instead."));
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (var entry : map.entrySet()) {
                collectRawUrls(path + "." + entry.getKey(), entry.getValue(), issues, stepId);
            }
        } else if (value instanceof Iterable<?> iterable) {
            var index = 0;
            for (var item : iterable) {
                collectRawUrls(path + "[" + index + "]", item, issues, stepId);
                index++;
            }
        }
    }

    private boolean allowsRawUrl(String path) {
        return false;
    }

    private String stripSqlStringLiterals(String sql) {
        return SQL_STRING_LITERAL.matcher(sql).replaceAll("''");
    }

    private int estimatedSize(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Map<?, ?> map) {
            var size = 0;
            for (var entry : map.entrySet()) {
                size += estimatedSize(entry.getKey()) + estimatedSize(entry.getValue());
            }
            return size;
        }
        if (value instanceof Iterable<?> iterable) {
            var size = 0;
            for (var item : iterable) {
                size += estimatedSize(item);
            }
            return size;
        }
        return String.valueOf(value).length();
    }
}
