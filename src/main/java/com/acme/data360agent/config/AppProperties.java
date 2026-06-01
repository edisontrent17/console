package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String executor,
        Data360 data360
) {
    public record Data360(String client, Mcp mcp, Connect connect) {
    }

    public record Mcp(String command) {
    }

    public record Connect(
            String apiVersion,
            String instanceUrl,
            String accessToken,
            String salesforceInstanceUrl,
            String salesforceAccessToken,
            String loginUrl,
            String clientId,
            String username,
            String privateKey,
            String privateKeyPath,
            String workloadName,
            Integer timeoutSeconds,
            Integer tokenSkewSeconds
    ) {
        public String resolvedApiVersion() {
            return apiVersion == null || apiVersion.isBlank() ? "v66.0" : apiVersion;
        }

        public String resolvedLoginUrl() {
            return loginUrl == null || loginUrl.isBlank() ? "https://login.salesforce.com" : loginUrl;
        }

        public String resolvedWorkloadName() {
            return workloadName == null || workloadName.isBlank() ? "data360-agent-console" : workloadName;
        }

        public int resolvedTimeoutSeconds() {
            return timeoutSeconds == null || timeoutSeconds < 1 ? 45 : timeoutSeconds;
        }

        public int resolvedTokenSkewSeconds() {
            return tokenSkewSeconds == null || tokenSkewSeconds < 1 ? 60 : tokenSkewSeconds;
        }
    }
}
