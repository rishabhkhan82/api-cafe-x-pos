package com.cafex.pos.service;

import com.cafex.pos.dto.ReviewRequest;
import com.cafex.pos.dto.ReviewResponse;
import com.cafex.pos.dto.ReviewPageResponse;

import java.util.Optional;

public interface ReviewService {
    ReviewResponse saveReview(ReviewRequest reviewRequest);
    ReviewResponse updateReview(Long id, ReviewRequest reviewRequest);
    ReviewPageResponse getReviewsWithFilters(Long restaurantId, Long customerId, Long parentReviewId, Boolean isActive, Boolean isVisible, int page, int size);
    Optional<ReviewResponse> getReviewById(Long id);
    void deleteReview(Long id);
}
