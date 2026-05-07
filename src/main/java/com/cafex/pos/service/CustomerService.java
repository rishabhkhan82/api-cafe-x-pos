package com.cafex.pos.service;

import com.cafex.pos.dto.CustomerCreateRequest;
import com.cafex.pos.dto.CustomerLoginResponse;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.RestaurantRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public CustomerLoginResponse createCustomer(CustomerCreateRequest request) {
        log.info("Creating new customer for restaurant: {}", request.getRestaurantId());

        // Generate unique customerId
        String customerId = generateCustomerId();

        // Find restaurant
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(request.getRestaurantId());
        if (restaurantOpt.isEmpty()) {
            throw new RuntimeException("Restaurant not found");
        }

        Restaurant restaurant = restaurantOpt.get();

        // Check email uniqueness only if email is provided (not null/empty)
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        if (!email.isEmpty()) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(email);
            if (existingCustomer.isPresent()) {
                throw new RuntimeException("Customer with this email already exists");
            }
        }

        // Create customer with defaults for optional fields
        Customer customer = new Customer(
            customerId,
            request.getName() != null ? request.getName() : "Guest",
            email,
            request.getPhone() != null ? request.getPhone() : "",
            request.getAvatar() != null ? request.getAvatar() : "/uploads/images/avatar.avif",
            restaurant
        );

        Customer savedCustomer = customerRepository.save(customer);

        // Generate JWT token for customer
        String accessToken = generateToken(savedCustomer);

        CustomerLoginResponse response = new CustomerLoginResponse();
        response.setCustomer(savedCustomer);
        response.setAccessToken(accessToken);
        response.setExpiresIn(jwtExpiration / 1000); // Convert to seconds

        log.info("Customer created successfully: {}", savedCustomer.getCustomerId());
        return response;
    }

    private String generateCustomerId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String generateToken(Customer customer) {
        return Jwts.builder()
                .setSubject(customer.getCustomerId())
                .claim("customerId", customer.getCustomerId())
                .claim("type", "customer")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}