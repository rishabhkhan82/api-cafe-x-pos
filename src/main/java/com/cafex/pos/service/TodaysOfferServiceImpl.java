package com.cafex.pos.service;

import com.cafex.pos.dto.TodaysOfferRequest;
import com.cafex.pos.dto.TodaysOfferResponse;
import com.cafex.pos.dto.TodaysOfferPageResponse;
import com.cafex.pos.entity.TodaysOffer;
import com.cafex.pos.repository.TodaysOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
public class TodaysOfferServiceImpl implements TodaysOfferService {

    private final TodaysOfferRepository todaysOfferRepository;

    @Override
    public TodaysOfferResponse saveTodaysOffer(TodaysOfferRequest todaysOfferRequest) {
        log.info("Saving new today's offer for restaurantId: {}", todaysOfferRequest.getRestaurantId());

        TodaysOffer todaysOffer = new TodaysOffer();
        todaysOffer.setRestaurantId(todaysOfferRequest.getRestaurantId());
        todaysOffer.setTitle(todaysOfferRequest.getTitle());
        todaysOffer.setDisplayOrder(todaysOfferRequest.getDisplayOrder() != null ? todaysOfferRequest.getDisplayOrder() : 0);
        todaysOffer.setIsActive(todaysOfferRequest.getIsActive() != null ? todaysOfferRequest.getIsActive() : true);
        todaysOffer.setImageUrl(processImageUrl(todaysOfferRequest.getImageUrl()));
        todaysOffer.setCreatedAt(LocalDateTime.now());
        todaysOffer.setUpdatedAt(LocalDateTime.now());
        todaysOffer.setCreatedBy(todaysOfferRequest.getCreatedBy());
        todaysOffer.setUpdatedBy(todaysOfferRequest.getUpdatedBy());

        TodaysOffer savedTodaysOffer = todaysOfferRepository.save(todaysOffer);
        log.info("Today's offer saved successfully with ID: {}", savedTodaysOffer.getId());

        return convertToResponse(savedTodaysOffer);
    }

    @Override
    public TodaysOfferResponse updateTodaysOffer(Long id, TodaysOfferRequest todaysOfferRequest) {
        log.info("Updating today's offer with ID: {}", id);

        TodaysOffer existingTodaysOffer = todaysOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Today's offer not found with ID: " + id));

        existingTodaysOffer.setRestaurantId(todaysOfferRequest.getRestaurantId());
        existingTodaysOffer.setTitle(todaysOfferRequest.getTitle());
        existingTodaysOffer.setDisplayOrder(todaysOfferRequest.getDisplayOrder() != null ? todaysOfferRequest.getDisplayOrder() : 0);
        existingTodaysOffer.setIsActive(todaysOfferRequest.getIsActive() != null ? todaysOfferRequest.getIsActive() : true);
        existingTodaysOffer.setUpdatedAt(LocalDateTime.now());
        existingTodaysOffer.setUpdatedBy(todaysOfferRequest.getUpdatedBy());

        if (todaysOfferRequest.getImageUrl() != null && !todaysOfferRequest.getImageUrl().isEmpty()) {
            if (todaysOfferRequest.getImageUrl().startsWith("data:image/")) {
                try {
                    if (existingTodaysOffer.getImageUrl() != null && !existingTodaysOffer.getImageUrl().isEmpty()
                            && !existingTodaysOffer.getImageUrl().startsWith("data:image/")) {
                        deleteImageFile(existingTodaysOffer.getImageUrl());
                    }
                    existingTodaysOffer.setImageUrl(processImageUrl(todaysOfferRequest.getImageUrl()));
                } catch (Exception e) {
                    log.error("Failed to update image for today's offer {}: {}", existingTodaysOffer.getId(), e.getMessage());
                }
            } else {
                existingTodaysOffer.setImageUrl(todaysOfferRequest.getImageUrl());
            }
        }

        TodaysOffer updatedTodaysOffer = todaysOfferRepository.save(existingTodaysOffer);
        log.info("Today's offer updated successfully with ID: {}", updatedTodaysOffer.getId());

        return convertToResponse(updatedTodaysOffer);
    }

    @Override
    public TodaysOfferPageResponse getTodaysOffersWithFilters(Long restaurantId, Boolean isActive, int page, int size) {
        log.info("Fetching today's offers with filters - restaurantId: {}, isActive: {}, page: {}, size: {}",
                restaurantId, isActive, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<TodaysOffer> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (restaurantId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), restaurantId));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        Page<TodaysOffer> todaysOfferPage = todaysOfferRepository.findAll(spec, pageable);

        List<TodaysOfferResponse> content = todaysOfferPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new TodaysOfferPageResponse(
                content,
                todaysOfferPage.getNumber() + 1,
                todaysOfferPage.getTotalPages(),
                todaysOfferPage.getTotalElements()
        );
    }

    @Override
    public Optional<TodaysOfferResponse> getTodaysOfferById(Long id) {
        log.info("Fetching today's offer by ID: {}", id);
        return todaysOfferRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteTodaysOffer(Long id) {
        log.info("Deleting today's offer with ID: {}", id);

        TodaysOffer todaysOffer = todaysOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Today's offer not found with ID: " + id));

        if (todaysOffer.getImageUrl() != null && !todaysOffer.getImageUrl().isEmpty()) {
            try {
                deleteImageFile(todaysOffer.getImageUrl());
            } catch (Exception e) {
                log.error("Failed to delete image for today's offer {}: {}", id, e.getMessage());
            }
        }

        todaysOfferRepository.deleteById(id);
        log.info("Today's offer deleted successfully with ID: {}", id);
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
                log.error("Failed to process today's offer image: {}", e.getMessage());
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
            log.info("Deleted today's offer image file: {}", filePath);
        }
    }

    private TodaysOfferResponse convertToResponse(TodaysOffer todaysOffer) {
        TodaysOfferResponse response = new TodaysOfferResponse();
        response.setId(todaysOffer.getId());
        response.setRestaurantId(todaysOffer.getRestaurantId());
        response.setTitle(todaysOffer.getTitle());
        response.setImageUrl(todaysOffer.getImageUrl());
        response.setDisplayOrder(todaysOffer.getDisplayOrder());
        response.setIsActive(todaysOffer.getIsActive());
        response.setCreatedAt(todaysOffer.getCreatedAt());
        response.setUpdatedAt(todaysOffer.getUpdatedAt());
        response.setCreatedBy(todaysOffer.getCreatedBy());
        response.setUpdatedBy(todaysOffer.getUpdatedBy());
        return response;
    }
}
