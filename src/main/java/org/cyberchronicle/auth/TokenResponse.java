package org.cyberchronicle.auth;

public record TokenResponse(
        String refresh,
        String access
) {
}
