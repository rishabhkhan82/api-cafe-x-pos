package com.cafex.pos.controller;

import com.cafex.pos.dto.LoginRequest;
import com.cafex.pos.dto.LoginResponse;
import com.cafex.pos.dto.ForgotPasswordRequest;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import com.cafex.pos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getUsername());

        LoginResponse response = authService.login(loginRequest);
        log.info("Login successful for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<OperationResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request received for identifier: {}", request.getIdentifier());
        authService.forgotPassword(request.getIdentifier());
        log.info("Forgot password processed for identifier: {}", request.getIdentifier());
        return ResponseEntity.ok(new OperationResponse("success", "PASSWORD_SENT", null, null));
    }

    @GetMapping("/debug/db")
    public ResponseEntity<?> debugDatabase() {
        List<User> users = userRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("database", "connected");
        response.put("totalUsers", users.size());
        if (!users.isEmpty()) {
            response.put("sampleUser", users.get(0).getUsername());
        }
        return ResponseEntity.ok(response);
    }
}
