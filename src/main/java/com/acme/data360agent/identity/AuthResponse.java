package com.acme.data360agent.identity;

import com.acme.data360agent.security.AppUser;

import java.io.Serializable;

public record AuthResponse(
        AppUser user,
        boolean setupRequired
) implements Serializable {
}
