package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemRequest;
import com.cafex.pos.dto.MenuItemResponse;
import com.cafex.pos.dto.MenuItemPageResponse;
import com.cafex.pos.entity.MenuItem;
import com.cafex.pos.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/restaurant/";

    @Override
    public MenuItemResponse saveMenuItem(MenuItemRequest menuItemRequest) {
        log.info("Saving new menu item: {}", menuItemRequest.getItemId());

        // Check if itemId already exists
        if (menuItemRepository.existsByItemId(menuItemRequest.getItemId())) {
            throw new ConflictException("Item ID already exists: " + menuItemRequest.getItemId());
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(menuItemRequest.getItemId());
        menuItem.setName(menuItemRequest.getName());
        menuItem.setDescription(menuItemRequest.getDescription());
        menuItem.setPrice(menuItemRequest.getPrice());
        menuItem.setOriginalPrice(menuItemRequest.getOriginalPrice());
        menuItem.setCategory(menuItemRequest.getCategory());
        menuItem.setImage(null); // Ensure image is null initially
        menuItem.setIsAvailable(menuItemRequest.getIsAvailable() != null ? menuItemRequest.getIsAvailable() : true);
        menuItem.setIsActive(menuItemRequest.getIsActive() != null ? menuItemRequest.getIsActive() : true);
        menuItem.setIsVegetarian(menuItemRequest.getIsVegetarian() != null ? menuItemRequest.getIsVegetarian() : false);
        menuItem.setIsVeg(menuItemRequest.getIsVeg());
        menuItem.setIsSpicy(menuItemRequest.getIsSpicy() != null ? menuItemRequest.getIsSpicy() : false);
        menuItem.setIsPopular(menuItemRequest.getIsPopular() != null ? menuItemRequest.getIsPopular() : false);
        menuItem.setIsFeatured(menuItemRequest.getIsFeatured() != null ? menuItemRequest.getIsFeatured() : false);
        menuItem.setIsRecommended(menuItemRequest.getIsRecommended() != null ? menuItemRequest.getIsRecommended() : false);
        menuItem.setPreparationTime(menuItemRequest.getPreparationTime());
        menuItem.setDiscount(menuItemRequest.getDiscount());
        menuItem.setType(menuItemRequest.getType() != null ? menuItemRequest.getType() : "RAW");
        menuItem.setRecipeId(menuItemRequest.getRecipeId());
        menuItem.setRestaurantId(menuItemRequest.getRestaurantId());
        menuItem.setCreatedAt(menuItemRequest.getCreatedAt() != null ? menuItemRequest.getCreatedAt().toLocalDateTime() : LocalDateTime.now());
        menuItem.setUpdatedAt(menuItemRequest.getUpdatedAt() != null ? menuItemRequest.getUpdatedAt().toLocalDateTime() : LocalDateTime.now());
        menuItem.setCreatedBy(menuItemRequest.getCreatedBy());
        menuItem.setUpdatedBy(menuItemRequest.getUpdatedBy());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        // Handle image upload if provided as base64
        if (menuItemRequest.getImage() != null && !menuItemRequest.getImage().isEmpty() && menuItemRequest.getImage().startsWith("data:image/")) {
            try {
                String imageUrl = saveImageFromBase64(menuItemRequest.getImage(), savedMenuItem.getId());
                savedMenuItem.setImage(imageUrl);
                savedMenuItem = menuItemRepository.save(savedMenuItem);
            } catch (Exception e) {
                log.error("Failed to save image for menu item {}: {}", savedMenuItem.getId(), e.getMessage());
                // Continue without image, don't fail the save
            }
        }

        log.info("Menu item saved successfully with ID: {}", savedMenuItem.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + savedMenuItem.getRestaurantId() + "/menu-items", convertToResponse(savedMenuItem));

        return convertToResponse(savedMenuItem);
    }

    @Override
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest menuItemRequest) {
        log.info("Updating menu item with ID: {}", id);

        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

        // Check itemId uniqueness if changed
        if (!existingMenuItem.getItemId().equals(menuItemRequest.getItemId()) &&
            menuItemRepository.existsByItemId(menuItemRequest.getItemId())) {
            throw new ConflictException("Item ID already exists: " + menuItemRequest.getItemId());
        }

        // Handle image change if provided as base64
        if (menuItemRequest.getImage() != null && !menuItemRequest.getImage().isEmpty() && menuItemRequest.getImage().startsWith("data:image/")) {
            try {
                // Delete old image if exists
                if (existingMenuItem.getImage() != null && !existingMenuItem.getImage().isEmpty() && !existingMenuItem.getImage().startsWith("data:image/")) {
                    deleteImageFile(existingMenuItem.getImage());
                }
                // Save new image
                String imageUrl = saveImageFromBase64(menuItemRequest.getImage(), existingMenuItem.getId());
                existingMenuItem.setImage(imageUrl);
            } catch (Exception e) {
                log.error("Failed to update image for menu item {}: {}", existingMenuItem.getId(), e.getMessage());
                // Continue without image change
            }
        }

        // Update fields
        existingMenuItem.setItemId(menuItemRequest.getItemId());
        existingMenuItem.setName(menuItemRequest.getName());
        existingMenuItem.setDescription(menuItemRequest.getDescription());
        existingMenuItem.setPrice(menuItemRequest.getPrice());
        existingMenuItem.setOriginalPrice(menuItemRequest.getOriginalPrice());
        existingMenuItem.setCategory(menuItemRequest.getCategory());
        existingMenuItem.setIsAvailable(menuItemRequest.getIsAvailable());
        existingMenuItem.setIsActive(menuItemRequest.getIsActive());
        existingMenuItem.setIsVegetarian(menuItemRequest.getIsVegetarian());
        existingMenuItem.setIsVeg(menuItemRequest.getIsVeg());
        existingMenuItem.setIsSpicy(menuItemRequest.getIsSpicy());
        existingMenuItem.setIsPopular(menuItemRequest.getIsPopular());
        existingMenuItem.setIsFeatured(menuItemRequest.getIsFeatured());
        existingMenuItem.setIsRecommended(menuItemRequest.getIsRecommended());
        existingMenuItem.setPreparationTime(menuItemRequest.getPreparationTime());
        existingMenuItem.setDiscount(menuItemRequest.getDiscount());
        existingMenuItem.setType(menuItemRequest.getType());
        existingMenuItem.setRecipeId(menuItemRequest.getRecipeId());
        existingMenuItem.setRestaurantId(menuItemRequest.getRestaurantId());
        existingMenuItem.setUpdatedAt(menuItemRequest.getUpdatedAt() != null ? menuItemRequest.getUpdatedAt().toLocalDateTime() : LocalDateTime.now());
        existingMenuItem.setUpdatedBy(menuItemRequest.getUpdatedBy());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        log.info("Menu item updated successfully with ID: {}", updatedMenuItem.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + updatedMenuItem.getRestaurantId() + "/menu-items", convertToResponse(updatedMenuItem));

        return convertToResponse(updatedMenuItem);
    }

    @Override
    public MenuItemPageResponse getMenuItemsWithFilters(String name, String category, String restaurantId, String isAvailable, String isActive, String isVegetarian, String isSpicy, String isPopular, String isFeatured, String isRecommended, int page, int size) {
        log.info("Fetching menu items with filters - name: {}, category: {}, restaurantId: {}, isAvailable: {}, isActive: {}, isVegetarian: {}, isSpicy: {}, isFeatured: {}, isPopular: {}, isRecommended: {}, page: {}, size: {}",
                name, category, restaurantId, isAvailable, isActive, isVegetarian, isSpicy, isPopular, isFeatured, isRecommended, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<MenuItem> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            // Category filter
            if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category"), category));
            }

            // Restaurant filter
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), id));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            // Is Available filter
            if (isAvailable != null && !isAvailable.trim().isEmpty() && !"all".equals(isAvailable)) {
                Boolean available = "true".equals(isAvailable);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isAvailable"), available));
            }

            // Is Active filter
            if (isActive != null && !isActive.trim().isEmpty() && !"all".equals(isActive)) {
                Boolean active = "true".equals(isActive);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), active));
            }

            // Is Vegetarian filter
            if (isVegetarian != null && !isVegetarian.trim().isEmpty() && !"all".equals(isVegetarian)) {
                Boolean vegetarian = "true".equals(isVegetarian);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isVegetarian"), vegetarian));
            }

            // Is Spicy filter
            if (isSpicy != null && !isSpicy.trim().isEmpty() && !"all".equals(isSpicy)) {
                Boolean spicy = "true".equals(isSpicy) || "1".equals(isSpicy);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isSpicy"), spicy));
            }

            // Is Popular filter
            if (isPopular != null && !isPopular.trim().isEmpty() && !"all".equals(isPopular)) {
                Boolean popular = "true".equals(isPopular) || "1".equals(isPopular);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isPopular"), popular));
            }

            // Is Featured filter
            if (isFeatured != null && !isFeatured.trim().isEmpty() && !"all".equals(isFeatured)) {
                Boolean featured = "true".equals(isFeatured) || "1".equals(isFeatured);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isFeatured"), featured));
            }

            // Is Recommended filter
            if (isRecommended != null && !isRecommended.trim().isEmpty() && !"all".equals(isRecommended)) {
                Boolean recommended = "true".equals(isRecommended) || "1".equals(isRecommended);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isRecommended"), recommended));
            }

            return predicate;
        };

        Page<MenuItem> menuItemPage = menuItemRepository.findAll(spec, pageable);

        List<MenuItemResponse> content = menuItemPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuItemPageResponse(
            content,
            menuItemPage.getNumber() + 1, // currentPage (1-based)
            menuItemPage.getTotalPages(),
            menuItemPage.getTotalElements()
        );
    }

    @Override
    public Optional<MenuItemResponse> getMenuItemById(Long id) {
        log.info("Fetching menu item by ID: {}", id);
        return menuItemRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteMenuItem(Long id) {
        log.info("Deleting menu item with ID: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

        // Delete associated image if exists
        if (menuItem.getImage() != null && !menuItem.getImage().isEmpty()) {
            try {
                deleteImageFile(menuItem.getImage());
            } catch (Exception e) {
                log.error("Failed to delete image for menu item {}: {}", id, e.getMessage());
                // Continue with deletion
            }
        }

        menuItemRepository.deleteById(id);
        log.info("Menu item deleted successfully with ID: {}", id);

        messagingTemplate.convertAndSend(TOPIC_PREFIX + menuItem.getRestaurantId() + "/menu-items", Map.of("id", id, "deleted", true));
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

    private String saveImageFromBase64(String base64Data, Long menuItemId) throws IOException {
        String base64Image = base64Data;
        String mimeType = "image/jpeg";
        if (base64Data.contains(",")) {
            String[] parts = base64Data.split(",");
            if (parts.length == 2) {
                String header = parts[0];
                if (header.startsWith("data:") && header.contains(";base64")) {
                    mimeType = header.substring(5, header.indexOf(";base64"));
                }
                base64Image = parts[1];
            }
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String extension = getExtensionFromMimeType(mimeType);
        String filename = menuItemId + "_image." + extension;
        Path uploadDir = Paths.get("uploads", "images", "menu");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, imageBytes);
        return "/uploads/images/menu/" + filename;
    }

    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    }

    private MenuItemResponse convertToResponse(MenuItem menuItem) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setItemId(menuItem.getItemId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setPrice(menuItem.getPrice());
        response.setOriginalPrice(menuItem.getOriginalPrice());
        response.setCategory(menuItem.getCategory());
        response.setImage(menuItem.getImage());
        response.setIsAvailable(menuItem.getIsAvailable());
        response.setIsActive(menuItem.getIsActive());
        response.setIsVegetarian(menuItem.getIsVegetarian());
        response.setIsVeg(menuItem.getIsVeg());
        response.setIsSpicy(menuItem.getIsSpicy());
        response.setIsPopular(menuItem.getIsPopular());
        response.setIsFeatured(menuItem.getIsFeatured());
        response.setIsRecommended(menuItem.getIsRecommended());
        response.setPreparationTime(menuItem.getPreparationTime());
        response.setDiscount(menuItem.getDiscount());
        response.setType(menuItem.getType());
        response.setRestaurantId(menuItem.getRestaurantId());
        response.setCreatedAt(menuItem.getCreatedAt());
        response.setUpdatedAt(menuItem.getUpdatedAt());
        response.setCreatedBy(menuItem.getCreatedBy());
        response.setUpdatedBy(menuItem.getUpdatedBy());
        return response;
    }
}
