package org.cyberchronicle.auth.dto;

public record LoginRequest(
        String login,
        String password
) {
}
