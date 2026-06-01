package com.acme.data360agent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DesktopApiTokenFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-Data360-Desktop-Token";
    private final String token;

    public DesktopApiTokenFilter(@Value("${app.desktop.api-token:}") String token) {
        this.token = token == null ? "" : token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (token.isBlank() || !request.getRequestURI().startsWith("/api/") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        var provided = request.getHeader(HEADER);
        if (!matches(provided)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Desktop API token is missing or invalid.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean matches(String provided) {
        if (provided == null || provided.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(bytes(token), bytes(provided));
    }

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
