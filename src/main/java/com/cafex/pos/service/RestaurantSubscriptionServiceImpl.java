package com.cafex.pos.service;

import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.dto.RestaurantSubscriptionPageResponse;
import com.cafex.pos.dto.RestaurantSubscriptionRequest;
import com.cafex.pos.dto.RestaurantSubscriptionResponse;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public RestaurantSubscriptionResponse saveRestaurantSubscription(RestaurantSubscriptionRequest request) {
        log.info("Saving new restaurant subscription: {}", request.getSubscriptionId());

        if (restaurantSubscriptionRepository.findAll().stream().anyMatch(rs -> request.getSubscriptionId().equals(rs.getSubscriptionId()))) {
            throw new ConflictException("Subscription ID already exists: " + request.getSubscriptionId());
        }

        RestaurantSubscriptions entity = convertToEntity(request);

        if (request.getPlanPriceAtSubscription() != null
                && request.getPlanPriceAtSubscription().compareTo(java.math.BigDecimal.ZERO) > 0
                && request.getEndDate() == null) {
            throw new BadRequestException("end_date is required for paid subscriptions");
        }
        entity.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        RestaurantSubscriptions saved = restaurantSubscriptionRepository.save(entity);
        log.info("Restaurant subscription saved successfully with ID: {}", saved.getId());

        RestaurantSubscriptionResponse response = convertToResponse(saved);

        // Publish to platform-wide topic
        emitSubscriptionUpdate(getAllSubscriptions());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));

        try {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId()).orElse(null);
            SubscriptionPlans plan = subscriptionPlansRepository.findById(request.getPlanId()).orElse(null);
            log.info("Subscription activated restaurant lookup: restaurant={}, plan={}, ownerEmail={}",
                restaurant != null ? restaurant.getId() : null,
                plan != null ? plan.getId() : null,
                restaurant != null ? restaurant.getOwnerEmail() : null);

            if (restaurant != null && plan != null && restaurant.getOwnerEmail() != null) {
                java.util.Map<String, Object> emailVariables = new java.util.HashMap<>();
                emailVariables.put("plan", plan.getDisplayName());
                emailVariables.put("restaurant_name", restaurant.getName());
                emailVariables.put("price", saved.getFinalAmount());
                emailVariables.put("subscription_id", saved.getSubscriptionId());

                try {
                    emailService.sendHtmlEmail(
                        restaurant.getOwnerEmail(),
                        "Subscription Activated",
                        "subscription_activated",
                        emailVariables
                    );
                    log.info("Subscription activated email sent to restaurant owner: {}", restaurant.getOwnerEmail());
                } catch (Exception ex) {
                    log.error("Failed to send subscription activated email to restaurant owner: {}. Error: {}", restaurant.getOwnerEmail(), ex.getMessage(), ex);
                }
            } else {
                log.warn("Skipping restaurant owner subscription email: restaurant={}, plan={}, ownerEmail={}",
                    restaurant != null, plan != null, restaurant != null ? restaurant.getOwnerEmail() : null);
            }

            List<User> platformOwners = userRepository.findAll((root, query, cb) -> {
                Specification<User> spec = Specification.where(null);
                spec = spec.and((r, q, c) -> c.equal(r.get("role"), User.UserRole.platform_owner));
                spec = spec.and((r, q, c) -> c.or(c.isNull(r.get("restaurantId")), c.equal(r.get("restaurantId"), "")));
                spec = spec.and((r, q, c) -> c.equal(r.get("isActive"), User.ActiveStatus.Y));
                return spec.toPredicate(root, query, cb);
            });

            for (User owner : platformOwners) {
                if (owner.getEmail() != null && !owner.getEmail().isEmpty()) {
                    java.util.Map<String, Object> emailVariables = new java.util.HashMap<>();
                    emailVariables.put("plan", plan != null ? plan.getDisplayName() : "");
                    emailVariables.put("restaurant_name", restaurant != null ? restaurant.getName() : "");
                    emailVariables.put("price", saved.getFinalAmount());
                    emailVariables.put("subscription_id", saved.getSubscriptionId());

                    emailService.sendHtmlEmail(
                        owner.getEmail(),
                        "New Subscription Activated",
                        "subscription_activated_platform",
                        emailVariables
                    );
                    log.info("Subscription activated platform email sent to: {}", owner.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send subscription activated emails for subscription ID: {}. Error: {}", saved.getSubscriptionId(), e.getMessage(), e);
        }

        return response;
    }

    @Override
    public RestaurantSubscriptionResponse createTrialSubscription(Long restaurantId, Long planId, Long userId) {
        log.info("Creating trial subscription for restaurant ID: {}, plan ID: {}", restaurantId, planId);

        // Check if restaurant has already used trial
        Optional<RestaurantSubscriptions> existingTrial = restaurantSubscriptionRepository
                .findByRestaurantIdAndIsTrialUsed(restaurantId, true);
        if (existingTrial.isPresent()) {
            throw new ConflictException("Restaurant has already used trial period");
        }

        // Get plan details for trial days
        SubscriptionPlans plan = subscriptionPlansRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));

        // Check if plan has trial days configured
        if (plan.getTrialDays() == null || plan.getTrialDays() <= 0) {
            throw new BadRequestException("Plan does not support trial period");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime trialEnd = now.plusDays(plan.getTrialDays());

        RestaurantSubscriptions trialSubscription = new RestaurantSubscriptions();
        trialSubscription.setSubscriptionId("trial_" + restaurantId + "_" + now.toString().replace(":", "").replace("-", "").substring(0, 15));
        trialSubscription.setRestaurant(restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId)));
        trialSubscription.setPlan(plan);
        trialSubscription.setStatus("trial");
        trialSubscription.setStartDate(now);
        trialSubscription.setEndDate(trialEnd);
        trialSubscription.setTrialStartDate(now);
        trialSubscription.setTrialEndDate(trialEnd);
        trialSubscription.setIsTrialUsed(true); // Mark trial as used
        trialSubscription.setBillingCycle(plan.getBillingCycle());
        trialSubscription.setCancelAtPeriodEnd(false);
        trialSubscription.setAutoRenew(false); // Trial subscriptions don't auto-renew
        trialSubscription.setDiscountAmount(java.math.BigDecimal.ZERO);
        trialSubscription.setFinalAmount(java.math.BigDecimal.ZERO);
        trialSubscription.setPlanPriceAtSubscription(plan.getPrice());
        trialSubscription.setOfferNameAtSubscription(plan.getOfferName());
        trialSubscription.setOfferDiscountPercentageAtSubscription(plan.getOfferDiscountPercentage() != null ? plan.getOfferDiscountPercentage() : 0);
        trialSubscription.setPlanNameAtSubscription(plan.getName());
        trialSubscription.setCreatedBy(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId)));
        trialSubscription.setCreatedAt(now);
        trialSubscription.setUpdatedAt(now);

        RestaurantSubscriptions saved = restaurantSubscriptionRepository.save(trialSubscription);
        log.info("Trial subscription created successfully with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public boolean hasRestaurantUsedTrial(Long restaurantId) {
        return restaurantSubscriptionRepository.findByRestaurantIdAndIsTrialUsed(restaurantId, true).isPresent();
    }

    @Override
    public List<RestaurantSubscriptionResponse> getActiveSubscriptions(Long restaurantId) {
        List<RestaurantSubscriptions> subscriptions = restaurantSubscriptionRepository
                .findByRestaurantIdAndStatusIn(restaurantId, List.of("trial", "active"));
        return subscriptions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public RestaurantSubscriptionResponse updateRestaurantSubscription(Long id, RestaurantSubscriptionRequest request) {
        log.info("Updating restaurant subscription with ID: {}", id);

        RestaurantSubscriptions existing = restaurantSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant subscription not found with ID: " + id));

        if (request.getPlanPriceAtSubscription() != null
                && request.getPlanPriceAtSubscription().compareTo(java.math.BigDecimal.ZERO) > 0
                && request.getEndDate() == null) {
            throw new BadRequestException("end_date is required for paid subscriptions");
        }

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
        existing.setGstAmount(request.getGstAmount());
        existing.setGstPercentage(request.getGstPercentage());
        existing.setPlanPriceAtSubscription(request.getPlanPriceAtSubscription());
        existing.setOfferNameAtSubscription(request.getOfferNameAtSubscription());
        existing.setOfferDiscountPercentageAtSubscription(request.getOfferDiscountPercentageAtSubscription());
        existing.setPlanNameAtSubscription(request.getPlanNameAtSubscription());
        existing.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        // Update relations if provided
        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + request.getRestaurantId()));
            existing.setRestaurant(restaurant);
        }
        if (request.getPlanId() != null) {
            SubscriptionPlans plan = subscriptionPlansRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + request.getPlanId()));
            existing.setPlan(plan);
        }
        if (request.getCreatedBy() != null) {
            User user = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getCreatedBy()));
            existing.setCreatedBy(user);
        }

        RestaurantSubscriptions updated = restaurantSubscriptionRepository.save(existing);
        log.info("Restaurant subscription updated successfully with ID: {}", updated.getId());

        // Publish to platform-wide topic
        emitSubscriptionUpdate(getAllSubscriptions());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));

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

        // Publish to platform-wide topic
        emitSubscriptionUpdate(getAllSubscriptions());
        eventPublisher.publishEvent(new com.cafex.pos.event.DashboardRefreshEvent(this));
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
        entity.setGstAmount(request.getGstAmount());
        entity.setGstPercentage(request.getGstPercentage());
        entity.setPlanPriceAtSubscription(request.getPlanPriceAtSubscription());
        entity.setOfferNameAtSubscription(request.getOfferNameAtSubscription());
        entity.setOfferDiscountPercentageAtSubscription(request.getOfferDiscountPercentageAtSubscription());
        entity.setPlanNameAtSubscription(request.getPlanNameAtSubscription());

        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
            entity.setRestaurant(restaurant);
        }
        if (request.getPlanId() != null) {
            SubscriptionPlans plan = subscriptionPlansRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
            entity.setPlan(plan);
        }
        if (request.getCreatedBy() != null) {
            User user = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
        response.setTrialStartDate(entity.getTrialStartDate());
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
        response.setGstAmount(entity.getGstAmount());
        response.setGstPercentage(entity.getGstPercentage());
        response.setPlanPriceAtSubscription(entity.getPlanPriceAtSubscription());
        response.setOfferNameAtSubscription(entity.getOfferNameAtSubscription());
        response.setOfferDiscountPercentageAtSubscription(entity.getOfferDiscountPercentageAtSubscription());
        response.setPlanNameAtSubscription(entity.getPlanNameAtSubscription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null);
        return response;
    }

    public List<RestaurantSubscriptionResponse> getAllSubscriptions() {
        return restaurantSubscriptionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void emitSubscriptionUpdate(List<RestaurantSubscriptionResponse> subscriptions) {
        messagingTemplate.convertAndSend("/topic/restaurant_subscriptions", subscriptions);
    }
}
