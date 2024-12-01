package org.cyberchronicle.auth.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisterResponse(
        Long userId,
        @JsonUnwrapped
        TokenResponse tokenResponse
) {
}
