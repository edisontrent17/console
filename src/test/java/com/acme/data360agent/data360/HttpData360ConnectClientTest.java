package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class HttpData360ConnectClientTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void postsQuerySqlToConnectApi() throws Exception {
        var seenPath = new AtomicReference<String>();
        var seenBody = new AtomicReference<String>();
        server = startServer(exchange -> {
            seenPath.set(exchange.getRequestURI().toString());
            seenBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            respond(exchange, 200, "{\"queryId\":\"q1\",\"data\":[{\"id\":\"u1\"}],\"done\":true}");
        });
        var client = client(server);
        var operation = new OperationRegistry().require(Data360Action.QUERY);
        var step = new PlanStep("preview", "Preview", Data360Action.QUERY, Map.of("sql", "SELECT Id FROM UnifiedIndividual LIMIT 10"), List.of(), false);

        var result = client.call(operation, step, step.input(), runContext());

        assertThat(seenPath.get()).startsWith("/services/data/v66.0/ssot/query-sql?");
        assertThat(seenPath.get()).contains("dataspace=default", "workloadName=data360-agent-console");
        assertThat(seenBody.get()).contains("SELECT Id FROM UnifiedIndividual LIMIT 10");
        assertThat(result.output()).containsEntry("rowCount", 1);
        assertThat(result.output()).containsKey("rows");
    }

    @Test
    void cachesSuccessfulMutationByRunAndStep() throws Exception {
        var calls = new AtomicInteger();
        server = startServer(exchange -> {
            calls.incrementAndGet();
            respond(exchange, 200, "{\"id\":\"seg_123\",\"status\":\"draft\"}");
        });
        var client = client(server);
        var operation = new OperationRegistry().require(Data360Action.CREATE_SEGMENT);
        var step = new PlanStep("create_segment", "Create segment", Data360Action.CREATE_SEGMENT, Map.of(
                "name", "Dormant Accounts",
                "criteria", Map.of("sql", "SELECT Id FROM UnifiedIndividual LIMIT 10")
        ), List.of(), true);

        var first = client.call(operation, step, step.input(), runContext());
        var second = client.call(operation, step, step.input(), runContext());

        assertThat(calls.get()).isEqualTo(1);
        assertThat(first.output()).containsEntry("segmentId", "seg_123");
        assertThat(second.output()).isEqualTo(first.output());
    }

    private HttpData360ConnectClient client(HttpServer server) {
        var baseUrl = "http://localhost:" + server.getAddress().getPort();
        var properties = new AppProperties("local", new AppProperties.Data360(
                "connect",
                new AppProperties.Mcp(""),
                new AppProperties.Connect("v66.0", baseUrl, "d360_token", "", "", "", "", "", "", "", "", 5, 60)
        ));
        var mapper = new ObjectMapper();
        var tokenProvider = new ConnectTokenProvider(properties, mapper, WebClient.builder());
        return new HttpData360ConnectClient(properties, mapper, WebClient.builder(), tokenProvider, new ConnectApiIdempotencyStore());
    }

    private RunContext runContext() {
        return new RunContext("run_1", "plan_1", new PlanContext("org", "default", "sandbox"));
    }

    private HttpServer startServer(Handler handler) throws IOException {
        var httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/", exchange -> {
            try {
                handler.handle(exchange);
            } finally {
                exchange.close();
            }
        });
        httpServer.start();
        return httpServer;
    }

    private void respond(com.sun.net.httpserver.HttpExchange exchange, int status, String body) throws IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private interface Handler {
        void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException;
    }
}
