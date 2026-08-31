package com.cafex.pos.service;

import com.cafex.pos.dto.MenuAddonRequest;
import com.cafex.pos.dto.MenuAddonResponse;
import com.cafex.pos.dto.MenuAddonPageResponse;
import com.cafex.pos.entity.MenuAddon;
import com.cafex.pos.repository.MenuAddonRepository;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
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
public class MenuAddonServiceImpl implements MenuAddonService {

    private final MenuAddonRepository menuAddonRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/restaurant/";

    @Override
    public MenuAddonResponse saveMenuAddon(MenuAddonRequest menuAddonRequest) {
        log.info("Saving new menu add-on: {}", menuAddonRequest.getName());

        MenuAddon menuAddon = new MenuAddon();
        menuAddon.setRestaurantId(menuAddonRequest.getRestaurantId());
        menuAddon.setName(menuAddonRequest.getName());
        menuAddon.setDescription(menuAddonRequest.getDescription());
        menuAddon.setPrice(menuAddonRequest.getPrice());
        menuAddon.setImage(null);
        menuAddon.setType(menuAddonRequest.getType());
        menuAddon.setIsActive(menuAddonRequest.getIsActive() != null ? menuAddonRequest.getIsActive() : true);
        menuAddon.setDisplayOrder(menuAddonRequest.getDisplayOrder() != null ? menuAddonRequest.getDisplayOrder() : 0);
        menuAddon.setCreatedAt(menuAddonRequest.getCreatedAt() != null ? menuAddonRequest.getCreatedAt().toLocalDateTime() : LocalDateTime.now());
        menuAddon.setUpdatedAt(menuAddonRequest.getUpdatedAt() != null ? menuAddonRequest.getUpdatedAt().toLocalDateTime() : LocalDateTime.now());
        menuAddon.setCreatedBy(menuAddonRequest.getCreatedBy());
        menuAddon.setUpdatedBy(menuAddonRequest.getUpdatedBy());

        MenuAddon savedMenuAddon = menuAddonRepository.save(menuAddon);

        if (menuAddonRequest.getImage() != null && !menuAddonRequest.getImage().isEmpty() && menuAddonRequest.getImage().startsWith("data:image/")) {
            try {
                String imageUrl = saveImageFromBase64(menuAddonRequest.getImage(), savedMenuAddon.getId());
                savedMenuAddon.setImage(imageUrl);
                savedMenuAddon = menuAddonRepository.save(savedMenuAddon);
            } catch (Exception e) {
                log.error("Failed to save image for menu add-on {}: {}", savedMenuAddon.getId(), e.getMessage());
            }
        }

        log.info("Menu add-on saved successfully with ID: {}", savedMenuAddon.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + savedMenuAddon.getRestaurantId() + "/menu-addons", savedMenuAddon);

        return convertToResponse(savedMenuAddon);
    }

