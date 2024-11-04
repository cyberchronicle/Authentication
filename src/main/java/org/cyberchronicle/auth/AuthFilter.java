package org.cyberchronicle.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.function.Function;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.removeRequestHeader;

@RequiredArgsConstructor
public class AuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_AUTHORITIES = "X-User-Authorities";

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @NonNull
    public ServerResponse filter(
            ServerRequest request,
            @NonNull HandlerFunction<ServerResponse> next
    ) throws Exception {
        var authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorMessage("Missing authorization header"));
        }

        if (!authHeader.startsWith(BEARER_HEADER_PREFIX)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorMessage("Authorization header is not bearer token"));
        }

        var rawToken = authHeader.substring(BEARER_HEADER_PREFIX.length());

        try {
            var requestUser = authService.parse(rawToken);
            var jsonAuthorities = objectMapper.writeValueAsString(requestUser.authorities());

            var targetRequest = addRequestHeader(X_USER_ID, requestUser.login())
                    .andThen(addRequestHeader(X_USER_AUTHORITIES, jsonAuthorities))
                    .andThen(removeRequestHeader(HttpHeaders.AUTHORIZATION))
                    .apply(request);

            return next.handle(targetRequest);
        } catch (JwtException exc) {
            var msg = "Permission denied";
            if (exc instanceof ExpiredJwtException) {
                msg = "Token has expired";
            }
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .body(new AuthErrorMessage(msg));
        }
    }
}
