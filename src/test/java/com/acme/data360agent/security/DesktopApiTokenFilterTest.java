package com.acme.data360agent.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class DesktopApiTokenFilterTest {
    @Test
    void ignoresRequestsWhenTokenIsNotConfigured() throws ServletException, IOException {
        var filter = new DesktopApiTokenFilter("");
        var request = new MockHttpServletRequest("GET", "/api/plans");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsApiRequestsWithoutMatchingDesktopToken() throws ServletException, IOException {
        var filter = new DesktopApiTokenFilter("secret-token");
        var request = new MockHttpServletRequest("GET", "/api/plans");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void acceptsApiRequestsWithMatchingDesktopToken() throws ServletException, IOException {
        var filter = new DesktopApiTokenFilter("secret-token");
        var request = new MockHttpServletRequest("GET", "/api/plans");
        request.addHeader("X-Data360-Desktop-Token", "secret-token");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
