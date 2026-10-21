package com.portfolio.expensetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * Issues and validates signed JSON Web Tokens.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.expirationMinutes();
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

    /**
     * Generates a signed token whose subject is the user's email.
     */
    public String generateToken(String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(getExpirationSeconds());
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, String expectedEmail) {
        try {
            String email = extractEmail(token);
            return email.equals(expectedEmail) && !isExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
