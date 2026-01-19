package com.cafex.pos.service;

import com.cafex.pos.dto.SubscriptionPlansRequest;
import com.cafex.pos.dto.SubscriptionPlansResponse;
import com.cafex.pos.dto.SubscriptionPlansPageResponse;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlansService {

    SubscriptionPlansResponse saveSubscriptionPlan(SubscriptionPlansRequest subscriptionPlansRequest);

    SubscriptionPlansResponse updateSubscriptionPlan(Long id, SubscriptionPlansRequest subscriptionPlansRequest);

    List<SubscriptionPlansResponse> getAllSubscriptionPlans();

    SubscriptionPlansPageResponse getSubscriptionPlansWithFilters(
            String name,
            String billingCycle,
            Boolean isActive,
            Boolean isPopular,
            int page,
            int size
    );

    Optional<SubscriptionPlansResponse> getSubscriptionPlanById(Long id);

    void deleteSubscriptionPlan(Long id);
}