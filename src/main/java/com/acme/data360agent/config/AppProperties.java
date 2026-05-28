package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String executor,
        Data360 data360
) {
    public record Data360(String client, Mcp mcp) {
    }

    public record Mcp(String command) {
    }
}
