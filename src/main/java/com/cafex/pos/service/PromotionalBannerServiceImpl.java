package com.cafex.pos.service;

import com.cafex.pos.dto.PromotionalBannerRequest;
import com.cafex.pos.dto.PromotionalBannerResponse;
import com.cafex.pos.dto.PromotionalBannerPageResponse;
import com.cafex.pos.entity.PromotionalBanner;
import com.cafex.pos.repository.PromotionalBannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.exception.BadRequestException;
import jakarta.persistence.criteria.Predicate;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionalBannerServiceImpl implements PromotionalBannerService {

    private final PromotionalBannerRepository promotionalBannerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TOPIC_PREFIX = "/topic/restaurant/";

    @Override
    public PromotionalBannerResponse savePromotionalBanner(PromotionalBannerRequest bannerRequest) {
        log.info("Saving new promotional banner for restaurantId: {}", bannerRequest.getRestaurantId());

        long activeBannerCount = promotionalBannerRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (bannerRequest.getRestaurantId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), bannerRequest.getRestaurantId()));
            }
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), true));
            return predicate;
        }).stream().count();

        if (activeBannerCount >= 3) {
            throw new com.cafex.pos.exception.BadRequestException("Maximum of 3 active promotional banners allowed per restaurant");
        }

        PromotionalBanner banner = new PromotionalBanner();
        banner.setRestaurantId(bannerRequest.getRestaurantId());
        banner.setTitle(bannerRequest.getTitle());
        banner.setDisplayOrder(bannerRequest.getDisplayOrder() != null ? bannerRequest.getDisplayOrder() : 0);
        banner.setIsActive(bannerRequest.getIsActive() != null ? bannerRequest.getIsActive() : true);
        banner.setImageUrl(processImageUrl(bannerRequest.getImageUrl()));
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
        banner.setCreatedBy(bannerRequest.getCreatedBy());
        banner.setUpdatedBy(bannerRequest.getUpdatedBy());

        PromotionalBanner savedBanner = promotionalBannerRepository.save(banner);
        log.info("Promotional banner saved successfully with ID: {}", savedBanner.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + savedBanner.getRestaurantId() + "/promotional-banners", (Object) convertToResponse(savedBanner));

        return convertToResponse(savedBanner);
    }

    @Override
    public PromotionalBannerResponse updatePromotionalBanner(Long id, PromotionalBannerRequest bannerRequest) {
        log.info("Updating promotional banner with ID: {}", id);

        PromotionalBanner existingBanner = promotionalBannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotional banner not found with ID: " + id));

        existingBanner.setRestaurantId(bannerRequest.getRestaurantId());
        existingBanner.setTitle(bannerRequest.getTitle());
        existingBanner.setDisplayOrder(bannerRequest.getDisplayOrder() != null ? bannerRequest.getDisplayOrder() : 0);

        Boolean newIsActive = bannerRequest.getIsActive() != null ? bannerRequest.getIsActive() : true;
        if (Boolean.TRUE.equals(newIsActive) && !Boolean.TRUE.equals(existingBanner.getIsActive())) {
            long activeBannerCount = promotionalBannerRepository.findAll((root, query, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();
                if (bannerRequest.getRestaurantId() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), bannerRequest.getRestaurantId()));
                }
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), true));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get("id"), id));
                return predicate;
            }).stream().count();

            if (activeBannerCount >= 3) {
                throw new com.cafex.pos.exception.BadRequestException("Maximum of 3 active promotional banners allowed per restaurant");
            }
        }

        existingBanner.setIsActive(newIsActive);
        existingBanner.setUpdatedAt(LocalDateTime.now());
        existingBanner.setUpdatedBy(bannerRequest.getUpdatedBy());

        if (bannerRequest.getImageUrl() != null && !bannerRequest.getImageUrl().isEmpty()) {
            if (bannerRequest.getImageUrl().startsWith("data:image/")) {
                try {
                    if (existingBanner.getImageUrl() != null && !existingBanner.getImageUrl().isEmpty()
                            && !existingBanner.getImageUrl().startsWith("data:image/")) {
                        deleteImageFile(existingBanner.getImageUrl());
                    }
                    existingBanner.setImageUrl(processImageUrl(bannerRequest.getImageUrl()));
                } catch (Exception e) {
                    log.error("Failed to update image for promotional banner {}: {}", existingBanner.getId(), e.getMessage());
                }
            } else {
                existingBanner.setImageUrl(bannerRequest.getImageUrl());
            }
        }

        PromotionalBanner updatedBanner = promotionalBannerRepository.save(existingBanner);
        log.info("Promotional banner updated successfully with ID: {}", updatedBanner.getId());

        messagingTemplate.convertAndSend(TOPIC_PREFIX + updatedBanner.getRestaurantId() + "/promotional-banners", (Object) convertToResponse(updatedBanner));

        return convertToResponse(updatedBanner);
    }

    @Override
    public PromotionalBannerPageResponse getPromotionalBannersWithFilters(Long restaurantId, String title, Boolean isActive, int page, int size) {
        log.info("Fetching promotional banners with filters - restaurantId: {}, title: {}, isActive: {}, page: {}, size: {}",
                restaurantId, title, isActive, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<PromotionalBanner> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (restaurantId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), restaurantId));
            }

            if (title != null && !title.trim().isEmpty()) {
                String searchTerm = "%" + title.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        Page<PromotionalBanner> bannerPage = promotionalBannerRepository.findAll(spec, pageable);

        List<PromotionalBannerResponse> content = bannerPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PromotionalBannerPageResponse(
                content,
                bannerPage.getNumber() + 1,
                bannerPage.getTotalPages(),
                bannerPage.getTotalElements()
        );
    }

    @Override
    public Optional<PromotionalBannerResponse> getPromotionalBannerById(Long id) {
        log.info("Fetching promotional banner by ID: {}", id);
        return promotionalBannerRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deletePromotionalBanner(Long id) {
        log.info("Deleting promotional banner with ID: {}", id);

        PromotionalBanner banner = promotionalBannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotional banner not found with ID: " + id));

        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            try {
                deleteImageFile(banner.getImageUrl());
            } catch (Exception e) {
                log.error("Failed to delete image for promotional banner {}: {}", id, e.getMessage());
            }
        }

        Long restaurantId = banner.getRestaurantId();

        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/promotional-banners", (Object) banner);

        promotionalBannerRepository.deleteById(id);
        log.info("Promotional banner deleted successfully with ID: {}", id);
    }

    private String processImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }

        if (imageUrl.startsWith("data:image/")) {
            try {
                String[] parts = imageUrl.split(",");
                if (parts.length != 2) {
                    return imageUrl;
                }

                String base64Data = parts[1];
                String mimeType = parts[0].split(":")[1].split(";")[0];
                String extension = mimeType.split("/")[1];

                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                String filename = UUID.randomUUID().toString() + "." + extension;
                Path uploadPath = Paths.get("uploads", "images", "promotional-banners", filename);

                Files.createDirectories(uploadPath.getParent());

                Files.write(uploadPath, imageBytes);

                return "/uploads/images/promotional-banners/" + filename;
            } catch (IOException e) {
                log.error("Failed to process promotional banner image: {}", e.getMessage());
                return imageUrl;
            }
        }

        return imageUrl;
    }

    private void deleteImageFile(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        String filePath = imageUrl.replace("/uploads/", "uploads/");
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Files.delete(path);
            log.info("Deleted promotional banner image file: {}", filePath);
        }
    }

    private PromotionalBannerResponse convertToResponse(PromotionalBanner banner) {
        PromotionalBannerResponse response = new PromotionalBannerResponse();
        response.setId(banner.getId());
        response.setRestaurantId(banner.getRestaurantId());
        response.setTitle(banner.getTitle());
        response.setImageUrl(banner.getImageUrl());
        response.setDisplayOrder(banner.getDisplayOrder());
        response.setIsActive(banner.getIsActive());
        response.setCreatedAt(banner.getCreatedAt());
        response.setUpdatedAt(banner.getUpdatedAt());
        response.setCreatedBy(banner.getCreatedBy());
        response.setUpdatedBy(banner.getUpdatedBy());
        return response;
    }
}
