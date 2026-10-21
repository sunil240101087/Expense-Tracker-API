package com.portfolio.expensetracker.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-key-that-is-definitely-long-enough-32-bytes", 60);
        jwtService = new JwtService(properties);
    }

    @Test
    void generatesTokenWhoseSubjectIsTheEmail() {
        String token = jwtService.generateToken("user@example.com");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void validatesTokenForMatchingEmail() {
        String token = jwtService.generateToken("user@example.com");

        assertThat(jwtService.isValid(token, "user@example.com")).isTrue();
        assertThat(jwtService.isValid(token, "someone-else@example.com")).isFalse();
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.generateToken("user@example.com");
        String tampered = token.substring(0, token.length() - 2) + "xy";

        assertThat(jwtService.isValid(tampered, "user@example.com")).isFalse();
    }

    @Test
    void exposesExpirationInSeconds() {
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(3600);
    }
}
