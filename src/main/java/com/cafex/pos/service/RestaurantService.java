package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantRequest;
import com.cafex.pos.dto.RestaurantResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.RestaurantPageResponse;
import com.cafex.pos.dto.RestaurantSubscriptionDetailsRequest;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.cafex.pos.dto.RestaurantPageResponse;
import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final OwnerDashboardService ownerDashboardService;

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

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("restaurantId")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                try {
                    Restaurant.RestaurantStatus restaurantStatus = Restaurant.RestaurantStatus.valueOf(status.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), restaurantStatus));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", status);
                }
            }

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
                restaurantPage.getNumber() + 1,
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

        if (restaurantRepository.existsByEmail(restaurantRequest.getEmail())) {
            throw new ConflictException("Email already exists: " + restaurantRequest.getEmail());
        }

        String logoImageBase64 = restaurantRequest.getLogoImage();
        String bannerImageBase64 = restaurantRequest.getBannerImage();

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

        try {
            if (logoImageBase64 != null && !logoImageBase64.isEmpty() && logoImageBase64.startsWith("data:image/")) {
                String imageUrl = saveImageFromBase64(logoImageBase64, savedRestaurant.getId(), "logo");
                savedRestaurant.setLogoImage(imageUrl);
                savedRestaurant = restaurantRepository.save(savedRestaurant);
            }

            if (bannerImageBase64 != null && !bannerImageBase64.isEmpty() && bannerImageBase64.startsWith("data:image/")) {
                String imageUrl = saveImageFromBase64(bannerImageBase64, savedRestaurant.getId(), "banner");
                savedRestaurant.setBannerImage(imageUrl);
                savedRestaurant = restaurantRepository.save(savedRestaurant);
            }
        } catch (Exception e) {
            log.error("Failed to save images for restaurant {}: {}", savedRestaurant.getId(), e.getMessage());
        }

        RestaurantResponse response = convertToResponse(savedRestaurant);

        try {
            java.util.Map<String, Object> emailVariables = new java.util.HashMap<>();
            emailVariables.put("restaurant_name", response.getName());
            emailVariables.put("restaurant_id", response.getId());
            emailVariables.put("address", response.getAddress());
            emailVariables.put("owner_name", response.getOwnerName());
            emailVariables.put("owner_email", response.getOwnerEmail());

            emailService.sendHtmlEmail(
                response.getOwnerEmail(),
                "Restaurant Created Successfully",
                "restaurant_created",
                emailVariables
            );
            log.info("Restaurant created email sent to: {}", response.getOwnerEmail());
        } catch (Exception e) {
            log.error("Failed to send restaurant created email to: {} for restaurant ID: {}. Error: {}", response.getOwnerEmail(), response.getId(), e.getMessage());
        }

        emitRestaurantUpdate(response);
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));
        ownerDashboardService.emitUpdate(savedRestaurant.getId());

        return response;
    }

    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest restaurantRequest) {
        log.info("Updating restaurant with ID: {}", id);

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));

        if (!existingRestaurant.getEmail().equals(restaurantRequest.getEmail()) &&
                restaurantRepository.existsByEmail(restaurantRequest.getEmail())) {
            throw new ConflictException("Email already exists: " + restaurantRequest.getEmail());
        }

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
        existingRestaurant.setUpdatedBy(restaurantRequest.getUpdatedBy());
        existingRestaurant.setState(restaurantRequest.getState());
        existingRestaurant.setCity(restaurantRequest.getCity());
        existingRestaurant.setPincode(restaurantRequest.getPincode());
        existingRestaurant.setLat(restaurantRequest.getLat());
        existingRestaurant.setLng(restaurantRequest.getLng());
        existingRestaurant.setUpdatedAt(LocalDateTime.now());

        String logoImagePayload = restaurantRequest.getLogoImage();
        String bannerImagePayload = restaurantRequest.getBannerImage();

        String oldLogoImage = existingRestaurant.getLogoImage();
        String oldBannerImage = existingRestaurant.getBannerImage();

        if (logoImagePayload != null) {
            if (logoImagePayload.startsWith("data:image/")) {
                existingRestaurant.setLogoImage(logoImagePayload);
            } else if (logoImagePayload.isEmpty()) {
                existingRestaurant.setLogoImage(null);
            }
            // else path string means keep existing — do nothing
        }

        if (bannerImagePayload != null) {
            if (bannerImagePayload.startsWith("data:image/")) {
                existingRestaurant.setBannerImage(bannerImagePayload);
            } else if (bannerImagePayload.isEmpty()) {
                existingRestaurant.setBannerImage(null);
            }
        }

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        log.info("Restaurant updated successfully with ID: {}", updatedRestaurant.getId());

        try {
            // Handle logo upload or deletion
            String currentLogoImage = updatedRestaurant.getLogoImage();
            if (currentLogoImage != null && currentLogoImage.startsWith("data:image/")) {
                if (oldLogoImage != null && !oldLogoImage.isEmpty() && !oldLogoImage.startsWith("data:image/")) {
                    deleteImageFile(oldLogoImage);
                }
                String imageUrl = saveImageFromBase64(currentLogoImage, updatedRestaurant.getId(), "logo");
                updatedRestaurant.setLogoImage(imageUrl);
                updatedRestaurant = restaurantRepository.save(updatedRestaurant);
            } else if (currentLogoImage == null || currentLogoImage.isEmpty()) {
                if (oldLogoImage != null && !oldLogoImage.isEmpty() && !oldLogoImage.startsWith("data:image/")) {
                    deleteImageFile(oldLogoImage);
                }
            }

            // Handle banner upload or deletion
            String currentBannerImage = updatedRestaurant.getBannerImage();
            if (currentBannerImage != null && currentBannerImage.startsWith("data:image/")) {
                if (oldBannerImage != null && !oldBannerImage.isEmpty() && !oldBannerImage.startsWith("data:image/")) {
                    deleteImageFile(oldBannerImage);
                }
                String imageUrl = saveImageFromBase64(currentBannerImage, updatedRestaurant.getId(), "banner");
                updatedRestaurant.setBannerImage(imageUrl);
                updatedRestaurant = restaurantRepository.save(updatedRestaurant);
            } else if (currentBannerImage == null || currentBannerImage.isEmpty()) {
                if (oldBannerImage != null && !oldBannerImage.isEmpty() && !oldBannerImage.startsWith("data:image/")) {
                    deleteImageFile(oldBannerImage);
                }
            }
        } catch (Exception e) {
            log.error("Failed to save images for restaurant {}: {}", updatedRestaurant.getId(), e.getMessage());
        }

        RestaurantResponse response = convertToResponse(updatedRestaurant);

        emitRestaurantUpdate(response);
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));
        ownerDashboardService.emitUpdate(updatedRestaurant.getId());

        return response;
    }

    public void updateRestaurantSubscriptionDetails(Long id, RestaurantSubscriptionDetailsRequest request) {
        log.info("Updating restaurant subscription details for ID: {}", id);

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));

        existingRestaurant.setSubscriptionPlan(request.getSubscriptionPlan());
        existingRestaurant.setSubscriptionStartDate(request.getSubscriptionStartDate());
        existingRestaurant.setSubscriptionEndDate(request.getSubscriptionEndDate());
        existingRestaurant.setUpdatedAt(LocalDateTime.now());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            existingRestaurant.setUpdatedBy(userOpt.map(User::getId).orElse(null));
        }

        restaurantRepository.save(existingRestaurant);
        log.info("Restaurant subscription details updated successfully for ID: {}", id);
    }

    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with ID: {}", id);

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));

        // Delete associated images if exist
        try {
            if (existingRestaurant.getLogoImage() != null && !existingRestaurant.getLogoImage().isEmpty()
                    && !existingRestaurant.getLogoImage().startsWith("data:image/")) {
                deleteImageFile(existingRestaurant.getLogoImage());
            }

            if (existingRestaurant.getBannerImage() != null && !existingRestaurant.getBannerImage().isEmpty()
                    && !existingRestaurant.getBannerImage().startsWith("data:image/")) {
                deleteImageFile(existingRestaurant.getBannerImage());
            }
        } catch (Exception e) {
            log.error("Failed to delete images for restaurant {}: {}", id, e.getMessage());
        }

        restaurantRepository.delete(existingRestaurant);
        log.info("Restaurant deleted successfully with ID: {}", id);

        emitRestaurantUpdate(null);
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));
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
        response.setBannerImage(restaurant.getBannerImage());
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

    private void emitRestaurantUpdate(RestaurantResponse restaurant) {
        try {
            messagingTemplate.convertAndSend("/topic/restaurants", getAllRestaurants());
            if (restaurant != null && restaurant.getId() != null) {
                messagingTemplate.convertAndSend("/topic/restaurant/" + restaurant.getId(), restaurant);
            }
        } catch (Exception e) {
            log.error("Failed to broadcast restaurant update: {}", e.getMessage());
        }
    }

    private String saveImageFromBase64(String base64Data, Long restaurantId, String suffix) throws IOException {
        String base64Image = base64Data.split(",")[1];
        String mimeType = base64Data.split(":")[1].split(";")[0];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String extension = getExtensionFromMimeType(mimeType);
        String filename = restaurantId + "_" + suffix + "." + extension;
        Path uploadDir = Paths.get("uploads", "images", "restaurant");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, imageBytes);
        return "/uploads/images/restaurant/" + filename;
    }

    private void deleteImageFile(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        String filePath = imageUrl.replace("/uploads/", "uploads/");
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("Deleted image file: {}", filePath);
        }
    }

    private String getExtensionFromMimeType(String mimeType) {
        return switch (mimeType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }
}
