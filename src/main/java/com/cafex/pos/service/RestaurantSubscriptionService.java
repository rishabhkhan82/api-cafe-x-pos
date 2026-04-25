package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantSubscriptionRequest;
import com.cafex.pos.dto.RestaurantSubscriptionResponse;
import com.cafex.pos.dto.RestaurantSubscriptionPageResponse;

import java.util.List;
import java.util.Optional;

public interface RestaurantSubscriptionService {
    RestaurantSubscriptionResponse saveRestaurantSubscription(RestaurantSubscriptionRequest restaurantSubscriptionRequest);
    RestaurantSubscriptionResponse updateRestaurantSubscription(Long id, RestaurantSubscriptionRequest restaurantSubscriptionRequest);
    RestaurantSubscriptionPageResponse getRestaurantSubscriptionsWithFilters(String subscriptionId, String restaurantId, String planId, String status, String autoRenew, String cancelAtPeriodEnd, int page, int size);
    Optional<RestaurantSubscriptionResponse> getRestaurantSubscriptionById(Long id);
    void deleteRestaurantSubscription(Long id);

    // Trial-related methods
    RestaurantSubscriptionResponse createTrialSubscription(Long restaurantId, Long planId, Long userId);
    boolean hasRestaurantUsedTrial(Long restaurantId);
    List<RestaurantSubscriptionResponse> getActiveSubscriptions(Long restaurantId);
}