    @Override
    public MenuAddonResponse updateMenuAddon(Long id, MenuAddonRequest menuAddonRequest) {
        log.info("Updating menu add-on with ID: {}", id);

        MenuAddon existingMenuAddon = menuAddonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu add-on not found with ID: " + id));

        Long restaurantId = existingMenuAddon.getRestaurantId();

        if (menuAddonRequest.getImage() != null && !menuAddonRequest.getImage().isEmpty() && menuAddonRequest.getImage().startsWith("data:image/")) {
            try {
                if (existingMenuAddon.getImage() != null && !existingMenuAddon.getImage().isEmpty() && !existingMenuAddon.getImage().startsWith("data:image/")) {
                    deleteImageFile(existingMenuAddon.getImage());
                }
                String imageUrl = saveImageFromBase64(menuAddonRequest.getImage(), existingMenuAddon.getId());
                existingMenuAddon.setImage(imageUrl);
            } catch (Exception e) {
                log.error("Failed to update image for menu add-on {}: {}", existingMenuAddon.getId(), e.getMessage());
            }
        }

        existingMenuAddon.setName(menuAddonRequest.getName());
        existingMenuAddon.setDescription(menuAddonRequest.getDescription());
        existingMenuAddon.setPrice(menuAddonRequest.getPrice());
        existingMenuAddon.setType(menuAddonRequest.getType());
        existingMenuAddon.setIsActive(menuAddonRequest.getIsActive());
        existingMenuAddon.setDisplayOrder(menuAddonRequest.getDisplayOrder() != null ? menuAddonRequest.getDisplayOrder() : 0);
        existingMenuAddon.setUpdatedAt(menuAddonRequest.getUpdatedAt() != null ? menuAddonRequest.getUpdatedAt().toLocalDateTime() : LocalDateTime.now());
        existingMenuAddon.setUpdatedBy(menuAddonRequest.getUpdatedBy());

        MenuAddon updatedMenuAddon = menuAddonRepository.save(existingMenuAddon);
        log.info("Menu add-on updated successfully with ID: {}", updatedMenuAddon.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/menu-addons", updatedMenuAddon);

        return convertToResponse(updatedMenuAddon);
    }

    @Override
    public MenuAddonPageResponse getMenuAddonsWithFilters(String name, String restaurantId, int page, int size) {
        log.info("Fetching menu add-ons with filters - name: {}, restaurantId: {}, page: {}, size: {}", name, restaurantId, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<MenuAddon> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), id));
                } catch (NumberFormatException e) {
                }
            }

            return predicate;
        };

        Page<MenuAddon> menuAddonPage = menuAddonRepository.findAll(spec, pageable);

        List<MenuAddonResponse> content = menuAddonPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuAddonPageResponse(
                content,
                menuAddonPage.getNumber() + 1,
                menuAddonPage.getTotalPages(),
                menuAddonPage.getTotalElements()
        );
    }

    @Override
    public Optional<MenuAddonResponse> getMenuAddonById(Long id) {
        log.info("Fetching menu add-on by ID: {}", id);
        return menuAddonRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteMenuAddon(Long id) {
        log.info("Deleting menu add-on with ID: {}", id);

        MenuAddon menuAddon = menuAddonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu add-on not found with ID: " + id));

        Long restaurantId = menuAddon.getRestaurantId();

        if (menuAddon.getImage() != null && !menuAddon.getImage().isEmpty()) {
            try {
                deleteImageFile(menuAddon.getImage());
            } catch (Exception e) {
                log.error("Failed to delete image for menu add-on {}: {}", id, e.getMessage());
            }
        }

        menuAddonRepository.deleteById(id);
        log.info("Menu add-on deleted successfully with ID: {}", id);

        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/menu-addons", menuAddon);
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

    private String saveImageFromBase64(String base64Data, Long menuAddonId) throws IOException {
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
        String filename = menuAddonId + "_addon_image." + extension;
        Path uploadDir = Paths.get("uploads", "images", "menu-addons");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, imageBytes);
        return "/uploads/images/menu-addons/" + filename;
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

    private MenuAddonResponse convertToResponse(MenuAddon menuAddon) {
        MenuAddonResponse response = new MenuAddonResponse();
        response.setId(menuAddon.getId());
        response.setRestaurantId(menuAddon.getRestaurantId());
        response.setName(menuAddon.getName());
        response.setDescription(menuAddon.getDescription());
        response.setPrice(menuAddon.getPrice());
        response.setImage(menuAddon.getImage());
        response.setType(menuAddon.getType());
        response.setIsActive(menuAddon.getIsActive());
        response.setDisplayOrder(menuAddon.getDisplayOrder());
        response.setCreatedAt(menuAddon.getCreatedAt());
        response.setUpdatedAt(menuAddon.getUpdatedAt());
        response.setCreatedBy(menuAddon.getCreatedBy());
        response.setUpdatedBy(menuAddon.getUpdatedBy());
        return response;
    }
}
