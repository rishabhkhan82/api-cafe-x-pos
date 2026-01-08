package com.cafex.pos.service;

import com.cafex.pos.dto.UserRequest;
import com.cafex.pos.dto.UserResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.cafex.pos.dto.UserPageResponse;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserPageResponse getUsersWithFilters(String name, String restaurantId, String role, String status, int page, int size) {
        log.info("Fetching users with filters - name: {}, restaurantId: {}, role: {}, status: {}, page: {}, size: {}",
                name, restaurantId, role, status, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<User> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter (search in name, username, email)
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            // Restaurant filter
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                if ("none".equals(restaurantId)) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("restaurantId")));
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), restaurantId));
                }
            }

            // Role filter
            if (role != null && !role.trim().isEmpty() && !"all".equals(role)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("role"), role));
            }

            // Status filter
            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                if ("active".equals(status)) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), "Y"));
                } else if ("inactive".equals(status)) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), "N"));
                }
            }

            return predicate;
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new UserPageResponse(
            content,
            userPage.getNumber() + 1, // currentPage (1-based)
            userPage.getTotalPages(),
            userPage.getTotalElements()
        );
    }

    public Optional<UserResponse> getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertToResponse);
    }

    private String processAvatar(String avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return avatar;
        }

        // Check if it's a base64 image
        if (avatar.startsWith("data:image/")) {
            try {
                // Extract base64 data
                String[] parts = avatar.split(",");
                if (parts.length != 2) {
                    return avatar; // Invalid format, return as is
                }

                String base64Data = parts[1];
                String mimeType = parts[0].split(":")[1].split(";")[0];
                String extension = mimeType.split("/")[1];

                // Decode base64
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                // Create unique filename
                String filename = UUID.randomUUID().toString() + "." + extension;
                Path uploadPath = Paths.get("uploads", "images", filename);

                // Ensure directory exists
                Files.createDirectories(uploadPath.getParent());

                // Save file
                Files.write(uploadPath, imageBytes);

                // Return URL
                return "/uploads/images/" + filename;
            } catch (IOException e) {
                log.error("Failed to process avatar image: {}", e.getMessage());
                return avatar; // Return original on error
            }
        }

        // If it's already a URL or not base64, return as is
        return avatar;
    }

    public UserResponse saveUser(UserRequest userRequest) {
        log.info("Saving new user: {}", userRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + userRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + userRequest.getEmail());
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRole(userRequest.getRole());
        user.setAvatar(processAvatar(userRequest.getAvatar()));
        user.setRestaurantId(userRequest.getRestaurantId());
        user.setIsActive(userRequest.getIsActive());
        user.setCreatedAt(userRequest.getCreatedAt() != null ? userRequest.getCreatedAt() : LocalDateTime.now());
        user.setUpdatedAt(userRequest.getUpdatedAt() != null ? userRequest.getUpdatedAt() : LocalDateTime.now());
        user.setMemberSince(userRequest.getMemberSince() != null ? userRequest.getMemberSince() : user.getCreatedAt());
        user.setLastLogin(userRequest.getLastLogin());
        user.setCreatedBy(userRequest.getCreatedBy());

        User savedUser = userRepository.save(user);
        log.info("User saved successfully with ID: {}", savedUser.getId());

        return convertToResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Check username uniqueness if changed
        if (!existingUser.getUsername().equals(userRequest.getUsername()) &&
            userRepository.existsByUsername(userRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + userRequest.getUsername());
        }

        // Check email uniqueness if changed
        if (!existingUser.getEmail().equals(userRequest.getEmail()) &&
            userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + userRequest.getEmail());
        }

        // Update fields
        existingUser.setUsername(userRequest.getUsername());

        // Only update password if provided and not empty
        if (userRequest.getPassword() != null && !userRequest.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(userRequest.getPhone());
        existingUser.setRole(userRequest.getRole());
        existingUser.setAvatar(processAvatar(userRequest.getAvatar()));
        existingUser.setRestaurantId(userRequest.getRestaurantId());
        existingUser.setIsActive(userRequest.getIsActive());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return convertToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setRestaurantId(user.getRestaurantId());
        response.setMemberSince(user.getMemberSince());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setCreatedBy(user.getCreatedBy());
        response.setIsActive(user.getIsActive());
        response.setLastLogin(user.getLastLogin());
        return response;
    }
}