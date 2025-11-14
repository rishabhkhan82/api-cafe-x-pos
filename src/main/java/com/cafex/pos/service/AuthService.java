package com.cafex.pos.service;

import com.cafex.pos.dto.LoginRequest;
import com.cafex.pos.dto.LoginResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();

        // Check password - handle both hashed and plain text for development
        boolean passwordMatches = false;
        if (passwordEncoder != null) {
            passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        }
        if (!passwordMatches) {
            // For development, also check plain text password
            passwordMatches = loginRequest.getPassword().equals(user.getPassword());
        }

        if (!passwordMatches) {
            throw new RuntimeException("Invalid username or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String accessToken = generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setUser(user);
        response.setAccessToken(accessToken);
        response.setExpiresIn(jwtExpiration / 1000); // Convert to seconds

        log.info("Login successful for user: {}", user.getUsername());
        return response;
    }

    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Optional<User> getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }

    public String extractUsernameFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
}
