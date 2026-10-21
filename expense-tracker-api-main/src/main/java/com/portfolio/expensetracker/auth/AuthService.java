package com.portfolio.expensetracker.auth;

import com.portfolio.expensetracker.auth.dto.AuthResponse;
import com.portfolio.expensetracker.auth.dto.LoginRequest;
import com.portfolio.expensetracker.auth.dto.RegisterRequest;
import com.portfolio.expensetracker.common.EmailAlreadyUsedException;
import com.portfolio.expensetracker.security.JwtService;
import com.portfolio.expensetracker.user.User;
import com.portfolio.expensetracker.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and login, issuing JWTs on success.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException(request.email());
        }
        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.displayName());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return AuthResponse.bearer(token, jwtService.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException on failure, handled globally.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        String token = jwtService.generateToken(request.email());
        return AuthResponse.bearer(token, jwtService.getExpirationSeconds());
    }
}
