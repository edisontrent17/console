package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class Data360DiagnosticsService {
    private final AppProperties properties;
    private final Data360Client client;
    private final OperationRegistry operations;
    private final McpSettingsService mcpSettings;

    public Data360DiagnosticsService(AppProperties properties, Data360Client client, OperationRegistry operations, McpSettingsService mcpSettings) {
        this.properties = properties;
        this.client = client;
        this.operations = operations;
        this.mcpSettings = mcpSettings;
    }

    public Data360DiagnosticsResult diagnostics() {
        var mode = mode();
        return new Data360DiagnosticsResult(mode, configured(mode), status(mode), details(mode), Instant.now());
    }

    public Data360DiagnosticsResult smokeMetadata() {
        var mode = mode();
        var details = new LinkedHashMap<String, Object>(details(mode));
        if (!configured(mode)) {
            return new Data360DiagnosticsResult(mode, false, "not_configured", details, Instant.now());
        }
        try {
            var operation = operations.require(Data360Action.METADATA_DESCRIBE);
            var step = new PlanStep(
                    "diagnose_metadata",
                    "Diagnose Data 360 metadata access",
                    PlanPhase.DISCOVER,
                    Data360Action.METADATA_DESCRIBE,
                    Map.of("objects", List.of("UnifiedIndividual", "UnifiedAccount")),
                    List.of(),
                    Map.of(),
                    false
            );
            var result = client.call(operation, step, step.input(), new RunContext("diagnostics", "diagnostics", new PlanContext("diagnostics", "default", "sandbox")));
            details.put("smoke", "metadata");
            details.put("outputKeys", result.output().keySet().stream().sorted().toList());
            details.put("rawKeys", result.raw().keySet().stream().sorted().toList());
            return new Data360DiagnosticsResult(mode, true, "ok", details, Instant.now());
        } catch (ConnectApiException e) {
            details.put("statusCode", e.statusCode());
            details.put("error", "Data 360 Connect API call failed.");
            return new Data360DiagnosticsResult(mode, true, "failed", details, Instant.now());
        } catch (Exception e) {
            details.put("error", e.getMessage());
            return new Data360DiagnosticsResult(mode, true, "failed", details, Instant.now());
        }
    }

    private String mode() {
        var data360 = properties.data360();
        if (data360 == null || data360.client() == null || data360.client().isBlank()) {
            return "mock";
        }
        return data360.client();
    }

    private boolean configured(String mode) {
        return switch (mode) {
            case "mock" -> true;
            case "mcp" -> hasText(mcpCommand());
            case "connect" -> connectConfigured();
            default -> false;
        };
    }

    private String status(String mode) {
        if (!configured(mode)) {
            return "not_configured";
        }
        return "ready";
    }

    private Map<String, Object> details(String mode) {
        var details = new LinkedHashMap<String, Object>();
        details.put("client", mode);
        switch (mode) {
            case "mock" -> details.put("note", "Mock Data 360 client is active; no external calls will be made.");
            case "mcp" -> {
                details.put("mcpCommandConfigured", hasText(mcpCommand()));
                details.put("mcpCommandPreview", preview(mcpCommand()));
            }
            case "connect" -> connectDetails(details);
            default -> details.put("note", "Unknown Data 360 client mode.");
        }
        return details;
    }

    private void connectDetails(Map<String, Object> details) {
        var connect = connect();
        details.put("apiVersion", connect.resolvedApiVersion());
        details.put("workloadName", connect.resolvedWorkloadName());
        details.put("directData360TokenConfigured", hasText(connect.instanceUrl()) && hasText(connect.accessToken()));
        details.put("salesforceTokenExchangeConfigured", hasText(connect.salesforceInstanceUrl()) && hasText(connect.salesforceAccessToken()));
        details.put("jwtBearerConfigured", hasText(connect.clientId()) && hasText(connect.username()) && (hasText(connect.privateKey()) || hasText(connect.privateKeyPath())));
        details.put("instanceUrlConfigured", hasText(connect.instanceUrl()));
        details.put("salesforceInstanceUrlConfigured", hasText(connect.salesforceInstanceUrl()));
        details.put("privateKeyPathConfigured", hasText(connect.privateKeyPath()));
        details.put("timeoutSeconds", connect.resolvedTimeoutSeconds());
    }

    private boolean connectConfigured() {
        var connect = connect();
        var direct = hasText(connect.instanceUrl()) && hasText(connect.accessToken());
        var tokenExchange = hasText(connect.salesforceInstanceUrl()) && hasText(connect.salesforceAccessToken());
        var jwt = hasText(connect.clientId()) && hasText(connect.username()) && (hasText(connect.privateKey()) || hasText(connect.privateKeyPath()));
        return direct || tokenExchange || jwt;
    }

    private AppProperties.Connect connect() {
        var data360 = properties.data360();
        if (data360 == null || data360.connect() == null) {
            return new AppProperties.Connect(null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return data360.connect();
    }

    private String mcpCommand() {
        return mcpSettings.commandFor("data360").orElse("");
    }

    private String preview(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.length() <= 80 ? value : value.substring(0, 77) + "...";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
