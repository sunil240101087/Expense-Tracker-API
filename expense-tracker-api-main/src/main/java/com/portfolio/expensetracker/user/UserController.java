package com.portfolio.expensetracker.user;

import com.portfolio.expensetracker.common.ResourceNotFoundException;
import com.portfolio.expensetracker.security.AppUserDetails;
import com.portfolio.expensetracker.security.CurrentUser;
import com.portfolio.expensetracker.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints relating to the currently authenticated account.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Information about the authenticated account")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile")
    public UserResponse me(@CurrentUser AppUserDetails principal) {
        return userRepository.findById(principal.getId())
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
