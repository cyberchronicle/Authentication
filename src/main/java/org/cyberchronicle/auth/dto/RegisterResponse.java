package org.cyberchronicle.auth.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record RegisterResponse(
        Long userId,
        @JsonUnwrapped
        TokenResponse tokenResponse
) {
    // Getters to hack springdoc
    public String getRefresh() {
        return tokenResponse.refresh();
    }

    public String getAccess() {
        return tokenResponse.access();
    }
}
