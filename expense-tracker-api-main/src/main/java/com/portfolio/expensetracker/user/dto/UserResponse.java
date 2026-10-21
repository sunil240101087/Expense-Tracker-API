package com.portfolio.expensetracker.user.dto;

import com.portfolio.expensetracker.user.Role;
import com.portfolio.expensetracker.user.User;
import java.time.Instant;
import java.util.Set;

/**
 * API representation of a user account (never exposes the password hash).
 */
public record UserResponse(
        Long id,
        String email,
        String displayName,
        Set<Role> roles,
        Instant createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
