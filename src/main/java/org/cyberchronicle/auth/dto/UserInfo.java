package org.cyberchronicle.auth.dto;

public record UserInfo(
        String firstName,
        String lastName,
        String login
) {
}
