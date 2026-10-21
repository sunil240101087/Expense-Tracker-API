package com.portfolio.expensetracker.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized JWT configuration bound from the {@code security.jwt.*} namespace.
 *
 * @param secret              base64-encoded signing key (must be at least 256 bits)
 * @param expirationMinutes   token lifetime in minutes
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, long expirationMinutes) {
}
