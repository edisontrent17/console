package com.acme.data360agent.plan;

import com.acme.data360agent.mcp.McpToolDescriptor;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private static final Pattern UNRESOLVED_PLACEHOLDER = Pattern.compile("\\$\\{[^}]+}");
    private final OperationRegistry operations;
    private final McpToolRegistryService mcpRegistry;

    public PlanValidator(OperationRegistry operations) {
        this(operations, (McpToolRegistryService) null);
    }

    @Autowired
    public PlanValidator(OperationRegistry operations, ObjectProvider<McpToolRegistryService> mcpRegistry) {
        this(operations, mcpRegistry == null ? null : mcpRegistry.getIfAvailable());
    }

    public PlanValidator(OperationRegistry operations, McpToolRegistryService mcpRegistry) {
        this.operations = operations;
        this.mcpRegistry = mcpRegistry;
    }

    public PlanValidationResult validate(PlanSpec plan) {
        return validate(plan, true);
    }

    public PlanValidationResult validatePure(PlanSpec plan) {
        return validate(plan, false);
    }

    private PlanValidationResult validate(PlanSpec plan, boolean registryBacked) {
        var issues = new ArrayList<ValidationIssue>();
        if (plan == null) {
            return new PlanValidationResult(List.of(ValidationIssue.error(null, "Plan is missing.")));
        }

        if (!PlanSpec.CURRENT_SCHEMA_VERSION.equals(plan.schemaVersion())) {
            issues.add(ValidationIssue.error(null, "Unsupported PlanSpec schemaVersion: " + plan.schemaVersion()));
        }

        validateAslProfile(plan, issues);

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
        var priorSteps = new LinkedHashMap<String, PlanStep>();
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
                } else if (!priorSteps.containsKey(binding.getValue().fromStep())) {
                    issues.add(ValidationIssue.error(step.id(), "Input binding must refer to an earlier step: " + binding.getValue().fromStep()));
                } else {
                    validateInputBindingContract(step, binding.getKey(), binding.getValue(), priorSteps.get(binding.getValue().fromStep()), issues);
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
            validateNoPlaceholders(step, input, issues);

            for (var required : definition.requiredAllOf()) {
                if (!hasParameter(step, required)) {
                    issues.add(ValidationIssue.error(step.id(), "Missing required input: " + required));
                }
            }

            if (!definition.requiredAnyOf().isEmpty()) {
                var hasOne = definition.requiredAnyOf().stream().anyMatch(key -> hasParameter(step, key));
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
            if (step.action() == Data360Action.MCP_EXECUTE) {
                validateMcpExecute(step, registryBacked, issues);
            }
            validateActionInput(step, issues);
            validatePhase(step, definition, issues);
            priorSteps.put(step.id(), step);
        }
        return new PlanValidationResult(List.copyOf(issues));
    }

    private void validateInputBindingContract(PlanStep step, String inputName, InputBinding binding, PlanStep sourceStep, ArrayList<ValidationIssue> issues) {
        if (!hasNonBlank(binding.path()) || !binding.path().startsWith("$.")) {
            return;
        }
        var target = operations.require(step.action());
        if (!target.acceptsInput(inputName)) {
            issues.add(ValidationIssue.error(step.id(), "Input binding target is not declared for " + step.action().value() + ": " + inputName));
        }
        var source = operations.require(sourceStep.action());
        var sourceProducesPath = sourceStep.action() == Data360Action.MCP_EXECUTE
                ? mcpProducesOutputPath(sourceStep, binding.path())
                : source.producesOutputPath(binding.path());
        if (!sourceProducesPath) {
            issues.add(ValidationIssue.error(step.id(), "Input binding references output not produced by " + sourceStep.action().value() + ": " + binding.fromStep() + binding.path().substring(1)));
        }
    }

    private boolean mcpProducesOutputPath(PlanStep sourceStep, String path) {
        if (sourceStep.action() != Data360Action.MCP_EXECUTE || path == null || !path.startsWith("$.")) {
            return false;
        }
        var normalized = path.substring(2);
        if (normalized.startsWith("selected.")) {
            normalized = normalized.substring("selected.".length());
        }
        if (normalized.contains(".")) {
            return false;
        }
        var topLevel = normalized.split("\\.", 2)[0];
        return sourceStep.input().get("outputSelectors") instanceof Map<?, ?> selectors && selectors.containsKey(topLevel);
    }

    private void validateAslProfile(PlanSpec plan, ArrayList<ValidationIssue> issues) {
        var definition = plan.definition();
        if (definition == null) {
            issues.add(ValidationIssue.error(null, "PlanSpec must include an ASL definition."));
            return;
        }
        if (!AslStateMachine.VERSION.equals(definition.version())) {
            issues.add(ValidationIssue.error(null, "ASL definition.Version must be " + AslStateMachine.VERSION + "."));
        }
        if (!AslStateMachine.QUERY_LANGUAGE.equals(definition.queryLanguage())) {
            issues.add(ValidationIssue.error(null, "Data 360 ASL profile supports only QueryLanguage=JSONPath."));
        }
        if (!hasNonBlank(definition.startAt())) {
            issues.add(ValidationIssue.error(null, "ASL definition.StartAt is required."));
        }
        if (definition.states().isEmpty()) {
            issues.add(ValidationIssue.error(null, "ASL definition.States must contain at least one state."));
            return;
        }
        if (definition.states().size() > MAX_STEPS) {
            issues.add(ValidationIssue.error(null, "ASL definition cannot contain more than " + MAX_STEPS + " states."));
        }
        if (hasNonBlank(definition.startAt()) && !definition.states().containsKey(definition.startAt())) {
            issues.add(ValidationIssue.error(null, "ASL definition.StartAt must reference an existing state."));
        }

        for (var entry : definition.states().entrySet()) {
            validateAslState(entry.getKey(), entry.getValue(), definition.states(), issues);
        }
        validateAslTraversal(definition, issues);
        validateAslStepProjection(plan, issues);
    }

    private void validateAslState(String stateName, AslState state, Map<String, AslState> states, ArrayList<ValidationIssue> issues) {
        if (!hasNonBlank(stateName) || !STEP_ID.matcher(stateName).matches()) {
            issues.add(ValidationIssue.error(stateName, "ASL state names must start with a letter and contain only letters, numbers, underscores, or hyphens."));
        }
        if (state == null) {
            issues.add(ValidationIssue.error(stateName, "ASL state is missing."));
            return;
        }
        if (!"Task".equals(state.type())) {
            issues.add(ValidationIssue.error(stateName, "Data 360 ASL profile currently supports only Task states."));
        }
        if (!hasNonBlank(state.resource())) {
            issues.add(ValidationIssue.error(stateName, "Task Resource is required."));
        } else if (state.resource().startsWith("mcp://")) {
            issues.add(ValidationIssue.error(stateName, "Task Resource must use a governed Data 360 capability URI, not raw mcp:// resources."));
        } else {
            try {
                Data360Action.fromResource(state.resource());
            } catch (IllegalArgumentException e) {
                issues.add(ValidationIssue.error(stateName, "Task Resource must be a Data 360 capability URI, not a raw tool, URL, or AWS ARN."));
            }
        }
        if (hasNonBlank(state.inputPath())) {
            issues.add(ValidationIssue.error(stateName, "InputPath is reserved for a later profile. Use Parameters with explicit fields."));
        }
        if (hasNonBlank(state.outputPath())) {
            issues.add(ValidationIssue.error(stateName, "OutputPath is reserved for a later profile. Use ResultPath with $.stateName."));
        }
        if (!state.retry().isEmpty() || !state.catchers().isEmpty()) {
            issues.add(ValidationIssue.error(stateName, "Retry and Catch are reserved for a later profile; Temporal owns retries in this version."));
        }
        if (state.timeoutSeconds() != null && (state.timeoutSeconds() < 1 || state.timeoutSeconds() > 300)) {
            issues.add(ValidationIssue.error(stateName, "TimeoutSeconds must be between 1 and 300."));
        }
        if (state.heartbeatSeconds() != null) {
            issues.add(ValidationIssue.error(stateName, "HeartbeatSeconds is reserved for a later profile."));
        }
        if (!hasNonBlank(state.resultPath())) {
            issues.add(ValidationIssue.error(stateName, "Task ResultPath is required and must be $.stateName."));
        } else if (!state.resultPath().equals("$." + stateName)) {
            issues.add(ValidationIssue.error(stateName, "Task ResultPath must be $." + stateName + "."));
        }

        var hasNext = hasNonBlank(state.next());
        var ends = Boolean.TRUE.equals(state.end());
        if (hasNext == ends) {
            issues.add(ValidationIssue.error(stateName, "Task state must set exactly one of Next or End=true."));
        }
        if (hasNext && !states.containsKey(state.next())) {
            issues.add(ValidationIssue.error(stateName, "Task Next must reference an existing state: " + state.next()));
        }
        validateAslParameters(stateName, state.parameters(), issues);
    }

    private void validateAslParameters(String stateName, Map<String, Object> parameters, ArrayList<ValidationIssue> issues) {
        for (var entry : parameters.entrySet()) {
            var key = entry.getKey();
            if (!hasNonBlank(key)) {
                issues.add(ValidationIssue.error(stateName, "Parameters keys cannot be blank."));
                continue;
            }
            if (key.endsWith(".$")) {
                var path = String.valueOf(entry.getValue());
                if (!path.matches("^\\$\\.[A-Za-z][A-Za-z0-9_-]{0,63}\\.[A-Za-z][A-Za-z0-9_.-]*$")) {
                    issues.add(ValidationIssue.error(stateName, "Dynamic Parameters must use simple $.state.field paths."));
                }
            }
        }
    }

    private void validateAslTraversal(AslStateMachine definition, ArrayList<ValidationIssue> issues) {
        if (definition == null || !hasNonBlank(definition.startAt()) || !definition.states().containsKey(definition.startAt())) {
            return;
        }
        var visited = new LinkedHashSet<String>();
        var current = definition.startAt();
        while (current != null && !current.isBlank()) {
            if (!visited.add(current)) {
                issues.add(ValidationIssue.error(current, "ASL definition contains a cycle."));
                return;
            }
            var state = definition.states().get(current);
            if (state == null || !"Task".equals(state.type())) {
                return;
            }
            current = Boolean.TRUE.equals(state.end()) ? null : state.next();
        }
        var unreachable = new LinkedHashSet<>(definition.states().keySet());
        unreachable.removeAll(visited);
        if (!unreachable.isEmpty()) {
            issues.add(ValidationIssue.error(null, "ASL definition contains unreachable states: " + String.join(", ", unreachable)));
        }
    }

    private void validateAslStepProjection(PlanSpec plan, ArrayList<ValidationIssue> issues) {
        if (plan.steps().isEmpty()) {
            return;
        }
        List<PlanStep> derivedSteps;
        try {
            derivedSteps = AslPlanCompiler.toSteps(plan.definition());
        } catch (IllegalArgumentException e) {
            return;
        }
        if (derivedSteps.size() != plan.steps().size()) {
            issues.add(ValidationIssue.error(null, "Submitted steps must match the ASL definition-derived steps."));
            return;
        }
        for (var index = 0; index < derivedSteps.size(); index++) {
            var expected = derivedSteps.get(index);
            var actual = plan.steps().get(index);
            if (!expected.id().equals(actual.id())
                    || expected.action() != actual.action()
                    || !expected.input().equals(actual.input())
                    || !expected.inputBindings().equals(actual.inputBindings())) {
                issues.add(ValidationIssue.error(actual.id(), "Submitted steps must match the ASL definition-derived steps."));
                return;
            }
        }
    }

    private void validateQuery(PlanStep step, ArrayList<ValidationIssue> issues) {
        var sql = String.valueOf(step.input().getOrDefault("sql", ""));
        if (sql.isBlank()) {
            return;
        }
        validateSql(step, sql, true, !step.input().containsKey("limit"), issues);
    }

    private void validatePhase(PlanStep step, com.acme.data360agent.operation.OperationDefinition definition, ArrayList<ValidationIssue> issues) {
        if (step.phase() == PlanPhase.MONITOR && step.action() != Data360Action.MONITOR_METRIC && step.action() != Data360Action.MCP_EXECUTE) {
            issues.add(ValidationIssue.error(step.id(), "Monitor phase currently allows only data360.monitor.metric or read-only data360.mcp.execute."));
        }
        if (step.action() == Data360Action.MONITOR_METRIC && step.phase() != PlanPhase.MONITOR) {
            issues.add(ValidationIssue.error(step.id(), "Monitor metric steps must use phase=monitor."));
        }
        try {
            if (step.phase() == PlanPhase.MONITOR && effectiveEffect(step, definition.effect()) != Effect.READ) {
                issues.add(ValidationIssue.error(step.id(), "Monitor phase cannot contain mutation or activation actions."));
            }
        } catch (IllegalArgumentException ignored) {
            // validateMcpExecute reports unsupported effect values.
        }
        if (step.action() == Data360Action.MONITOR_METRIC) {
            validateThreshold(step, issues);
        }
    }

    private void validateMcpExecute(PlanStep step, boolean registryBacked, ArrayList<ValidationIssue> issues) {
        requireText(step, "serverId", issues);
        requireText(step, "toolName", issues);
        if (!step.input().containsKey("effect")) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute steps must declare effect: read, write, publish, activate, or destructive."));
        } else {
            try {
                effectiveEffect(step, Effect.READ);
            } catch (IllegalArgumentException e) {
                issues.add(ValidationIssue.error(step.id(), e.getMessage()));
            }
        }
        if (!(step.input().get("params") instanceof Map<?, ?>)) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute params must be an object."));
        }
        if (step.input().containsKey("facadeTool") && !"execute".equals(text(step.input().get("facadeTool")))) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute facadeTool must be execute."));
        }
        if (step.input().containsKey("outputSelectors") && !(step.input().get("outputSelectors") instanceof Map<?, ?>)) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute outputSelectors must be an object."));
        }
        if (!(step.input().get("outputSelectors") instanceof Map<?, ?> selectors) || selectors.isEmpty()) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute steps must declare outputSelectors so execution persists only bounded selected output."));
        }
        if (step.input().get("outputSelectors") instanceof Map<?, ?> selectors) {
            for (var entry : selectors.entrySet()) {
                var key = String.valueOf(entry.getKey());
                var path = String.valueOf(entry.getValue());
                if (!STEP_ID.matcher(key).matches()) {
                    issues.add(ValidationIssue.error(step.id(), "Output selector names must start with a letter and contain only letters, numbers, underscores, or hyphens: " + key));
                }
                if (!path.startsWith("$.")) {
                    issues.add(ValidationIssue.error(step.id(), "Output selector path must start with $.: " + key));
                } else if ("$.raw".equals(path) || path.startsWith("$.raw.") || "$.text".equals(path) || path.startsWith("$.text.") || "$.output".equals(path)) {
                    issues.add(ValidationIssue.error(step.id(), "Output selector path is too broad or unsafe: " + key));
                }
            }
        }
        try {
            if (effectiveEffect(step, Effect.READ) != Effect.READ && !step.needsApproval()) {
                issues.add(ValidationIssue.error(step.id(), "MCP execute mutation steps require needsApproval=true."));
            }
        } catch (IllegalArgumentException ignored) {
            // Reported above.
        }
        validateMcpSql(step, issues);
        if (registryBacked) {
            validateMcpRegistry(step, issues);
        }
    }

    private void validateMcpRegistry(PlanStep step, ArrayList<ValidationIssue> issues) {
        if (mcpRegistry == null) {
            return;
        }
        var serverId = text(step.input().get("serverId"));
        var toolName = text(step.input().get("toolName"));
        if (serverId.isBlank() || toolName.isBlank()) {
            return;
        }
        try {
            var snapshot = mcpRegistry.current();
            var server = snapshot.serverStatus(serverId);
            if (server.isEmpty()) {
                issues.add(ValidationIssue.error(step.id(), "MCP server is not enabled or was not discovered: " + serverId));
                return;
            }
            if (!"passed".equals(server.get().status())) {
                issues.add(ValidationIssue.error(step.id(), "MCP server is not available for validation: " + serverId + " (" + server.get().status() + ": " + server.get().message() + ")"));
                return;
            }
            if (!server.get().executionServer()) {
                issues.add(ValidationIssue.error(step.id(), "MCP server is discovery-only and cannot execute PlanSpec tasks: " + serverId));
                return;
            }
            var tool = snapshot.findTool(serverId, toolName);
            if (tool.isEmpty()) {
                issues.add(ValidationIssue.error(step.id(), "MCP tool is not available on server " + serverId + ": " + toolName));
                return;
            }
            validateMcpEffect(step, tool.get(), issues);
            validateMcpParams(step, tool.get(), issues);
            validateMcpSelectorContracts(step, tool.get(), issues);
        } catch (RuntimeException e) {
            issues.add(ValidationIssue.error(step.id(), "MCP registry validation failed: " + message(e)));
        }
    }

    private void validateMcpEffect(PlanStep step, McpToolDescriptor tool, ArrayList<ValidationIssue> issues) {
        Effect declared;
        try {
            declared = effectiveEffect(step, Effect.READ);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        var discovered = effectFromRegistry(tool.effect());
        if (effectRisk(discovered) > effectRisk(declared)) {
            issues.add(ValidationIssue.error(
                    step.id(),
                    "MCP execute effect understates registry effect for " + tool.name() + ": declared " + declared.name().toLowerCase(Locale.ROOT) + ", registry " + discovered.name().toLowerCase(Locale.ROOT)
            ));
        } else if (effectRisk(declared) > effectRisk(discovered)) {
            issues.add(ValidationIssue.warning(
                    step.id(),
                    "MCP execute effect is stricter than registry effect for " + tool.name() + ": declared " + declared.name().toLowerCase(Locale.ROOT) + ", registry " + discovered.name().toLowerCase(Locale.ROOT)
            ));
        }
    }

    private void validateMcpParams(PlanStep step, McpToolDescriptor tool, ArrayList<ValidationIssue> issues) {
        if (!(step.input().get("params") instanceof Map<?, ?> params)) {
            return;
        }
        var schema = tool.inputSchema();
        if (schema.get("required") instanceof Iterable<?> required) {
            for (var field : required) {
                var name = String.valueOf(field);
                if (!params.containsKey(name)) {
                    issues.add(ValidationIssue.error(step.id(), "MCP execute params missing required tool field for " + tool.name() + ": " + name));
                }
            }
        }
        if (!(schema.get("properties") instanceof Map<?, ?> properties)) {
            return;
        }
        for (var entry : params.entrySet()) {
            var definition = properties.get(entry.getKey());
            if (definition instanceof Map<?, ?> property && property.get("type") instanceof String type && !matchesJsonType(entry.getValue(), type)) {
                issues.add(ValidationIssue.error(step.id(), "MCP execute param " + entry.getKey() + " must be " + type + " for " + tool.name() + "."));
            }
        }
    }

    private void validateMcpSelectorContracts(PlanStep step, McpToolDescriptor tool, ArrayList<ValidationIssue> issues) {
        if (!(step.input().get("outputSelectors") instanceof Map<?, ?> selectors) || selectors.isEmpty() || tool.selectorContracts().isEmpty()) {
            return;
        }
        for (var entry : selectors.entrySet()) {
            var name = String.valueOf(entry.getKey());
            var path = String.valueOf(entry.getValue());
            var expected = tool.selectorContracts().get(name);
            if (expected == null) {
                issues.add(ValidationIssue.error(step.id(), "Output selector is not declared by the MCP tool contract for " + tool.name() + ": " + name));
            } else if (!expected.equals(path)) {
                issues.add(ValidationIssue.error(step.id(), "Output selector path must match the MCP tool contract for " + tool.name() + ": " + name));
            }
        }
    }

    private void validateMcpSql(PlanStep step, ArrayList<ValidationIssue> issues) {
        var toolName = text(step.input().get("toolName")).toLowerCase(Locale.ROOT);
        if (!toolName.contains("sql") && !toolName.contains("query")) {
            return;
        }
        if (!(step.input().get("params") instanceof Map<?, ?> params)) {
            return;
        }
        for (var entry : params.entrySet()) {
            var key = String.valueOf(entry.getKey()).toLowerCase(Locale.ROOT);
            if (!(entry.getValue() instanceof String sql) || (!key.contains("sql") && !key.contains("query"))) {
                continue;
            }
            validateSql(step, sql, true, true, issues);
        }
    }

    private void validateSql(PlanStep step, String sql, boolean requireSelect, boolean requireLimit, ArrayList<ValidationIssue> issues) {
        if (requireSelect && !sql.trim().toLowerCase(Locale.ROOT).startsWith("select")) {
            issues.add(ValidationIssue.error(step.id(), "Only SELECT queries are allowed in the MVP."));
        }
        if (SQL_MUTATION.matcher(stripSqlStringLiterals(sql)).find()) {
            issues.add(ValidationIssue.error(step.id(), "Query SQL must be read-only and cannot contain mutation or DDL keywords."));
        }
        if (requireLimit && !SQL_LIMIT.matcher(sql).find()) {
            issues.add(ValidationIssue.error(step.id(), "Query steps must include a LIMIT or a limit input."));
        }
        if (sql.contains(";")) {
            issues.add(ValidationIssue.error(step.id(), "SQL must contain a single statement without semicolons."));
        }
    }

    private Effect effectFromRegistry(String value) {
        var normalized = value == null ? "read" : value.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "destructive" -> Effect.DESTRUCTIVE;
            case "write" -> Effect.WRITE;
            case "publish" -> Effect.PUBLISH;
            case "activate" -> Effect.ACTIVATE;
            default -> Effect.READ;
        };
    }

    private int effectRisk(Effect effect) {
        return switch (effect) {
            case READ -> 0;
            case WRITE -> 1;
            case PUBLISH, ACTIVATE -> 2;
            case DESTRUCTIVE -> 3;
        };
    }

    private boolean matchesJsonType(Object value, String type) {
        if (value == null) {
            return true;
        }
        return switch (type) {
            case "string" -> value instanceof String;
            case "number" -> value instanceof Number;
            case "integer" -> value instanceof Integer || value instanceof Long;
            case "boolean" -> value instanceof Boolean;
            case "object" -> value instanceof Map<?, ?>;
            case "array" -> value instanceof List<?>;
            default -> true;
        };
    }

    private void requireText(PlanStep step, String key, ArrayList<ValidationIssue> issues) {
        if (!(step.input().get(key) instanceof String text) || text.isBlank()) {
            issues.add(ValidationIssue.error(step.id(), "MCP execute " + key + " must be nonblank text."));
        }
    }

    private Effect effectiveEffect(PlanStep step, Effect fallback) {
        if (step.action() != Data360Action.MCP_EXECUTE) {
            return fallback;
        }
        var raw = String.valueOf(step.input().getOrDefault("effect", "read")).trim().toUpperCase(Locale.ROOT);
        return switch (raw) {
            case "READ" -> Effect.READ;
            case "WRITE" -> Effect.WRITE;
            case "PUBLISH" -> Effect.PUBLISH;
            case "ACTIVATE" -> Effect.ACTIVATE;
            case "DESTRUCTIVE" -> Effect.DESTRUCTIVE;
            default -> throw new IllegalArgumentException("Unsupported MCP execute effect: " + raw.toLowerCase(Locale.ROOT));
        };
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

    private String text(Object value) {
        return value instanceof String string ? string.trim() : "";
    }

    private boolean hasParameter(PlanStep step, String key) {
        return hasNonBlank(step.input().get(key)) || step.inputBindings().containsKey(key);
    }

    private void validateRawUrls(PlanStep step, Map<String, Object> input, ArrayList<ValidationIssue> issues) {
        collectRawUrls("", input, issues, step.id());
    }

    private void validateNoPlaceholders(PlanStep step, Map<String, Object> input, ArrayList<ValidationIssue> issues) {
        collectPlaceholders("", input, issues, step.id());
    }

    private void collectPlaceholders(String path, Object value, ArrayList<ValidationIssue> issues, String stepId) {
        if (value instanceof String string && UNRESOLVED_PLACEHOLDER.matcher(string).find()) {
            issues.add(ValidationIssue.error(stepId, "Unresolved placeholder is not allowed at input" + path + ": " + string));
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (var entry : map.entrySet()) {
                collectPlaceholders(path + "." + entry.getKey(), entry.getValue(), issues, stepId);
            }
        } else if (value instanceof Iterable<?> iterable) {
            var index = 0;
            for (var item : iterable) {
                collectPlaceholders(path + "[" + index + "]", item, issues, stepId);
                index++;
            }
        }
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

    private String message(RuntimeException e) {
        var message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
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
