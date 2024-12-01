package org.cyberchronicle.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 4, max = 100)
        String login,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(min = 2, max = 100)
        String firstName,

        @Size(min = 2, max = 100)
        @NotNull
        String lastName
) {
}
