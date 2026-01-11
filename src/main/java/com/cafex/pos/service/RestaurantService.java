package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantRequest;
import com.cafex.pos.dto.RestaurantResponse;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.cafex.pos.dto.RestaurantPageResponse;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantResponse> getAllRestaurants() {
        log.info("Fetching all restaurants");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RestaurantPageResponse getRestaurantsWithFilters(String name, String status, String ownerName, int page, int size) {
        log.info("Fetching restaurants with filters - name: {}, status: {}, ownerName: {}, page: {}, size: {}",
                name, status, ownerName, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<Restaurant> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter (search in name, restaurantId)
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("restaurantId")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            // Status filter
            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                try {
                    Restaurant.RestaurantStatus restaurantStatus = Restaurant.RestaurantStatus.valueOf(status.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), restaurantStatus));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", status);
                }
            }

            // Owner name filter
            if (ownerName != null && !ownerName.trim().isEmpty()) {
                String searchTerm = "%" + ownerName.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerName")), searchTerm));
            }

            return predicate;
        };

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);

        List<RestaurantResponse> content = restaurantPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RestaurantPageResponse(
            content,
            restaurantPage.getNumber() + 1, // currentPage (1-based)
            restaurantPage.getTotalPages(),
            restaurantPage.getTotalElements()
        );
    }

    public Optional<RestaurantResponse> getRestaurantById(Long id) {
        log.info("Fetching restaurant by ID: {}", id);
        return restaurantRepository.findById(id)
                .map(this::convertToResponse);
    }

    public RestaurantResponse saveRestaurant(RestaurantRequest restaurantRequest) {
        log.info("Saving new restaurant: {}", restaurantRequest.getName());

        // Check if email already exists
        if (restaurantRepository.existsByEmail(restaurantRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + restaurantRequest.getEmail());
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRequest.getName());
        restaurant.setDescription(restaurantRequest.getDescription());
        restaurant.setAddress(restaurantRequest.getAddress());
        restaurant.setPhone(restaurantRequest.getPhone());
        restaurant.setEmail(restaurantRequest.getEmail());
        restaurant.setGstNumber(restaurantRequest.getGstNumber());
        restaurant.setLicenseNumber(restaurantRequest.getLicenseNumber());
        restaurant.setOwnerName(restaurantRequest.getOwnerName());
        restaurant.setOwnerPhone(restaurantRequest.getOwnerPhone());
        restaurant.setOwnerEmail(restaurantRequest.getOwnerEmail());
        restaurant.setStatus(restaurantRequest.getStatus());
        restaurant.setIsActive(restaurantRequest.getIsActive());
        restaurant.setSubscriptionPlan(restaurantRequest.getSubscriptionPlan());
        restaurant.setSubscriptionStartDate(restaurantRequest.getSubscriptionStartDate());
        restaurant.setSubscriptionEndDate(restaurantRequest.getSubscriptionEndDate());
        restaurant.setLogoImage(restaurantRequest.getLogoImage());
        restaurant.setCreatedBy(restaurantRequest.getCreatedBy());
        restaurant.setUpdatedBy(restaurantRequest.getUpdatedBy());
        restaurant.setState(restaurantRequest.getState());
        restaurant.setCity(restaurantRequest.getCity());
        restaurant.setPincode(restaurantRequest.getPincode());
        restaurant.setLat(restaurantRequest.getLat());
        restaurant.setLng(restaurantRequest.getLng());
        restaurant.setCreatedAt(restaurantRequest.getCreatedAt() != null ? restaurantRequest.getCreatedAt() : LocalDateTime.now());
        restaurant.setUpdatedAt(restaurantRequest.getUpdatedAt() != null ? restaurantRequest.getUpdatedAt() : LocalDateTime.now());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant saved successfully with ID: {}", savedRestaurant.getId());

        return convertToResponse(savedRestaurant);
    }

    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest restaurantRequest) {
        log.info("Updating restaurant with ID: {}", id);

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + id));

        // Check email uniqueness if changed
        if (!existingRestaurant.getEmail().equals(restaurantRequest.getEmail()) &&
            restaurantRepository.existsByEmail(restaurantRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + restaurantRequest.getEmail());
        }

        // Update fields
        existingRestaurant.setName(restaurantRequest.getName());
        existingRestaurant.setDescription(restaurantRequest.getDescription());
        existingRestaurant.setAddress(restaurantRequest.getAddress());
        existingRestaurant.setPhone(restaurantRequest.getPhone());
        existingRestaurant.setEmail(restaurantRequest.getEmail());
        existingRestaurant.setGstNumber(restaurantRequest.getGstNumber());
        existingRestaurant.setLicenseNumber(restaurantRequest.getLicenseNumber());
        existingRestaurant.setOwnerName(restaurantRequest.getOwnerName());
        existingRestaurant.setOwnerPhone(restaurantRequest.getOwnerPhone());
        existingRestaurant.setOwnerEmail(restaurantRequest.getOwnerEmail());
        existingRestaurant.setStatus(restaurantRequest.getStatus());
        existingRestaurant.setIsActive(restaurantRequest.getIsActive());
        existingRestaurant.setSubscriptionPlan(restaurantRequest.getSubscriptionPlan());
        existingRestaurant.setSubscriptionStartDate(restaurantRequest.getSubscriptionStartDate());
        existingRestaurant.setSubscriptionEndDate(restaurantRequest.getSubscriptionEndDate());
        existingRestaurant.setLogoImage(restaurantRequest.getLogoImage());
        existingRestaurant.setUpdatedBy(restaurantRequest.getUpdatedBy());
        existingRestaurant.setState(restaurantRequest.getState());
        existingRestaurant.setCity(restaurantRequest.getCity());
        existingRestaurant.setPincode(restaurantRequest.getPincode());
        existingRestaurant.setLat(restaurantRequest.getLat());
        existingRestaurant.setLng(restaurantRequest.getLng());
        existingRestaurant.setUpdatedAt(LocalDateTime.now());

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        log.info("Restaurant updated successfully with ID: {}", updatedRestaurant.getId());

        return convertToResponse(updatedRestaurant);
    }

    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with ID: {}", id);

        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with ID: " + id);
        }

        restaurantRepository.deleteById(id);
        log.info("Restaurant deleted successfully with ID: {}", id);
    }

    public boolean existsByEmail(String email) {
        return restaurantRepository.existsByEmail(email);
    }

    private RestaurantResponse convertToResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setAddress(restaurant.getAddress());
        response.setPhone(restaurant.getPhone());
        response.setEmail(restaurant.getEmail());
        response.setGstNumber(restaurant.getGstNumber());
        response.setLicenseNumber(restaurant.getLicenseNumber());
        response.setOwnerName(restaurant.getOwnerName());
        response.setOwnerPhone(restaurant.getOwnerPhone());
        response.setOwnerEmail(restaurant.getOwnerEmail());
        response.setStatus(restaurant.getStatus());
        response.setIsActive(restaurant.getIsActive());
        response.setSubscriptionPlan(restaurant.getSubscriptionPlan());
        response.setSubscriptionStartDate(restaurant.getSubscriptionStartDate());
        response.setSubscriptionEndDate(restaurant.getSubscriptionEndDate());
        response.setLogoImage(restaurant.getLogoImage());
        response.setCreatedBy(restaurant.getCreatedBy());
        response.setUpdatedBy(restaurant.getUpdatedBy());
        response.setState(restaurant.getState());
        response.setCity(restaurant.getCity());
        response.setPincode(restaurant.getPincode());
        response.setLat(restaurant.getLat());
        response.setLng(restaurant.getLng());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setUpdatedAt(restaurant.getUpdatedAt());
        return response;
    }
}