package com.cafex.pos.service;

import com.cafex.pos.dto.ReviewRequest;
import com.cafex.pos.dto.ReviewResponse;
import com.cafex.pos.dto.ReviewPageResponse;
import com.cafex.pos.dto.NotificationRequest;
import com.cafex.pos.entity.Review;
import com.cafex.pos.repository.ReviewRepository;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.UserRepository;
import com.cafex.pos.service.NotificationUserService;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.User;
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
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.service.NotificationsService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationsService notificationsService;
    private final NotificationUserService notificationUserService;
    private static final String TOPIC_PREFIX = "/topic/restaurant/";
    private static final String USER_TOPIC_PREFIX = "/topic/users/";

    @Override
    public ReviewResponse saveReview(ReviewRequest reviewRequest) {
        log.info("Saving new review for restaurantId: {}, customerId: {}", reviewRequest.getRestaurantId(), reviewRequest.getCustomerId());

        if (reviewRequest.getRating() == null || reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        if (reviewRequest.getParentReviewId() == null) {
            Specification<Review> existingReviewSpec = (root, query, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), reviewRequest.getRestaurantId()));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customerId"), reviewRequest.getCustomerId()));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("parentReviewId")));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), true));
                return predicate;
            };

            long existingReviewCount = reviewRepository.findAll(existingReviewSpec).stream().count();
            if (existingReviewCount > 0) {
                throw new ConflictException("You have already submitted a review for this restaurant. You can update your existing review instead.");
            }
        }

        String userType = reviewRequest.getUserType();
        if (userType == null || userType.isEmpty()) {
            userType = "customer";
        }

        if ("customer".equalsIgnoreCase(userType)) {
            Optional<Customer> customerOpt = customerRepository.findById(reviewRequest.getCustomerId());
            if (customerOpt.isEmpty()) {
                throw new ResourceNotFoundException("Customer not found with ID: " + reviewRequest.getCustomerId());
            }
        } else if ("admin".equalsIgnoreCase(userType)) {
            Optional<User> userOpt = userRepository.findById(reviewRequest.getCustomerId());
            if (userOpt.isEmpty()) {
                throw new ResourceNotFoundException("Admin user not found with ID: " + reviewRequest.getCustomerId());
            }
        } else {
            throw new BadRequestException("Invalid user_type. Must be 'customer' or 'admin'");
        }

        Review review = new Review();
        review.setRestaurantId(reviewRequest.getRestaurantId());
        review.setCustomerId(reviewRequest.getCustomerId());
        review.setRating(reviewRequest.getRating());
        review.setReviewText(reviewRequest.getReviewText());
        review.setParentReviewId(reviewRequest.getParentReviewId());
        review.setIsActive(reviewRequest.getIsActive() != null ? reviewRequest.getIsActive() : true);
        review.setIsVisible(reviewRequest.getIsVisible() != null ? reviewRequest.getIsVisible() : true);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setCreatedBy(reviewRequest.getCreatedBy());
        review.setUpdatedBy(reviewRequest.getUpdatedBy());

        Review savedReview = reviewRepository.save(review);
        log.info("Review saved successfully with ID: {}", savedReview.getId());

        ReviewResponse response = convertToResponse(savedReview);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + savedReview.getRestaurantId() + "/reviews", (Object) response);
        messagingTemplate.convertAndSend(USER_TOPIC_PREFIX + savedReview.getCustomerId() + "/reviews", (Object) response);

        if (savedReview.getParentReviewId() == null) {
            this.notifyRestaurantAdmins(savedReview);
        } else {
            this.notifyParentReviewAuthor(savedReview);
        }

        return response;
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest) {
        log.info("Updating review with ID: {}", id);

        if (reviewRequest.getRating() == null || reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        String userType = reviewRequest.getUserType();
        if (userType == null || userType.isEmpty()) {
            userType = "customer";
        }

        if ("customer".equalsIgnoreCase(userType)) {
            Optional<Customer> customerOpt = customerRepository.findById(reviewRequest.getCustomerId());
            if (customerOpt.isEmpty()) {
                throw new ResourceNotFoundException("Customer not found with ID: " + reviewRequest.getCustomerId());
            }
        } else if ("admin".equalsIgnoreCase(userType)) {
            Optional<User> userOpt = userRepository.findById(reviewRequest.getCustomerId());
            if (userOpt.isEmpty()) {
                throw new ResourceNotFoundException("Admin user not found with ID: " + reviewRequest.getCustomerId());
            }
        } else {
            throw new BadRequestException("Invalid user_type. Must be 'customer' or 'admin'");
        }

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        existingReview.setRating(reviewRequest.getRating());
        existingReview.setReviewText(reviewRequest.getReviewText());
        existingReview.setParentReviewId(reviewRequest.getParentReviewId());
        existingReview.setIsActive(reviewRequest.getIsActive() != null ? reviewRequest.getIsActive() : true);
        existingReview.setIsVisible(reviewRequest.getIsVisible() != null ? reviewRequest.getIsVisible() : true);
        existingReview.setUpdatedAt(LocalDateTime.now());
        existingReview.setUpdatedBy(reviewRequest.getUpdatedBy());

        Review updatedReview = reviewRepository.save(existingReview);
        log.info("Review updated successfully with ID: {}", updatedReview.getId());

        ReviewResponse response = convertToResponse(updatedReview);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + updatedReview.getRestaurantId() + "/reviews", (Object) response);
        messagingTemplate.convertAndSend(USER_TOPIC_PREFIX + updatedReview.getCustomerId() + "/reviews", (Object) response);

        return response;
    }

    @Override
    public ReviewPageResponse getReviewsWithFilters(Long restaurantId, Long customerId, Long parentReviewId, Boolean isActive, Boolean isVisible, int page, int size) {
        log.info("Fetching reviews with filters - restaurantId: {}, customerId: {}, parentReviewId: {}, isActive: {}, isVisible: {}, page: {}, size: {}",
                restaurantId, customerId, parentReviewId, isActive, isVisible, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<Review> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (restaurantId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), restaurantId));
            }

            if (customerId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customerId"), customerId));
            }

            if (parentReviewId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("parentReviewId"), parentReviewId));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            if (isVisible != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isVisible"), isVisible));
            }

            return predicate;
        };

        Page<Review> reviewPage = reviewRepository.findAll(spec, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new ReviewPageResponse(
                content,
                reviewPage.getNumber() + 1,
                reviewPage.getTotalPages(),
                reviewPage.getTotalElements()
        );
    }

    @Override
    public Optional<ReviewResponse> getReviewById(Long id) {
        log.info("Fetching review by ID: {}", id);
        return reviewRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteReview(Long id) {
        log.info("Deleting review with ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        Long restaurantId = review.getRestaurantId();
        Long customerId = review.getCustomerId();

        ReviewResponse response = convertToResponse(review);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/reviews", (Object) response);
        messagingTemplate.convertAndSend(USER_TOPIC_PREFIX + customerId + "/reviews", (Object) response);

        reviewRepository.deleteById(id);
        log.info("Review deleted successfully with ID: {}", id);
    }

    private void notifyRestaurantAdmins(Review review) {
        try {
            List<String> adminRoles = List.of("restaurant_owner", "restaurant_manager", "kitchen_manager");
            var admins = notificationUserService.getUsersForNotifications(
                String.valueOf(review.getRestaurantId()),
                adminRoles
            );

            for (var admin : admins) {
                NotificationRequest request = new NotificationRequest();
                request.setNotificationId(UUID.randomUUID().toString());
                request.setRecipientId(String.valueOf(admin.getId()));
                request.setRecipientRole(admin.getRole().name());
                request.setRestaurantId(String.valueOf(review.getRestaurantId()));
                request.setType("review");
                request.setTitle("New Review Received");
                request.setMessage("A new review has been submitted for your restaurant.");
                request.setIcon("fas fa-star");
                request.setPriority("medium");
                request.setStatus("unread");
                request.setRelatedEntityType("review");
                request.setRelatedEntityId(String.valueOf(review.getId()));

                notificationsService.createNotification(request);
            }
        } catch (Exception e) {
            log.error("Failed to send review notification to restaurant admins: {}", e.getMessage());
        }
    }

    private void notifyParentReviewAuthor(Review reply) {
        try {
            Review parentReview = reviewRepository.findById(reply.getParentReviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent review not found with ID: " + reply.getParentReviewId()));

            Long customerId = parentReview.getCustomerId();

            NotificationRequest request = new NotificationRequest();
            request.setNotificationId(UUID.randomUUID().toString());
            request.setRecipientId(String.valueOf(customerId));
            request.setRecipientRole("customer");
            request.setRestaurantId(String.valueOf(reply.getRestaurantId()));
            request.setType("review");
            request.setTitle("Admin Replied to Your Review");
            request.setMessage("The restaurant has replied to your review.");
            request.setIcon("fas fa-reply");
            request.setPriority("medium");
            request.setStatus("unread");
            request.setRelatedEntityType("review");
            request.setRelatedEntityId(String.valueOf(reply.getId()));

            notificationsService.createNotification(request);
        } catch (Exception e) {
            log.error("Failed to send reply notification to review author: {}", e.getMessage());
        }
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRestaurantId(review.getRestaurantId());
        response.setCustomerId(review.getCustomerId());
        response.setRating(review.getRating());
        response.setReviewText(review.getReviewText());
        response.setParentReviewId(review.getParentReviewId());
        response.setIsActive(review.getIsActive());
        response.setIsVisible(review.getIsVisible());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        response.setCreatedBy(review.getCreatedBy());
        response.setUpdatedBy(review.getUpdatedBy());
        return response;
    }
}
