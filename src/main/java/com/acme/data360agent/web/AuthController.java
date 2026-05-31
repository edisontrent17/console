package com.acme.data360agent.web;

import com.acme.data360agent.identity.AuthResponse;
import com.acme.data360agent.identity.IdentityRequests;
import com.acme.data360agent.identity.IdentityService;
import com.acme.data360agent.security.AppUser;
import com.acme.data360agent.security.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final IdentityService identities;
    private final CurrentUserService users;

    public AuthController(IdentityService identities, CurrentUserService users) {
        this.identities = identities;
        this.users = users;
    }

    @GetMapping("/status")
    public AuthResponse status() {
        return new AuthResponse(users.currentUser(), identities.setupRequired());
    }

    @PostMapping("/bootstrap")
    public AuthResponse bootstrap(@Valid @RequestBody IdentityRequests.BootstrapRequest request, HttpServletResponse response) {
        var login = identities.bootstrap(request);
        setSessionCookie(response, login.token());
        return new AuthResponse(AppUser.from(login.principal()), false);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody IdentityRequests.LoginRequest request, HttpServletResponse response) {
        var login = identities.login(request.email(), request.password());
        setSessionCookie(response, login.token());
        return new AuthResponse(AppUser.from(login.principal()), false);
    }

    @PostMapping("/logout")
    public AuthResponse logout(HttpServletRequest request, HttpServletResponse response) {
        sessionToken(request).ifPresent(identities::revoke);
        clearSessionCookie(response);
        return new AuthResponse(AppUser.anonymous(), identities.setupRequired());
    }

    private void setSessionCookie(HttpServletResponse response, String token) {
        var cookie = ResponseCookie.from(IdentityService.SESSION_COOKIE, token)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearSessionCookie(HttpServletResponse response) {
        var cookie = ResponseCookie.from(IdentityService.SESSION_COOKIE, "")
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Optional<String> sessionToken(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> IdentityService.SESSION_COOKIE.equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }
}
