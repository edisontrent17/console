package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ConnectTokenProvider {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder builder;
    private volatile ConnectAuthSession cached;

    public ConnectTokenProvider(AppProperties properties, ObjectMapper objectMapper, WebClient.Builder builder) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.builder = builder;
    }

    public ConnectAuthSession session() {
        var connect = connect();
        var current = cached;
        if (current != null && !current.expiresSoon(connect.resolvedTokenSkewSeconds())) {
            return current;
        }
        synchronized (this) {
            current = cached;
            if (current != null && !current.expiresSoon(connect.resolvedTokenSkewSeconds())) {
                return current;
            }
            cached = createSession(connect);
            return cached;
        }
    }

    private ConnectAuthSession createSession(AppProperties.Connect connect) {
        if (hasText(connect.instanceUrl()) && hasText(connect.accessToken())) {
            return new ConnectAuthSession(trimTrailingSlash(connect.instanceUrl()), connect.accessToken(), Instant.now().plus(Duration.ofDays(365)));
        }
        if (hasText(connect.salesforceInstanceUrl()) && hasText(connect.salesforceAccessToken())) {
            return exchangeForData360Token(connect, connect.salesforceInstanceUrl(), connect.salesforceAccessToken());
        }
        if (hasJwtConfig(connect)) {
            var salesforce = requestSalesforceJwtToken(connect);
            return exchangeForData360Token(connect, stringValue(salesforce.get("instance_url")), stringValue(salesforce.get("access_token")));
        }
        throw new IllegalStateException("Data 360 Connect client requires app.data360.connect.instance-url + access-token, Salesforce access token exchange settings, or JWT settings.");
    }

    private Map<String, Object> requestSalesforceJwtToken(AppProperties.Connect connect) {
        var assertion = signJwt(connect);
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        form.add("assertion", assertion);
        var raw = builder.baseUrl(trimTrailingSlash(connect.resolvedLoginUrl()))
                .build()
                .post()
                .uri("/services/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(connect.resolvedTimeoutSeconds()));
        return readMap(raw);
    }

    private ConnectAuthSession exchangeForData360Token(AppProperties.Connect connect, String salesforceInstanceUrl, String salesforceAccessToken) {
        if (!hasText(salesforceInstanceUrl) || !hasText(salesforceAccessToken)) {
            throw new IllegalStateException("Salesforce instance URL and access token are required for Data 360 token exchange.");
        }
        var raw = builder.baseUrl(trimTrailingSlash(salesforceInstanceUrl))
                .build()
                .post()
                .uri("/services/a360/token")
                .header("Authorization", "Bearer " + salesforceAccessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(connect.resolvedTimeoutSeconds()));
        var response = readMap(raw);
        var instanceUrl = stringValue(response.get("instance_url"));
        var token = stringValue(response.get("access_token"));
        if (!hasText(instanceUrl) || !hasText(token)) {
            throw new IllegalStateException("Data 360 token exchange response must include instance_url and access_token.");
        }
        return new ConnectAuthSession(trimTrailingSlash(instanceUrl), token, Instant.now().plus(Duration.ofMinutes(55)));
    }

    private String signJwt(AppProperties.Connect connect) {
        try {
            var header = Map.of("alg", "RS256", "typ", "JWT");
            var now = Instant.now();
            var claims = new LinkedHashMap<String, Object>();
            claims.put("iss", connect.clientId());
            claims.put("sub", connect.username());
            claims.put("aud", connect.resolvedLoginUrl());
            claims.put("exp", now.plus(Duration.ofMinutes(3)).getEpochSecond());

            var signingInput = base64Url(objectMapper.writeValueAsBytes(header))
                    + "."
                    + base64Url(objectMapper.writeValueAsBytes(claims));
            var signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey(connect));
            signature.update(signingInput.getBytes(StandardCharsets.UTF_8));
            return signingInput + "." + base64Url(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign Salesforce JWT bearer assertion.", e);
        }
    }

    private RSAPrivateKey privateKey(AppProperties.Connect connect) throws Exception {
        var pem = hasText(connect.privateKey())
                ? connect.privateKey()
                : Files.readString(Path.of(connect.privateKeyPath()), StandardCharsets.UTF_8);
        var normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            throw new IllegalArgumentException("Use a PKCS#8 private key PEM with BEGIN PRIVATE KEY, not BEGIN RSA PRIVATE KEY.");
        }
        var keyBytes = Base64.getDecoder().decode(normalized);
        var spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private AppProperties.Connect connect() {
        var data360 = properties.data360();
        if (data360 == null || data360.connect() == null) {
            return new AppProperties.Connect(null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return data360.connect();
    }

    private boolean hasJwtConfig(AppProperties.Connect connect) {
        return hasText(connect.clientId())
                && hasText(connect.username())
                && (hasText(connect.privateKey()) || hasText(connect.privateKeyPath()));
    }

    private Map<String, Object> readMap(String raw) {
        try {
            return objectMapper.readValue(raw, MAP);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse Data 360 auth response.", e);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trimTrailingSlash(String value) {
        return value == null ? null : value.replaceAll("/+$", "");
    }
}
