package com.cafex.pos.controller;

import com.cafex.pos.dto.PromotionalBannerRequest;
import com.cafex.pos.dto.PromotionalBannerResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.PromotionalBannerPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.PromotionalBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotional-banners")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PromotionalBannerController {

    private final PromotionalBannerService promotionalBannerService;

    @PostMapping
    public ResponseEntity<OperationResponse> savePromotionalBanner(@Valid @RequestBody PromotionalBannerRequest bannerRequest) {
        log.info("Save promotional banner request received for restaurantId: {}", bannerRequest.getRestaurantId());
        PromotionalBannerResponse response = promotionalBannerService.savePromotionalBanner(bannerRequest);
        log.info("Promotional banner saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "PROMOTIONAL_BANNER_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updatePromotionalBanner(@PathVariable Long id, @Valid @RequestBody PromotionalBannerRequest bannerRequest) {
        log.info("Update promotional banner request received for ID: {}", id);
        PromotionalBannerResponse response = promotionalBannerService.updatePromotionalBanner(id, bannerRequest);
        log.info("Promotional banner updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "PROMOTIONAL_BANNER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<PromotionalBannerPageResponse> getPromotionalBanners(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get promotional banners request received with filters - restaurantId: {}, title: {}, isActive: {}, page: {}, size: {}",
                restaurantId, title, isActive, page, size);
        PromotionalBannerPageResponse response = promotionalBannerService.getPromotionalBannersWithFilters(restaurantId, title, isActive, page, size);
        log.info("Retrieved {} promotional banners (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionalBannerResponse> getPromotionalBannerById(@PathVariable Long id) {
        log.info("Get promotional banner by ID request received for ID: {}", id);
        PromotionalBannerResponse response = promotionalBannerService.getPromotionalBannerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotional banner not found"));
        log.info("Promotional banner retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deletePromotionalBanner(@PathVariable Long id) {
        log.info("Delete promotional banner request received for ID: {}", id);
        promotionalBannerService.deletePromotionalBanner(id);
        log.info("Promotional banner deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "PROMOTIONAL_BANNER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
