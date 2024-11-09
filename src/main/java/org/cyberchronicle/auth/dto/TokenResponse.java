package org.cyberchronicle.auth.dto;

public record TokenResponse(
        String refresh,
        String access
) {
}
