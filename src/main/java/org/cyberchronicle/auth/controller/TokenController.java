package org.cyberchronicle.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cyberchronicle.auth.dto.AuthErrorResponse;
import org.cyberchronicle.auth.dto.AuthRequestData;
import org.cyberchronicle.auth.dto.TokenResponse;
import org.cyberchronicle.auth.model.UserRole;
import org.cyberchronicle.auth.service.TokenService;
import org.cyberchronicle.auth.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_ROLES = "X-User-Roles";
    private static final String EXPIRED_TOKEN_MESSAGE = "Token has expired";

    private final TokenService tokenService;
    private final UserService userService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenResponse refresh(HttpServletRequest request) {
        var authHeader = extractAuthHeader(request);
        var authData = tokenService.parse(authHeader);
        if (!(authData instanceof AuthRequestData.RefreshAuthData refreshAuthData)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot refresh with access token");
        }
        return tokenService.refresh(refreshAuthData.userId(), refreshAuthData.tokenId());
    }

    @GetMapping("/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        var authHeader = extractAuthHeader(request);
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
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<AuthErrorResponse> expiredTokenHandler(JwtException exception) {
        var msg = "Permission denied";
        if (exception instanceof ExpiredJwtException) {
            msg = "Token has expired";
        }
        var res = new AuthErrorResponse(msg);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(res);
    }

    private String extractAuthHeader(HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
        }
        if (!authHeader.startsWith(BEARER_HEADER_PREFIX)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authorization header is not bearer token"
            );
        }
        return authHeader.substring(BEARER_HEADER_PREFIX.length());
    }
}
