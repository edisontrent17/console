package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.data360.client", havingValue = "connect")
public class HttpData360ConnectClient implements Data360Client {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder builder;
    private final ConnectTokenProvider tokenProvider;
    private final ConnectApiIdempotencyStore idempotency;

    public HttpData360ConnectClient(AppProperties properties, ObjectMapper objectMapper, WebClient.Builder builder, ConnectTokenProvider tokenProvider, ConnectApiIdempotencyStore idempotency) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.builder = builder;
        this.tokenProvider = tokenProvider;
        this.idempotency = idempotency;
    }

    @Override
    public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var request = compile(operation, step, resolvedInput, context);
        if (operation.effect() != Effect.READ) {
            var key = idempotencyKey(context, step, resolvedInput);
            var existing = idempotency.completed(key);
            if (existing.isPresent()) {
                return existing.get();
            }
            return idempotency.remember(key, execute(request, operation, step));
        }
        return execute(request, operation, step);
    }

    private Data360CallResult execute(ConnectRequest request, OperationDefinition operation, PlanStep step) {
        var session = tokenProvider.session();
        var raw = send(session, request);
        var output = normalize(step.action(), raw);
        var audit = Map.<String, Object>of(
                "mode", "connect",
                "method", request.method().name(),
                "path", request.path(),
                "calledAt", Instant.now().toString(),
                "effect", operation.effect().name(),
                "raw", raw
        );
        return new Data360CallResult(output, audit);
    }

    private ConnectRequest compile(OperationDefinition operation, PlanStep step, Map<String, Object> input, RunContext context) {
        return switch (step.action()) {
            case SEARCH, METADATA_DESCRIBE -> new ConnectRequest(HttpMethod.GET, "/api/v1/metadata/", Map.of(), null);
            case QUERY -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/query-sql"), queryParams(context, "workloadName", workloadName(input)), Map.of(
                    "sql", input.get("sql"),
                    "sqlParameters", input.getOrDefault("sqlParameters", List.of())
            ));
            case CREATE_CALCULATED_INSIGHT -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/calculated-insights"), dataspaceParams(context), calculatedInsightBody(input));
            case RUN_CALCULATED_INSIGHT -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/calculated-insights/" + encode(required(input, "insightId")) + "/actions/run"), Map.of(), null);
            case CREATE_SEGMENT -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/segments"), dataspaceParams(context), segmentBody(input, context));
            case UPDATE_SEGMENT -> new ConnectRequest(HttpMethod.PATCH, servicesPath("/ssot/segments/" + encode(required(input, "segmentId"))), dataspaceParams(context), segmentBody(input, context));
            case PUBLISH_SEGMENT -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/segments/" + encode(required(input, "segmentId")) + "/actions/publish"), Map.of(), null);
            case CREATE_ACTIVATION -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/activations"), Map.of(), activationBody(input, context));
            case RUN_ACTIVATION -> new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/activations/" + encode(required(input, "activationId")) + "/actions/publish"), Map.of(), Map.of("fullRefresh", input.getOrDefault("fullRefresh", false)));
            case GET_IDENTITY_RULESET -> identityResolutionRequest(input);
            case MONITOR_METRIC -> monitorMetricRequest(input, context);
        };
    }

    private Map<String, Object> send(ConnectAuthSession session, ConnectRequest request) {
        var client = builder.baseUrl(session.instanceUrl()).build();
        var uri = request.uri();
        try {
            WebClient.RequestBodySpec bodySpec = client.method(request.method())
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON);
            bodySpec.headers(headers -> headers.setBearerAuth(session.accessToken()));
            WebClient.RequestHeadersSpec<?> headersSpec = request.body() == null
                    ? bodySpec
                    : bodySpec.contentType(MediaType.APPLICATION_JSON).bodyValue(request.body());
            var raw = headersSpec.exchangeToMono(response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("{}")
                            .map(body -> {
                                if (response.statusCode().isError()) {
                                    throw new ConnectApiException("Data 360 Connect API call failed: " + request.method() + " " + request.path(), response.statusCode().value(), redact(body));
                                }
                                return body == null || body.isBlank() ? "{}" : body;
                            }))
                    .block(Duration.ofSeconds(connect().resolvedTimeoutSeconds()));
            return objectMapper.readValue(raw, MAP);
        } catch (ConnectApiException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Data 360 Connect API call failed: " + request.method() + " " + uri, e);
        }
    }

    private ConnectRequest identityResolutionRequest(Map<String, Object> input) {
        var identityResolution = firstText(input, "rulesetId", "rulesetName", "identityResolution");
        if (identityResolution == null) {
            return new ConnectRequest(HttpMethod.GET, servicesPath("/ssot/identity-resolutions"), Map.of(), null);
        }
        return new ConnectRequest(HttpMethod.GET, servicesPath("/ssot/identity-resolutions/" + encode(identityResolution)), Map.of(), null);
    }

    @SuppressWarnings("unchecked")
    private ConnectRequest monitorMetricRequest(Map<String, Object> input, RunContext context) {
        var sql = firstText(input, "query", "sql");
        if (sql == null && input.get("queryContext") instanceof Map<?, ?> contextMap && contextMap.get("sql") != null) {
            sql = String.valueOf(contextMap.get("sql"));
        }
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("Connect monitor metric requires query, sql, or queryContext.sql.");
        }
        return new ConnectRequest(HttpMethod.POST, servicesPath("/ssot/query-sql"), queryParams(context, "workloadName", workloadName(input)), Map.of(
                "sql", sql,
                "sqlParameters", input.getOrDefault("sqlParameters", List.of())
        ));
    }

    private Map<String, Object> calculatedInsightBody(Map<String, Object> input) {
        var name = required(input, "name");
        var apiName = String.valueOf(input.getOrDefault("apiName", developerName(name) + "__cio"));
        var body = new LinkedHashMap<String, Object>();
        body.put("displayName", name);
        body.put("apiName", apiName);
        body.put("description", input.getOrDefault("description", ""));
        body.put("definitionType", input.getOrDefault("definitionType", "CALCULATED_METRIC"));
        body.put("expression", input.getOrDefault("sql", input.get("definition")));
        return dropNulls(body);
    }

    private Map<String, Object> segmentBody(Map<String, Object> input, RunContext context) {
        var name = required(input, "name");
        var criteria = input.get("criteria") instanceof Map<?, ?> map ? map : Map.of();
        var sql = input.getOrDefault("sql", criteria.getOrDefault("sql", null));
        var body = new LinkedHashMap<String, Object>();
        body.put("name", name);
        body.put("developerName", input.getOrDefault("developerName", developerName(name)));
        body.put("description", input.getOrDefault("description", ""));
        body.put("dataSpaceName", context.planContext().dataspace());
        body.put("segmentOnApiName", input.getOrDefault("segmentOnApiName", input.getOrDefault("baseEntity", "UnifiedIndividual")));
        body.put("sql", sql);
        body.put("criteria", input.getOrDefault("criteria", Map.of()));
        return dropNulls(body);
    }

    private Map<String, Object> activationBody(Map<String, Object> input, RunContext context) {
        var name = required(input, "name");
        var body = new LinkedHashMap<String, Object>();
        body.put("name", name);
        body.put("developerName", input.getOrDefault("developerName", developerName(name)));
        body.put("description", input.getOrDefault("description", ""));
        body.put("dataSpaceName", context.planContext().dataspace());
        body.put("activationTargetName", input.getOrDefault("activationTargetName", input.get("destination")));
        body.put("marketSegmentId", input.getOrDefault("segmentId", input.get("marketSegmentId")));
        body.put("segmentId", input.getOrDefault("segmentId", input.get("marketSegmentId")));
        body.put("segmentApiName", input.get("segmentApiName"));
        body.put("refreshType", input.getOrDefault("refreshType", "INCREMENTAL"));
        return dropNulls(body);
    }

    private Map<String, Object> normalize(Data360Action action, Map<String, Object> raw) {
        return switch (action) {
            case QUERY, MONITOR_METRIC -> normalizeQuery(raw);
            case CREATE_SEGMENT, UPDATE_SEGMENT -> withPrimaryId(raw, "segmentId", "id", "segmentId", "marketSegmentId", "developerName");
            case PUBLISH_SEGMENT -> withPrimaryId(raw, "segmentId", "segmentId", "marketSegmentId", "id");
            case CREATE_ACTIVATION -> withPrimaryId(raw, "activationId", "id", "activationId", "developerName");
            case RUN_ACTIVATION -> withPrimaryId(raw, "activationId", "activationId", "id");
            case CREATE_CALCULATED_INSIGHT -> withPrimaryId(raw, "insightId", "apiName", "id", "insightId");
            case RUN_CALCULATED_INSIGHT -> raw;
            case SEARCH, METADATA_DESCRIBE, GET_IDENTITY_RULESET -> raw;
        };
    }

    private Map<String, Object> normalizeQuery(Map<String, Object> raw) {
        var output = new LinkedHashMap<String, Object>(raw);
        if (!output.containsKey("rows") && raw.get("data") instanceof List<?> data) {
            output.put("rows", data);
        }
        if (!output.containsKey("rowCount")) {
            if (raw.get("rowCount") instanceof Number number) {
                output.put("rowCount", number.intValue());
            } else if (output.get("rows") instanceof List<?> rows) {
                output.put("rowCount", rows.size());
            }
        }
        if (!output.containsKey("observedValue") && output.get("rowCount") instanceof Number number) {
            output.put("observedValue", number.doubleValue());
        }
        return Map.copyOf(output);
    }

    private Map<String, Object> withPrimaryId(Map<String, Object> raw, String targetKey, String... sourceKeys) {
        var output = new LinkedHashMap<>(raw);
        for (var key : sourceKeys) {
            if (raw.get(key) != null) {
                output.putIfAbsent(targetKey, raw.get(key));
                break;
            }
        }
        return Map.copyOf(output);
    }

    private Map<String, String> dataspaceParams(RunContext context) {
        return Map.of("dataspace", context.planContext().dataspace());
    }

    private Map<String, String> queryParams(RunContext context, String key, String value) {
        var params = new LinkedHashMap<String, String>();
        params.put("dataspace", context.planContext().dataspace());
        params.put(key, value);
        return Map.copyOf(params);
    }

    private String workloadName(Map<String, Object> input) {
        return String.valueOf(input.getOrDefault("workloadName", connect().resolvedWorkloadName()));
    }

    private String servicesPath(String suffix) {
        return "/services/data/" + connect().resolvedApiVersion() + suffix;
    }

    private AppProperties.Connect connect() {
        var data360 = properties.data360();
        if (data360 == null || data360.connect() == null) {
            return new AppProperties.Connect(null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return data360.connect();
    }

    private String required(Map<String, Object> input, String key) {
        var value = input.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException("Missing required Connect API input: " + key);
        }
        return String.valueOf(value);
    }

    private String firstText(Map<String, Object> input, String... keys) {
        for (var key : keys) {
            var value = input.get(key);
            if (value != null && !String.valueOf(value).isBlank()) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private String developerName(String name) {
        var normalized = name == null ? "generated" : name.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (normalized.isBlank()) {
            normalized = "generated";
        }
        return normalized + "_" + UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)).toString().substring(0, 8).replace("-", "");
    }

    private String idempotencyKey(RunContext context, PlanStep step, Map<String, Object> input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            digest.update(context.planId().getBytes(StandardCharsets.UTF_8));
            digest.update(context.runId().getBytes(StandardCharsets.UTF_8));
            digest.update(step.id().getBytes(StandardCharsets.UTF_8));
            digest.update(objectMapper.writeValueAsBytes(input));
            return bytesToHex(digest.digest());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute Connect API idempotency key.", e);
        }
    }

    private Map<String, Object> dropNulls(LinkedHashMap<String, Object> input) {
        input.entrySet().removeIf(entry -> entry.getValue() == null);
        return Map.copyOf(input);
    }

    private String bytesToHex(byte[] bytes) {
        var builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String redact(String body) {
        if (body == null) {
            return "";
        }
        return body.replaceAll("(?i)(access_token|refresh_token|secret|private_key)\"\\s*:\\s*\"[^\"]+\"", "$1\":\"***\"");
    }

    private record ConnectRequest(HttpMethod method, String path, Map<String, String> queryParams, Map<String, Object> body) {
        String uri() {
            if (queryParams == null || queryParams.isEmpty()) {
                return path;
            }
            var query = queryParams.entrySet().stream()
                    .map(entry -> encodeComponent(entry.getKey()) + "=" + encodeComponent(entry.getValue()))
                    .reduce((left, right) -> left + "&" + right)
                    .orElse("");
            return path + "?" + query;
        }

        private String encodeComponent(String value) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        }
    }
}
