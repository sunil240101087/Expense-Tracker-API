package com.portfolio.expensetracker.admin;

import com.portfolio.expensetracker.user.UserRepository;
import com.portfolio.expensetracker.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only endpoints. Access is restricted to ROLE_ADMIN via the security
 * configuration ("/api/v1/admin/**").
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Administrative operations (ROLE_ADMIN only)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users")
    public List<UserResponse> listUsers() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(UserResponse::from)
                .toList();
    }
}
