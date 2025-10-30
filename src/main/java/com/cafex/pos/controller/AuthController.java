package com.cafex.pos.controller;

import com.cafex.pos.dto.LoginRequest;
import com.cafex.pos.dto.LoginResponse;
import com.cafex.pos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<LoginResponse> response = authService.authenticate(loginRequest);

            if (response.isPresent()) {
                log.info("Login successful for user: {}", loginRequest.getUsername());
                return ResponseEntity.ok(response.get());
            } else {
                log.warn("Login failed for user: {}", loginRequest.getUsername());
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid username or password");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequest.getUsername(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Authentication failed");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                boolean isValid = authService.validateToken(jwtToken);

                Map<String, Object> response = new HashMap<>();
                response.put("valid", isValid);

                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid token format");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            log.error("Token validation error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Token validation failed");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}