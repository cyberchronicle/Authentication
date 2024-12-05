package org.cyberchronicle.auth.dto;

public record RegisterResponse(
        Long userId,
        String refresh,
        String access
) {
    public static RegisterResponse of(Long userId, TokenResponse tokenResponse) {
        return new RegisterResponse(userId, tokenResponse.refresh(), tokenResponse.access());
    }
}
