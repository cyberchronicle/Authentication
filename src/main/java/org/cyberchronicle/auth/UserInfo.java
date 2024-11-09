package org.cyberchronicle.auth;

public record UserInfo(
        String firstName,
        String lastName,
        String login
) {
}
