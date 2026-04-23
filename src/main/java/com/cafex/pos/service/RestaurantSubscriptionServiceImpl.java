package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantSubscriptionRequest;
import com.cafex.pos.dto.RestaurantSubscriptionResponse;
import com.cafex.pos.dto.RestaurantSubscriptionPageResponse;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.entity.RestaurantSubscriptions;
import com.cafex.pos.entity.SubscriptionPlans;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.repository.RestaurantSubscriptionRepository;
import com.cafex.pos.repository.SubscriptionPlansRepository;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantSubscriptionServiceImpl implements RestaurantSubscriptionService {

    private final RestaurantSubscriptionRepository restaurantSubscriptionRepository;
    private final RestaurantRepository restaurantRepository;
    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final UserRepository userRepository;

    @Override
    public RestaurantSubscriptionResponse saveRestaurantSubscription(RestaurantSubscriptionRequest request) {
        log.info("Saving new restaurant subscription: {}", request.getSubscriptionId());

        // Check if subscriptionId already exists
        if (restaurantSubscriptionRepository.findAll().stream().anyMatch(rs -> request.getSubscriptionId().equals(rs.getSubscriptionId()))) {
            throw new RuntimeException("Subscription ID already exists: " + request.getSubscriptionId());
        }

        RestaurantSubscriptions entity = convertToEntity(request);
        entity.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        RestaurantSubscriptions saved = restaurantSubscriptionRepository.save(entity);
        log.info("Restaurant subscription saved successfully with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public RestaurantSubscriptionResponse updateRestaurantSubscription(Long id, RestaurantSubscriptionRequest request) {
        log.info("Updating restaurant subscription with ID: {}", id);

        RestaurantSubscriptions existing = restaurantSubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant subscription not found with ID: " + id));

        // Update fields
        existing.setSubscriptionId(request.getSubscriptionId());
        existing.setStatus(request.getStatus());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setTrialEndDate(request.getTrialEndDate());
        existing.setNextBillingDate(request.getNextBillingDate());
        existing.setBillingCycle(request.getBillingCycle());
        existing.setCurrentPeriodStart(request.getCurrentPeriodStart());
        existing.setCurrentPeriodEnd(request.getCurrentPeriodEnd());
        existing.setCancelAtPeriodEnd(request.getCancelAtPeriodEnd());
        existing.setCancelledAt(request.getCancelledAt());
        existing.setCancellationReason(request.getCancellationReason());
        existing.setPaymentMethodId(request.getPaymentMethodId());
        existing.setAutoRenew(request.getAutoRenew());
        existing.setDiscountCode(request.getDiscountCode());
        existing.setDiscountAmount(request.getDiscountAmount());
        existing.setFinalAmount(request.getFinalAmount());
        existing.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        // Update relations if provided
        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + request.getRestaurantId()));
            existing.setRestaurant(restaurant);
        }
        if (request.getPlanId() != null) {
            SubscriptionPlans plan = subscriptionPlansRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + request.getPlanId()));
            existing.setPlan(plan);
        }
        if (request.getCreatedBy() != null) {
            User user = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getCreatedBy()));
            existing.setCreatedBy(user);
        }

        RestaurantSubscriptions updated = restaurantSubscriptionRepository.save(existing);
        log.info("Restaurant subscription updated successfully with ID: {}", updated.getId());

        return convertToResponse(updated);
    }

    @Override
    public RestaurantSubscriptionPageResponse getRestaurantSubscriptionsWithFilters(String subscriptionId, String restaurantId, String planId, String status, String autoRenew, String cancelAtPeriodEnd, int page, int size) {
        log.info("Fetching restaurant subscriptions with filters");

        Pageable pageable = PageRequest.of(Math.max(0, page), size);

        Specification<RestaurantSubscriptions> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (subscriptionId != null && !subscriptionId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("subscriptionId"), "%" + subscriptionId + "%"));
            }
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), id));
                } catch (NumberFormatException e) {}
            }
            if (planId != null && !planId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(planId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("plan").get("id"), id));
                } catch (NumberFormatException e) {}
            }
            if (status != null && !status.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (autoRenew != null && !autoRenew.trim().isEmpty()) {
                Boolean renew = "true".equals(autoRenew);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("autoRenew"), renew));
            }
            if (cancelAtPeriodEnd != null && !cancelAtPeriodEnd.trim().isEmpty()) {
                Boolean cancel = "true".equals(cancelAtPeriodEnd);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cancelAtPeriodEnd"), cancel));
            }

            return predicate;
        };

        Page<RestaurantSubscriptions> pageResult = restaurantSubscriptionRepository.findAll(spec, pageable);

        List<RestaurantSubscriptionResponse> content = pageResult.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RestaurantSubscriptionPageResponse(content, pageResult.getNumber() + 1, pageResult.getTotalPages(), pageResult.getTotalElements());
    }

    @Override
    public Optional<RestaurantSubscriptionResponse> getRestaurantSubscriptionById(Long id) {
        log.info("Fetching restaurant subscription by ID: {}", id);
        return restaurantSubscriptionRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public void deleteRestaurantSubscription(Long id) {
        log.info("Deleting restaurant subscription with ID: {}", id);
        restaurantSubscriptionRepository.deleteById(id);
        log.info("Restaurant subscription deleted successfully with ID: {}", id);
    }

    private RestaurantSubscriptions convertToEntity(RestaurantSubscriptionRequest request) {
        RestaurantSubscriptions entity = new RestaurantSubscriptions();

        entity.setSubscriptionId(request.getSubscriptionId());
        entity.setStatus(request.getStatus());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setTrialEndDate(request.getTrialEndDate());
        entity.setNextBillingDate(request.getNextBillingDate());
        entity.setBillingCycle(request.getBillingCycle());
        entity.setCurrentPeriodStart(request.getCurrentPeriodStart());
        entity.setCurrentPeriodEnd(request.getCurrentPeriodEnd());
        entity.setCancelAtPeriodEnd(request.getCancelAtPeriodEnd());
        entity.setCancelledAt(request.getCancelledAt());
        entity.setCancellationReason(request.getCancellationReason());
        entity.setPaymentMethodId(request.getPaymentMethodId());
        entity.setAutoRenew(request.getAutoRenew());
        entity.setDiscountCode(request.getDiscountCode());
        entity.setDiscountAmount(request.getDiscountAmount());
        entity.setFinalAmount(request.getFinalAmount());

        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            entity.setRestaurant(restaurant);
        }
        if (request.getPlanId() != null) {
            SubscriptionPlans plan = subscriptionPlansRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
            entity.setPlan(plan);
        }
        if (request.getCreatedBy() != null) {
            User user = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setCreatedBy(user);
        }

        return entity;
    }

    private RestaurantSubscriptionResponse convertToResponse(RestaurantSubscriptions entity) {
        RestaurantSubscriptionResponse response = new RestaurantSubscriptionResponse();
        response.setId(entity.getId());
        response.setSubscriptionId(entity.getSubscriptionId());
        response.setRestaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null);
        response.setPlanId(entity.getPlan() != null ? entity.getPlan().getId() : null);
        response.setStatus(entity.getStatus());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setTrialEndDate(entity.getTrialEndDate());
        response.setNextBillingDate(entity.getNextBillingDate());
        response.setBillingCycle(entity.getBillingCycle());
        response.setCurrentPeriodStart(entity.getCurrentPeriodStart());
        response.setCurrentPeriodEnd(entity.getCurrentPeriodEnd());
        response.setCancelAtPeriodEnd(entity.getCancelAtPeriodEnd());
        response.setCancelledAt(entity.getCancelledAt());
        response.setCancellationReason(entity.getCancellationReason());
        response.setPaymentMethodId(entity.getPaymentMethodId());
        response.setAutoRenew(entity.getAutoRenew());
        response.setDiscountCode(entity.getDiscountCode());
        response.setDiscountAmount(entity.getDiscountAmount());
        response.setFinalAmount(entity.getFinalAmount());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null);
        return response;
    }
}