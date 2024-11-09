package org.cyberchronicle.auth;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class AuthService {
    private final Duration tokenTtl = Duration.ofDays(2);
    private final SecretKey signingKey;
    private final JwtParser tokenParser;

    public AuthService(String key) {
        signingKey = Keys.hmacShaKeyFor(key.getBytes());
        tokenParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }

    public void addRole() {
    }

    public void removeRole() {
    }

    public String issueToken(String login) {
        var now = Instant.now();
        var expirationDate = now.plus(tokenTtl);

        return Jwts.builder()
                .claim("login", login)
                .expiration(Date.from(expirationDate))
                .signWith(signingKey)
                .compact();
    }


    public RequestUser parse(String token) {
        var parsed = tokenParser.parseSignedClaims(token);
        var login = parsed.getPayload().get("login", String.class);

        // todo: fetch roles
        //var authorities = Collections.<String>emptyList();
        var authorities = List.of("USER");

        return new RequestUser(login, authorities);
    }
}
