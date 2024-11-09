package org.cyberchronicle.auth;

public record RegisterRequest(
        String login,
        String password,
        String firstName,
        String lastName
) {
}
