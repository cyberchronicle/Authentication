package org.cyberchronicle.auth;

public record LoginRequest(
        String login,
        String password
) {
}
