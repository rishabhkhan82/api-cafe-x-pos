package com.cafex.pos.controller;

import com.cafex.pos.dto.ReviewRequest;
import com.cafex.pos.dto.ReviewResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.ReviewPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("Save review request received for restaurantId: {}, customerId: {}", reviewRequest.getRestaurantId(), reviewRequest.getCustomerId());
        ReviewResponse response = reviewService.saveReview(reviewRequest);
        log.info("Review saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "REVIEW_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("Update review request received for ID: {}", id);
        ReviewResponse response = reviewService.updateReview(id, reviewRequest);
        log.info("Review updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "REVIEW_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<ReviewPageResponse> getReviews(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long parentReviewId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isVisible,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get reviews request received with filters - restaurantId: {}, customerId: {}, parentReviewId: {}, isActive: {}, isVisible: {}, page: {}, size: {}",
                restaurantId, customerId, parentReviewId, isActive, isVisible, page, size);
        ReviewPageResponse response = reviewService.getReviewsWithFilters(restaurantId, customerId, parentReviewId, isActive, isVisible, page, size);
        log.info("Retrieved {} reviews (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        log.info("Get review by ID request received for ID: {}", id);
        ReviewResponse response = reviewService.getReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        log.info("Review retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteReview(@PathVariable Long id) {
        log.info("Delete review request received for ID: {}", id);
        reviewService.deleteReview(id);
        log.info("Review deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "REVIEW_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
