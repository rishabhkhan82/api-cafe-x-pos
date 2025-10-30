package com.cafex.pos.service;

import com.cafex.pos.dto.LoginRequest;
import com.cafex.pos.dto.LoginResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<LoginResponse> authenticate(LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty()) {
                log.warn("User not found: {}", loginRequest.getUsername());
                return Optional.empty();
            }

            User user = userOptional.get();

            if (!user.getIsActive()) {
                log.warn("Inactive user attempted login: {}", loginRequest.getUsername());
                return Optional.empty();
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", loginRequest.getUsername());
                return Optional.empty();
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Create simple session token (for now)
            String token = "SESSION_" + user.getId() + "_" + System.currentTimeMillis();

            LoginResponse response = new LoginResponse(token, user);
            log.info("User logged in successfully: {}", loginRequest.getUsername());

            return Optional.of(response);

        } catch (Exception e) {
            log.error("Authentication error for user: {}", loginRequest.getUsername(), e);
            return Optional.empty();
        }
    }

    public Optional<User> getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validateToken(String token) {
        // Simple token validation for now
        return token != null && token.startsWith("SESSION_");
    }
}