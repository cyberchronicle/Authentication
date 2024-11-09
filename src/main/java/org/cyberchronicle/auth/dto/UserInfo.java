package org.cyberchronicle.auth.dto;

import java.util.List;

public record UserInfo(
        Long id,
        String firstName,
        String lastName,
        String login,
        List<String> roles
) {
}
