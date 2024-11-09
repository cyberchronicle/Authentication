package org.cyberchronicle.auth.dto;

import java.util.UUID;

public sealed interface AuthRequestData {
    Long userId();

    record RefreshAuthData(Long userId, UUID tokenId) implements AuthRequestData {
    }

    record AccessAuthData(Long userId) implements AuthRequestData {
    }
}
