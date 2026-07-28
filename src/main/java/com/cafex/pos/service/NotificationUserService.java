package com.cafex.pos.service;

import com.cafex.pos.dto.UserResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationUserService {

    private final UserRepository userRepository;

    public List<UserResponse> getUsersForNotifications(String restaurantId, List<String> roles) {
        log.info("Fetching users for notifications with restaurantId: {}, roles: {}", restaurantId, roles);

        Specification<User> platformOwnerSpec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("role"), User.UserRole.platform_owner));
            predicate = cb.and(predicate, cb.or(
                cb.isNull(root.get("restaurantId")),
                cb.equal(root.get("restaurantId"), "")
            ));
            predicate = cb.and(predicate, cb.equal(root.get("isActive"), User.ActiveStatus.Y));
            return predicate;
        };

        Specification<User> restaurantUserSpec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("restaurantId"), restaurantId));
            }
            predicate = cb.and(predicate, cb.notEqual(root.get("role"), User.UserRole.platform_owner));
            predicate = cb.and(predicate, cb.equal(root.get("isActive"), User.ActiveStatus.Y));

            if (roles != null && !roles.isEmpty()) {
                Predicate rolePredicate = cb.disjunction();
                for (String role : roles) {
                    try {
                        User.UserRole userRole = User.UserRole.valueOf(role);
                        rolePredicate = cb.or(rolePredicate, cb.equal(root.get("role"), userRole));
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid role value: {}", role);
                    }
                }
                predicate = cb.and(predicate, rolePredicate);
            }

            return predicate;
        };

        List<User> platformOwners = userRepository.findAll(platformOwnerSpec);
        List<User> restaurantUsers = userRepository.findAll(restaurantUserSpec);

        List<User> combinedUsers = Stream.concat(platformOwners.stream(), restaurantUsers.stream())
                .distinct()
                .collect(Collectors.toList());

        return combinedUsers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setUserType(user.getUserType());
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
