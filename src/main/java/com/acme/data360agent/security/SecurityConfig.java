package com.acme.data360agent.security;

import com.acme.data360agent.config.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String ADMIN = "SCOPE_data360.admin";
    private static final String ROLE_ADMIN = "ROLE_DATA360_ADMIN";
    private static final String[] READ = {ADMIN, ROLE_ADMIN, "SCOPE_data360.read"};
    private static final String[] PLAN = {ADMIN, ROLE_ADMIN, "SCOPE_data360.plan"};
    private static final String[] EXECUTE = {ADMIN, ROLE_ADMIN, "SCOPE_data360.execute"};
    private static final String[] APPROVE = {ADMIN, ROLE_ADMIN, "SCOPE_data360.approve"};
    private static final String[] MONITOR = {ADMIN, ROLE_ADMIN, "SCOPE_data360.monitor"};
    private static final String[] DEMO = {ADMIN, ROLE_ADMIN, "SCOPE_data360.demo"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties properties) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!properties.resolvedEnabled()) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/scenarios", "/api/library", "/api/library/*").hasAnyAuthority(READ)
                        .requestMatchers(HttpMethod.GET, "/api/plans", "/api/plans/*", "/api/runs/*", "/api/runs/*/approvals").hasAnyAuthority(READ)
                        .requestMatchers(HttpMethod.GET, "/api/monitors", "/api/monitors/*", "/api/monitors/*/runs", "/api/monitors/recommendations").hasAnyAuthority(READ)
                        .requestMatchers(HttpMethod.GET, "/api/demo/dormant-revenue-recovery").hasAnyAuthority(READ)
                        .requestMatchers(HttpMethod.POST, "/api/plans", "/api/library/*/plans").hasAnyAuthority(PLAN)
                        .requestMatchers(HttpMethod.POST, "/api/plans/*/runs").hasAnyAuthority(EXECUTE)
                        .requestMatchers(HttpMethod.POST, "/api/runs/*/steps/*/approve", "/api/monitors/recommendations/*/approve", "/api/monitors/recommendations/*/reject").hasAnyAuthority(APPROVE)
                        .requestMatchers(HttpMethod.POST, "/api/monitors/*/run-now").hasAnyAuthority(MONITOR)
                        .requestMatchers(HttpMethod.GET, "/api/runs/*/audit").hasAnyAuthority(ADMIN, ROLE_ADMIN)
                        .requestMatchers("/api/data360/diagnostics", "/api/data360/diagnostics/**").hasAnyAuthority(ADMIN, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/demo/dormant-revenue-recovery/**").hasAnyAuthority(DEMO)
                        .requestMatchers("/api/**").denyAll()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(properties))));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(SecurityProperties properties) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.resolvedAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder(org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties properties, SecurityProperties security) {
        var jwt = properties.getJwt();
        if (security.resolvedEnabled() && (jwt.getIssuerUri() == null || jwt.getIssuerUri().isBlank())) {
            throw new IllegalStateException("OAUTH2_ISSUER_URI is required when app.security.enabled=true.");
        }
        if (security.resolvedEnabled() && (security.requiredAudience() == null || security.requiredAudience().isBlank())) {
            throw new IllegalStateException("APP_SECURITY_REQUIRED_AUDIENCE is required when app.security.enabled=true.");
        }
        NimbusJwtDecoder decoder;
        if (jwt.getJwkSetUri() != null && !jwt.getJwkSetUri().isBlank()) {
            decoder = NimbusJwtDecoder.withJwkSetUri(jwt.getJwkSetUri()).build();
        } else if (jwt.getIssuerUri() != null && !jwt.getIssuerUri().isBlank()) {
            decoder = NimbusJwtDecoder.withIssuerLocation(jwt.getIssuerUri()).build();
        } else {
            return token -> {
                throw new IllegalStateException("JWT auth is enabled but no issuer-uri or jwk-set-uri is configured.");
            };
        }
        var validator = jwt.getIssuerUri() == null || jwt.getIssuerUri().isBlank()
                ? JwtValidators.createDefault()
                : JwtValidators.createDefaultWithIssuer(jwt.getIssuerUri());
        if (security.requiredAudience() != null && !security.requiredAudience().isBlank()) {
            validator = new DelegatingOAuth2TokenValidator<>(validator, new JwtAudienceValidator(security.requiredAudience()));
        }
        decoder.setJwtValidator(validator);
        return decoder;
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(SecurityProperties properties) {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> authorities(jwt, properties.resolvedAuthorityClaim()));
        converter.setPrincipalClaimName("sub");
        return converter;
    }

    private Collection<GrantedAuthority> authorities(Jwt jwt, String claimName) {
        var authorities = new ArrayList<GrantedAuthority>();
        addAuthorities(authorities, jwt.getClaim(claimName), "SCOPE_");
        addAuthorities(authorities, jwt.getClaim("roles"), "ROLE_");
        return List.copyOf(authorities);
    }

    @SuppressWarnings("unchecked")
    private void addAuthorities(List<GrantedAuthority> authorities, Object value, String prefix) {
        if (value instanceof String string) {
            for (var token : string.split("\\s+")) {
                addOne(authorities, token, prefix);
            }
        } else if (value instanceof Collection<?> collection) {
            collection.forEach(item -> addOne(authorities, String.valueOf(item), prefix));
        } else if (value instanceof Map<?, ?> map && map.get("roles") instanceof Collection<?> roles) {
            roles.forEach(item -> addOne(authorities, String.valueOf(item), "ROLE_"));
        }
    }

    private void addOne(List<GrantedAuthority> authorities, String value, String prefix) {
        if (value == null || value.isBlank()) {
            return;
        }
        var normalized = value.startsWith("ROLE_") || value.startsWith("SCOPE_") ? value : prefix + value;
        authorities.add(new SimpleGrantedAuthority(normalized));
    }
}
