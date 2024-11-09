package org.cyberchronicle.auth;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenResponse(
        UUID uuid,
        Instant expiresAt
) {
}
