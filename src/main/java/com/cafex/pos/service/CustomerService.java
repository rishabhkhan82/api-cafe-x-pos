package com.cafex.pos.service;

import com.cafex.pos.dto.*;
import com.cafex.pos.dto.CustomerWithTokenResponse;
import com.cafex.pos.dto.CustomerValidateResponse;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Optional<CustomerValidateResponse> validateCustomer(String customerId, Long restaurantId) {
        log.info("Validating customer by customerId: {}, restaurantId: {}", customerId, restaurantId);

        try {
            Optional<Customer> customerOpt = customerRepository.findByCustomerId(customerId);
            log.info("Customer lookup result for customerId {}: found = {}", customerId, customerOpt.isPresent());

            if (customerOpt.isEmpty()) {
                log.warn("Customer not found with customerId: {}", customerId);
                return Optional.empty();
            }

            Customer customer = customerOpt.get();
            log.info("Customer found: {} (ID: {})", customer.getName(), customer.getId());

            // Validate that customer belongs to the specified restaurant
            if (customer.getRestaurant() == null || !customer.getRestaurant().getId().equals(restaurantId)) {
                log.warn("Customer {} does not belong to restaurant {}", customerId, restaurantId);
                return Optional.empty();
            }

            // Generate new JWT token
            log.info("Generating new token for customer ID: {}", customerId);
            String accessToken = generateToken(customer);
            log.info("Token generated successfully for customer ID: {}", customerId);

            CustomerValidateResponse response = new CustomerValidateResponse();
            response.setCustomer(mapToCustomerResponse(customer));
            response.setAccessToken(accessToken);
            response.setExpiresIn(jwtExpiration / 1000);

            log.info("Customer validated and token refreshed for customerId: {}, restaurantId: {}", customerId, restaurantId);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Exception in validateCustomer for customerId {}, restaurantId {}: {}", customerId, restaurantId, e.getMessage(), e);
            throw e;
        }
    }

    public CustomerLoginResponse createCustomer(CustomerCreateRequest request) {
        log.info("Creating new customer for restaurant: {}", request.getRestaurantId());

        // Generate unique customerId
        String customerId = generateCustomerId();

        // Find restaurant
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(request.getRestaurantId());
        if (restaurantOpt.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        Restaurant restaurant = restaurantOpt.get();

        // Check email uniqueness only if email is provided (not null/empty)
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        if (!email.isEmpty()) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(email);
            if (existingCustomer.isPresent()) {
                throw new ConflictException("Customer with this email already exists");
            }
        }

        // Process avatar if it's base64 data
        String avatarPath = "/uploads/images/guest/guest-default-avatar.jpg";
        if (request.getAvatar() != null) {
            if (request.getAvatar().startsWith("data:")) {
                // Will process after customer creation
                avatarPath = request.getAvatar();
            } else {
                avatarPath = request.getAvatar();
            }
        }

        // Create customer with defaults for optional fields
        Customer customer = new Customer(
            customerId,
            request.getName() != null ? request.getName() : "Guest",
            email,
            request.getPhone() != null ? request.getPhone() : "",
            avatarPath,
            restaurant
        );

        Customer savedCustomer = customerRepository.save(customer);

        // Process avatar if it was base64 data
        if (request.getAvatar() != null && request.getAvatar().startsWith("data:")) {
            String processedAvatarPath = processAvatar(request.getAvatar(), savedCustomer.getId());
            savedCustomer.setAvatar(processedAvatarPath);
            savedCustomer = customerRepository.save(savedCustomer);
        }

        // Generate JWT token for customer
        String accessToken = generateToken(savedCustomer);

        CustomerLoginResponse response = new CustomerLoginResponse();
        response.setCustomer(savedCustomer);
        response.setAccessToken(accessToken);
        response.setExpiresIn(jwtExpiration / 1000); // Convert to seconds

        log.info("Customer created successfully: {}", savedCustomer.getCustomerId());
        return response;
    }

    public CustomerResponse saveCustomer(CustomerRequest request) {
        log.info("Saving customer: {}", request.getName());

        // Find restaurant
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(request.getRestaurantId());
        if (restaurantOpt.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        Restaurant restaurant = restaurantOpt.get();

        // Check email uniqueness if email is provided
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(request.getEmail());
            if (existingCustomer.isPresent()) {
                throw new ConflictException("Customer with this email already exists");
            }
        }

        Customer customer = new Customer();
        customer.setCustomerId(request.getCustomerId() != null ? request.getCustomerId() : generateCustomerId());
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAvatar(request.getAvatar() != null ? request.getAvatar() : "/uploads/images/avatar.avif");
        customer.setRestaurant(restaurant);
        customer.setTotalOrders(request.getTotalOrders() != null ? request.getTotalOrders() : 0);
        customer.setTotalSpent(request.getTotalSpent() != null ? request.getTotalSpent() : BigDecimal.ZERO);
        customer.setLoyaltyPoints(request.getLoyaltyPoints() != null ? request.getLoyaltyPoints() : 0);
        customer.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved successfully with ID: {}", savedCustomer.getId());

        // Publish to platform-wide topic
        emitCustomerUpdate(getAllCustomers());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));

        return mapToCustomerResponse(savedCustomer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found");
        }

        Customer customer = customerOpt.get();

        // Note: Email uniqueness is not enforced for customer updates to allow shared emails

        // Update fields
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());

        // Handle avatar update - convert base64 to file if needed
        if (request.getAvatar() != null) {
            String avatarPath = processAvatar(request.getAvatar(), customer.getId());
            customer.setAvatar(avatarPath);
        }
        if (request.getTotalOrders() != null) customer.setTotalOrders(request.getTotalOrders());
        if (request.getTotalSpent() != null) customer.setTotalSpent(request.getTotalSpent());
        if (request.getLoyaltyPoints() != null) customer.setLoyaltyPoints(request.getLoyaltyPoints());

        customer.setUpdatedAt(LocalDateTime.now());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());

        // Publish to platform-wide topic
        emitCustomerUpdate(getAllCustomers());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));

        return mapToCustomerResponse(updatedCustomer);
    }

    public Optional<CustomerResponse> getCustomerById(Long id) {
        log.info("Getting customer by ID: {}", id);
        return customerRepository.findById(id).map(this::mapToCustomerResponse);
    }

    public Optional<CustomerWithTokenResponse> getCustomerWithTokenById(Long id) {
        log.info("Getting customer with token by ID: {}", id);
        return customerRepository.findById(id).map(customer -> {
            CustomerResponse response = mapToCustomerResponse(customer);
            String accessToken = generateToken(customer);
            CustomerWithTokenResponse tokenResponse = new CustomerWithTokenResponse();
            tokenResponse.setCustomer(response);
            tokenResponse.setAccessToken(accessToken);
            tokenResponse.setExpiresIn(jwtExpiration / 1000);
            return tokenResponse;
        });
    }

    public Optional<CustomerResponse> getCustomerByCustomerId(String customerId) {
        log.info("Getting customer by customerId: {}", customerId);
        return customerRepository.findByCustomerId(customerId).map(this::mapToCustomerResponse);
    }

    public CustomerPageResponse getCustomers(String name, Long restaurantId, String email,
                                           int page, int size) {
        log.info("Getting customers with filters - name: {}, restaurantId: {}, email: {}, page: {}, size: {}",
                name, restaurantId, email, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage;

        // Build query based on filters
        if (name != null && !name.trim().isEmpty()) {
            customerPage = customerRepository.findAll(pageable); // You might want to add custom query methods
        } else if (restaurantId != null) {
            customerPage = customerRepository.findAll(pageable); // You might want to add custom query methods
        } else if (email != null && !email.trim().isEmpty()) {
            customerPage = customerRepository.findAll(pageable); // You might want to add custom query methods
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        List<CustomerResponse> customerResponses = customerPage.getContent()
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());

        return new CustomerPageResponse(
                customerResponses,
                customerPage.getNumber(),
                customerPage.getTotalPages(),
                customerPage.getTotalElements()
        );
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with ID: {}", id);

        // Publish to platform-wide topic
        emitCustomerUpdate(getAllCustomers());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setAvatar(customer.getAvatar());
        response.setTotalOrders(customer.getTotalOrders());
        response.setTotalSpent(customer.getTotalSpent());
        response.setLoyaltyPoints(customer.getLoyaltyPoints());
        response.setRestaurantId(customer.getRestaurant() != null ? customer.getRestaurant().getId() : null);
        response.setRestaurantName(customer.getRestaurant() != null ? customer.getRestaurant().getName() : null);
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }

    private String generateCustomerId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String processAvatar(String avatarData, Long customerId) {
        // Check if it's already a file path (not base64)
        if (!avatarData.startsWith("data:")) {
            return avatarData; // It's already a file path
        }

        try {
            // Extract base64 data
            String[] parts = avatarData.split(",");
            if (parts.length != 2) {
                throw new BadRequestException("Invalid base64 data format");
            }

            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];

            // Determine file extension
            String extension = getFileExtensionFromMimeType(mimeType);

            // Create upload directory
            String uploadDir = "uploads/images/guest";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String filename = "customer_" + customerId + "_" + UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadPath.resolve(filename);

            // Decode and save file
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            Files.write(filePath, imageBytes);

            // Return file path
            return "/uploads/images/guest/" + filename;

        } catch (IOException e) {
            log.error("Failed to save avatar file for customer {}: {}", customerId, e.getMessage());
            throw new BadRequestException("Failed to process avatar image");
        }
    }

    private String getFileExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg"; // Default fallback
        }
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

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    private void emitCustomerUpdate(List<CustomerResponse> customers) {
        messagingTemplate.convertAndSend("/topic/customers", customers);
    }
}