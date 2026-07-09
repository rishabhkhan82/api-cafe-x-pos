package com.cafex.pos.service;

import com.cafex.pos.dto.LoginRequest;
import com.cafex.pos.dto.LoginResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.repository.UserRepository;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.service.EmailService;
import com.cafex.pos.exception.BadRequestException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            throw new BadRequestException("Invalid username or password");
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
            throw new BadRequestException("Invalid username or password");
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

    public void forgotPassword(String identifier) {
        log.info("Forgot password request for identifier: {}", identifier);

        Optional<User> userOpt = Optional.empty();

        if (identifier != null && identifier.contains("@")) {
            userOpt = userRepository.findByEmail(identifier.trim());
        }
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(identifier.trim());
        }

        if (userOpt.isEmpty()) {
            log.warn("No user found for identifier: {}", identifier);
            throw new BadRequestException("No account found with the provided email or username");
        }

        User user = userOpt.get();
        String password = user.getPassword();

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("email", user.getEmail());
        variables.put("password", password);
        variables.put("loginUrl", "http://localhost:4200/login");

        emailService.sendHtmlEmail(
            user.getEmail(),
            "Your CafeX POS Password",
            "forgot_password.html",
            variables
        );

        log.info("Forgot password email sent to: {}", user.getEmail());
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

    public Optional<Customer> getCurrentCustomer(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    public String extractUsernameFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractTokenType(String token) {
        try {
            return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().get("type", String.class);
        } catch (Exception e) {
            log.error("Error extracting token type from token: {}", e.getMessage());
            return null;
        }
    }
}
