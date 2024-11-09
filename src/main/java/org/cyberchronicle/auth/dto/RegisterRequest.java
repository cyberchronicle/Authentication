package org.cyberchronicle.auth.dto;

public record RegisterRequest(
        String login,
        String password,
        String firstName,
        String lastName
) {
}
