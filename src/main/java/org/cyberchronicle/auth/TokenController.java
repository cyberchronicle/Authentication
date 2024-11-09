package org.cyberchronicle.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_ROLES = "X-User-Roles";

    private final TokenService tokenService;
    private final UserService userService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/refresh")
    public TokenResponse refresh(HttpServletRequest request) {
        var authHeader = extractAuthHeader(request);
        var authData = tokenService.parse(authHeader);
        if (!(authData instanceof AuthRequestData.RefreshAuthData refreshAuthData)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot refresh with access token");
        }
        return tokenService.refresh(refreshAuthData.userId(), refreshAuthData.tokenId());
    }

    @PostMapping("/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response) {
        var authHeader = extractAuthHeader(request);
        try {
            var authData = tokenService.parse(authHeader);
            if (!(authData instanceof AuthRequestData.AccessAuthData)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token type");
            }

            var userId = authData.userId();
            var roles = userService.fetchRoles(userId).stream()
                    .map(UserRole::getRole)
                    .toList();
            var jsonRoles = objectMapper.writeValueAsString(roles);

            response.addHeader(X_USER_ID, userId.toString());
            response.addHeader(X_USER_ROLES, jsonRoles);
        } catch (JwtException exc) {
            var msg = "Permission denied";
            if (exc instanceof ExpiredJwtException) {
                msg = "Token has expired";
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(500));
        }
    }

    private String extractAuthHeader(HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing authorization header");
        }
        if (!authHeader.startsWith(BEARER_HEADER_PREFIX)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Authorization header is not bearer token"
            );
        }
        return authHeader.substring(BEARER_HEADER_PREFIX.length());
    }
}
