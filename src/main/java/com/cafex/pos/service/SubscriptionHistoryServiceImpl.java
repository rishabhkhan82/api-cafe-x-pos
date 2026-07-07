package com.cafex.pos.service;

import com.cafex.pos.dto.SubscriptionHistoryRequest;
import com.cafex.pos.dto.SubscriptionHistoryResponse;
import com.cafex.pos.dto.SubscriptionHistoryPageResponse;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.entity.SubscriptionHistory;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.repository.SubscriptionHistoryRepository;
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
public class SubscriptionHistoryServiceImpl implements SubscriptionHistoryService {

    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public SubscriptionHistoryResponse saveSubscriptionHistory(SubscriptionHistoryRequest request) {
        log.info("Saving new subscription history: {}", request.getHistoryId());

        SubscriptionHistory entity = convertToEntity(request);
        entity.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());

        SubscriptionHistory saved = subscriptionHistoryRepository.save(entity);
        log.info("Subscription history saved successfully with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public SubscriptionHistoryResponse updateSubscriptionHistory(Long id, SubscriptionHistoryRequest request) {
        log.info("Updating subscription history with ID: {}", id);

        SubscriptionHistory existing = subscriptionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription history not found with ID: " + id));

        // Update fields
        existing.setHistoryId(request.getHistoryId());
        existing.setNewPlanId(request.getNewPlanId());
        existing.setChangeType(request.getChangeType());
        existing.setEffectiveDate(request.getEffectiveDate());
        existing.setPreviousPrice(request.getPreviousPrice());
        existing.setNewPrice(request.getNewPrice());
        existing.setPriceDifference(request.getPriceDifference());
        existing.setBillingCycleChange(request.getBillingCycleChange());
        existing.setProratedAmount(request.getProratedAmount());
        existing.setInitiatedBy(request.getInitiatedBy());
        existing.setReason(request.getReason());
        existing.setNotes(request.getNotes());
        existing.setPaymentStatus(request.getPaymentStatus());
        existing.setPaymentId(request.getPaymentId());
        existing.setCancellationReason(request.getCancellationReason());
        existing.setChurnRiskScore(request.getChurnRiskScore());
        existing.setRetentionActions(request.getRetentionActions());
        existing.setPlanPriceAtSubscription(request.getPlanPriceAtSubscription());
        existing.setOfferNameAtSubscription(request.getOfferNameAtSubscription());
        existing.setOfferDiscountPercentageAtSubscription(request.getOfferDiscountPercentageAtSubscription());
        existing.setPlanNameAtSubscription(request.getPlanNameAtSubscription());

        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + request.getRestaurantId()));
            existing.setRestaurant(restaurant);
        }
        if (request.getPreviousPlanId() != null) {
            existing.setPreviousPlanId(request.getPreviousPlanId());
        }

        SubscriptionHistory updated = subscriptionHistoryRepository.save(existing);
        log.info("Subscription history updated successfully with ID: {}", updated.getId());

        return convertToResponse(updated);
    }

    @Override
    public SubscriptionHistoryPageResponse getSubscriptionHistoriesWithFilters(String historyId, String restaurantId, String changeType, String initiatedBy, String paymentStatus, int page, int size) {
        log.info("Fetching subscription histories with filters");

        Pageable pageable = PageRequest.of(Math.max(0, page), size);

        Specification<SubscriptionHistory> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (historyId != null && !historyId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("historyId"), "%" + historyId + "%"));
            }
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), id));
                } catch (NumberFormatException e) {}
            }
            if (changeType != null && !changeType.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("changeType"), changeType));
            }
            if (initiatedBy != null && !initiatedBy.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("initiatedBy"), initiatedBy));
            }
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
            }

            return predicate;
        };

        Page<SubscriptionHistory> pageResult = subscriptionHistoryRepository.findAll(spec, pageable);

        List<SubscriptionHistoryResponse> content = pageResult.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new SubscriptionHistoryPageResponse(content, pageResult.getNumber() + 1, pageResult.getTotalPages(), pageResult.getTotalElements());
    }

    @Override
    public Optional<SubscriptionHistoryResponse> getSubscriptionHistoryById(Long id) {
        log.info("Fetching subscription history by ID: {}", id);
        return subscriptionHistoryRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public void deleteSubscriptionHistory(Long id) {
        log.info("Deleting subscription history with ID: {}", id);
        subscriptionHistoryRepository.deleteById(id);
        log.info("Subscription history deleted successfully with ID: {}", id);
    }

    private SubscriptionHistory convertToEntity(SubscriptionHistoryRequest request) {
        SubscriptionHistory entity = new SubscriptionHistory();

        entity.setHistoryId(request.getHistoryId());
        entity.setNewPlanId(request.getNewPlanId());
        entity.setChangeType(request.getChangeType());
        entity.setEffectiveDate(request.getEffectiveDate());
        entity.setPreviousPrice(request.getPreviousPrice());
        entity.setNewPrice(request.getNewPrice());
        entity.setPriceDifference(request.getPriceDifference());
        entity.setBillingCycleChange(request.getBillingCycleChange());
        entity.setProratedAmount(request.getProratedAmount());
        entity.setInitiatedBy(request.getInitiatedBy());
        entity.setReason(request.getReason());
        entity.setNotes(request.getNotes());
        entity.setPaymentStatus(request.getPaymentStatus());
        entity.setPaymentId(request.getPaymentId());
        entity.setCancellationReason(request.getCancellationReason());
        entity.setChurnRiskScore(request.getChurnRiskScore());
        entity.setRetentionActions(request.getRetentionActions());
        entity.setPlanPriceAtSubscription(request.getPlanPriceAtSubscription());
        entity.setOfferNameAtSubscription(request.getOfferNameAtSubscription());
        entity.setOfferDiscountPercentageAtSubscription(request.getOfferDiscountPercentageAtSubscription());
        entity.setPlanNameAtSubscription(request.getPlanNameAtSubscription());

        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            entity.setRestaurant(restaurant);
        }
        entity.setPreviousPlanId(request.getPreviousPlanId());

        return entity;
    }

    private SubscriptionHistoryResponse convertToResponse(SubscriptionHistory entity) {
        SubscriptionHistoryResponse response = new SubscriptionHistoryResponse();
        response.setId(entity.getId());
        response.setHistoryId(entity.getHistoryId());
        response.setRestaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null);
        response.setPreviousPlanId(entity.getPreviousPlanId());
        response.setNewPlanId(entity.getNewPlanId());
        response.setChangeType(entity.getChangeType());
        response.setEffectiveDate(entity.getEffectiveDate());
        response.setPreviousPrice(entity.getPreviousPrice());
        response.setNewPrice(entity.getNewPrice());
        response.setPriceDifference(entity.getPriceDifference());
        response.setBillingCycleChange(entity.getBillingCycleChange());
        response.setProratedAmount(entity.getProratedAmount());
        response.setInitiatedBy(entity.getInitiatedBy());
        response.setReason(entity.getReason());
        response.setNotes(entity.getNotes());
        response.setPaymentStatus(entity.getPaymentStatus());
        response.setPaymentId(entity.getPaymentId());
        response.setCancellationReason(entity.getCancellationReason());
        response.setChurnRiskScore(entity.getChurnRiskScore());
        response.setRetentionActions(entity.getRetentionActions());
        response.setPlanPriceAtSubscription(entity.getPlanPriceAtSubscription());
        response.setOfferNameAtSubscription(entity.getOfferNameAtSubscription());
        response.setOfferDiscountPercentageAtSubscription(entity.getOfferDiscountPercentageAtSubscription());
        response.setPlanNameAtSubscription(entity.getPlanNameAtSubscription());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}