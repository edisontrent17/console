package com.acme.data360agent.identity;

import com.acme.data360agent.support.Ids;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class IdentityService {
    public static final String SESSION_COOKIE = "D360_SESSION";
    private static final Duration SESSION_TTL = Duration.ofDays(14);
    private static final String DEFAULT_ORG_ID = "org_default";

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwords;
    private final SecureRandom random = new SecureRandom();

    public IdentityService(JdbcTemplate jdbc, PasswordEncoder passwords) {
        this.jdbc = jdbc;
        this.passwords = passwords;
    }

    public boolean setupRequired() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM app_users", Integer.class);
        return count == null || count == 0;
    }

    @Transactional
    public SessionLogin bootstrap(IdentityRequests.BootstrapRequest request) {
        if (!setupRequired()) {
            throw new IllegalArgumentException("Initial organization already exists.");
        }
        var organization = createOrganization(request.organizationName());
        var user = createUser(organization.id(), request.email(), request.displayName(), request.password(), "OWNER");
        return createSession(user);
    }

    @Transactional
    public Organization createOrganization(String name) {
        var id = Ids.prefixed("org");
        var normalized = cleanName(name);
        jdbc.update("""
                INSERT INTO organizations (organization_id, name)
                VALUES (?, ?)
                """, id, normalized);
        return organization(id).orElseThrow();
    }

    @Transactional
    public PublicUser createUser(String organizationId, IdentityRequests.CreateUserRequest request) {
        var role = normalizeRole(request.role());
        var user = createUser(organizationId, request.email(), request.displayName(), request.password(), role);
        return PublicUser.from(user, authorityNames(role));
    }

    public Optional<Organization> organization(String organizationId) {
        var results = jdbc.query("""
                SELECT organization_id, name, created_at
                FROM organizations
                WHERE organization_id = ?
                """, (rs, rowNum) -> organization(rs), organizationId);
        return results.stream().findFirst();
    }

    public List<Organization> organizations() {
        return jdbc.query("""
                SELECT organization_id, name, created_at
                FROM organizations
                ORDER BY created_at DESC
                """, (rs, rowNum) -> organization(rs));
    }

    public List<PublicUser> users(String organizationId) {
        return jdbc.query("""
                SELECT u.user_id, u.organization_id, o.name AS organization_name, u.email, u.display_name,
                       u.password_hash, u.role, u.status, u.created_at, u.updated_at
                FROM app_users u
                JOIN organizations o ON o.organization_id = u.organization_id
                WHERE u.organization_id = ?
                ORDER BY u.created_at DESC
                """, (rs, rowNum) -> {
                    var user = user(rs);
                    return PublicUser.from(user, authorityNames(user.role()));
                }, organizationId);
    }

    public Optional<LocalUser> findUserByEmail(String email) {
        var results = jdbc.query("""
                SELECT u.user_id, u.organization_id, o.name AS organization_name, u.email, u.display_name,
                       u.password_hash, u.role, u.status, u.created_at, u.updated_at
                FROM app_users u
                JOIN organizations o ON o.organization_id = u.organization_id
                WHERE LOWER(u.email) = LOWER(?)
                ORDER BY u.created_at
                """, (rs, rowNum) -> user(rs), cleanEmail(email));
        return results.stream().findFirst();
    }

    public Optional<LocalUser> findUserById(String userId) {
        var results = jdbc.query("""
                SELECT u.user_id, u.organization_id, o.name AS organization_name, u.email, u.display_name,
                       u.password_hash, u.role, u.status, u.created_at, u.updated_at
                FROM app_users u
                JOIN organizations o ON o.organization_id = u.organization_id
                WHERE u.user_id = ?
                """, (rs, rowNum) -> user(rs), userId);
        return results.stream().findFirst();
    }

    public SessionLogin login(String email, String password) {
        var user = findUserByEmail(email)
                .filter(LocalUser::active)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!passwords.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return createSession(user);
    }

    @Transactional
    public SessionLogin createSession(LocalUser user) {
        var token = newToken();
        var hash = hashToken(token);
        jdbc.update("""
                INSERT INTO user_sessions (session_id, user_id, session_token_hash, expires_at)
                VALUES (?, ?, ?, ?)
                """, Ids.prefixed("ses"), user.id(), hash, Instant.now().plus(SESSION_TTL));
        return new SessionLogin(token, new LocalUserPrincipal(user, authorities(user.role())));
    }

    @Transactional
    public void revoke(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        jdbc.update("""
                UPDATE user_sessions
                SET revoked_at = ?
                WHERE session_token_hash = ? AND revoked_at IS NULL
                """, Instant.now(), hashToken(token));
    }

    public Optional<LocalUserPrincipal> principalForSession(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            var user = jdbc.queryForObject("""
                    SELECT u.user_id, u.organization_id, o.name AS organization_name, u.email, u.display_name,
                           u.password_hash, u.role, u.status, u.created_at, u.updated_at
                    FROM user_sessions s
                    JOIN app_users u ON u.user_id = s.user_id
                    JOIN organizations o ON o.organization_id = u.organization_id
                    WHERE s.session_token_hash = ?
                      AND s.revoked_at IS NULL
                      AND s.expires_at > ?
                    """, (rs, rowNum) -> user(rs), hashToken(token), Instant.now());
            if (user == null || !user.active()) {
                return Optional.empty();
            }
            return Optional.of(new LocalUserPrincipal(user, authorities(user.role())));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public String defaultOrganizationId() {
        ensureDefaultOrganization();
        return DEFAULT_ORG_ID;
    }

    @Transactional
    public void ensureDefaultOrganization() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM organizations WHERE organization_id = ?", Integer.class, DEFAULT_ORG_ID);
        if (count != null && count > 0) {
            return;
        }
        jdbc.update("""
                INSERT INTO organizations (organization_id, name)
                VALUES (?, ?)
                """, DEFAULT_ORG_ID, "Default Organization");
    }

    private LocalUser createUser(String organizationId, String email, String displayName, String password, String role) {
        var normalizedEmail = cleanEmail(email);
        var id = Ids.prefixed("usr");
        jdbc.update("""
                INSERT INTO app_users (user_id, organization_id, email, display_name, password_hash, role, status)
                VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')
                """, id, organizationId, normalizedEmail, cleanName(displayName), passwords.encode(password), normalizeRole(role));
        return findUserById(id).orElseThrow();
    }

    private List<GrantedAuthority> authorities(String role) {
        return authorityNames(role).stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private Set<String> authorityNames(String role) {
        var normalized = normalizeRole(role);
        if ("VIEWER".equals(normalized)) {
            return Set.of("SCOPE_data360.read");
        }
        if ("MEMBER".equals(normalized)) {
            return Set.of("SCOPE_data360.read", "SCOPE_data360.plan", "SCOPE_data360.execute", "SCOPE_data360.approve", "SCOPE_data360.monitor", "SCOPE_data360.demo");
        }
        return Set.of("ROLE_DATA360_ADMIN", "SCOPE_data360.admin", "SCOPE_data360.read", "SCOPE_data360.plan", "SCOPE_data360.execute", "SCOPE_data360.approve", "SCOPE_data360.monitor", "SCOPE_data360.demo");
    }

    private String normalizeRole(String role) {
        var value = role == null || role.isBlank() ? "MEMBER" : role.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "OWNER", "ADMIN", "MEMBER", "VIEWER" -> value;
            default -> "MEMBER";
        };
    }

    private String cleanEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        return name.trim();
    }

    private String newToken() {
        var bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable.", e);
        }
    }

    private Organization organization(ResultSet rs) throws SQLException {
        return new Organization(
                rs.getString("organization_id"),
                rs.getString("name"),
                rs.getTimestamp("created_at").toInstant()
        );
    }

    private LocalUser user(ResultSet rs) throws SQLException {
        return new LocalUser(
                rs.getString("user_id"),
                rs.getString("organization_id"),
                rs.getString("organization_name"),
                rs.getString("email"),
                rs.getString("display_name"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }

    public record SessionLogin(
            String token,
            LocalUserPrincipal principal
    ) {
    }
}
