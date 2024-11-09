package org.cyberchronicle.auth;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private final Duration refreshTokenTtl = Duration.ofDays(20);
    private final Duration accessTokenTtl = Duration.ofMinutes(3);
    private final SecretKey signingKey;
    private final JwtParser tokenParser;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(@Value("${token.sign.key}") String key, RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        signingKey = Keys.hmacShaKeyFor(key.getBytes());
        tokenParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }

    @Transactional
    public TokenResponse refresh(Long userId, UUID currentRefreshTokenId) {
        return new TokenResponse(
                issueRefreshToken(userId, currentRefreshTokenId),
                issueAccessToken(userId)
        );
    }

    @Transactional
    public TokenResponse issueNewTokens(Long userId) {
        return new TokenResponse(
                issueNewRefreshToken(userId),
                issueAccessToken(userId)
        );
    }

    public AuthRequestData parse(String raw) {
        var parsed = tokenParser.parseSignedClaims(raw);
        var payload = parsed.getPayload();
        var userId = payload.get("userId", Long.class);
        var tokenType = payload.get("tokenType", String.class);

        if ("refresh".equals(tokenType)) {
            var refreshTokenId = UUID.fromString(payload.get("refreshTokenId", String.class));
            return new AuthRequestData.RefreshAuthData(userId, refreshTokenId);
        } else {
            return new AuthRequestData.AccessAuthData(userId);
        }
    }

    private String issueNewRefreshToken(Long userId) {
        var now = Instant.now();
        var expirationDate = now.plus(refreshTokenTtl);

        var updatedRefreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .expiresAt(expirationDate)
                        .userId(userId)
                        .build()
        );

        return Jwts.builder()
                .claim("tokenType", "refresh")
                .claim("refreshTokenId", updatedRefreshToken.getId())
                .claim("userId", userId)
                .expiration(Date.from(expirationDate))
                .signWith(signingKey)
                .compact();
    }

    private String issueRefreshToken(Long userId, UUID currentRefreshTokenId) {
        var currentToken =  refreshTokenRepository.findById(currentRefreshTokenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token"));
        refreshTokenRepository.deleteById(currentRefreshTokenId);
        return issueNewRefreshToken(userId);
    }

    private String issueAccessToken(Long userId) {
        var now = Instant.now();
        var expirationDate = now.plus(accessTokenTtl);
        return Jwts.builder()
                .claim("tokenType", "access")
                .claim("userId", userId)
                .expiration(Date.from(expirationDate))
                .signWith(signingKey)
                .compact();
    }
}
