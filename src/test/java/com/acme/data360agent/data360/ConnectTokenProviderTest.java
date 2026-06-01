package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectTokenProviderTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void exchangesSalesforceTokenForData360Token() throws Exception {
        var authorization = new AtomicReference<String>();
        server = startServer(exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            respond(exchange, 200, "{\"instance_url\":\"http://data360.example\",\"access_token\":\"d360_token\"}");
        });
        var baseUrl = "http://localhost:" + server.getAddress().getPort();
        var properties = new AppProperties("local", new AppProperties.Data360(
                "connect",
                new AppProperties.Mcp(""),
                new AppProperties.Connect("v66.0", "", "", baseUrl, "sf_token", "", "", "", "", "", "", 5, 60)
        ));
        var provider = new ConnectTokenProvider(properties, new ObjectMapper(), WebClient.builder());

        var session = provider.session();

        assertThat(session.instanceUrl()).isEqualTo("http://data360.example");
        assertThat(session.accessToken()).isEqualTo("d360_token");
        assertThat(authorization.get()).isEqualTo("Bearer sf_token");
    }

    private HttpServer startServer(Handler handler) throws IOException {
        var httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/services/a360/token", exchange -> {
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
