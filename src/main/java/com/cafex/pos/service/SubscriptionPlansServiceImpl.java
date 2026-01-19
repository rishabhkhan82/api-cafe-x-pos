package com.cafex.pos.service;

import com.cafex.pos.dto.SubscriptionPlansRequest;
import com.cafex.pos.dto.SubscriptionPlansResponse;
import com.cafex.pos.dto.SubscriptionPlansPageResponse;
import com.cafex.pos.entity.SubscriptionPlans;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.SubscriptionPlansRepository;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlansServiceImpl implements SubscriptionPlansService {

    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final UserRepository userRepository;

    @Override
    public SubscriptionPlansResponse saveSubscriptionPlan(SubscriptionPlansRequest subscriptionPlansRequest) {
        SubscriptionPlans subscriptionPlan = mapToEntity(subscriptionPlansRequest);
        subscriptionPlan.setCreatedAt(LocalDateTime.now());
        subscriptionPlan.setUpdatedAt(LocalDateTime.now());
        subscriptionPlan = subscriptionPlansRepository.save(subscriptionPlan);
        return mapToResponse(subscriptionPlan);
    }

    @Override
    public SubscriptionPlansResponse updateSubscriptionPlan(Long id, SubscriptionPlansRequest subscriptionPlansRequest) {
        SubscriptionPlans existingPlan = subscriptionPlansRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
        updateEntityFromRequest(existingPlan, subscriptionPlansRequest);
        existingPlan.setUpdatedAt(LocalDateTime.now());
        existingPlan = subscriptionPlansRepository.save(existingPlan);
        return mapToResponse(existingPlan);
    }

    @Override
    public List<SubscriptionPlansResponse> getAllSubscriptionPlans() {
        List<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findAll();
        return subscriptionPlans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlansPageResponse getSubscriptionPlansWithFilters(
            String name,
            String billingCycle,
            Boolean isActive,
            Boolean isPopular,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<SubscriptionPlans> spec = buildSpecification(name, billingCycle, isActive, isPopular);
        Page<SubscriptionPlans> subscriptionPlansPage = subscriptionPlansRepository.findAll(spec, pageable);
        List<SubscriptionPlansResponse> responses = subscriptionPlansPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new SubscriptionPlansPageResponse(
                responses,
                subscriptionPlansPage.getNumber(),
                subscriptionPlansPage.getTotalPages(),
                subscriptionPlansPage.getTotalElements()
        );
    }

    @Override
    public Optional<SubscriptionPlansResponse> getSubscriptionPlanById(Long id) {
        return subscriptionPlansRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Override
    public void deleteSubscriptionPlan(Long id) {
        subscriptionPlansRepository.deleteById(id);
    }

    private Specification<SubscriptionPlans> buildSpecification(
            String name,
            String billingCycle,
            Boolean isActive,
            Boolean isPopular
    ) {
        return (root, query, criteriaBuilder) -> {
            Specification<SubscriptionPlans> spec = Specification.where(null);
            if (name != null && !name.isEmpty()) {
                spec = spec.and((r, q, cb) -> cb.like(r.get("name"), "%" + name + "%"));
            }
            if (billingCycle != null && !billingCycle.isEmpty()) {
                spec = spec.and((r, q, cb) -> cb.equal(r.get("billingCycle"), billingCycle));
            }
            if (isActive != null) {
                spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));
            }
            if (isPopular != null) {
                spec = spec.and((r, q, cb) -> cb.equal(r.get("isPopular"), isPopular));
            }
            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }

    private SubscriptionPlans mapToEntity(SubscriptionPlansRequest request) {
        SubscriptionPlans subscriptionPlan = new SubscriptionPlans();
        subscriptionPlan.setPlanId(request.getPlan_id());
        subscriptionPlan.setName(request.getName());
        subscriptionPlan.setDisplayName(request.getDisplay_name());
        subscriptionPlan.setDescription(request.getDescription());
        subscriptionPlan.setPrice(request.getPrice());
        subscriptionPlan.setCurrency(request.getCurrency());
        subscriptionPlan.setBillingCycle(request.getBilling_cycle());
        subscriptionPlan.setMaxRestaurants(request.getMax_restaurants());
        subscriptionPlan.setMaxUsers(request.getMax_users());
        subscriptionPlan.setFeatures(request.getFeatures());
        subscriptionPlan.setIsActive(request.getIs_active());
        subscriptionPlan.setIsPopular(request.getIs_popular());
        subscriptionPlan.setTrialDays(request.getTrial_days());
        subscriptionPlan.setSetupFee(request.getSetup_fee());
        return subscriptionPlan;
    }

    private void updateEntityFromRequest(SubscriptionPlans subscriptionPlan, SubscriptionPlansRequest request) {
        subscriptionPlan.setPlanId(request.getPlan_id());
        subscriptionPlan.setName(request.getName());
        subscriptionPlan.setDisplayName(request.getDisplay_name());
        subscriptionPlan.setDescription(request.getDescription());
        subscriptionPlan.setPrice(request.getPrice());
        subscriptionPlan.setCurrency(request.getCurrency());
        subscriptionPlan.setBillingCycle(request.getBilling_cycle());
        subscriptionPlan.setMaxRestaurants(request.getMax_restaurants());
        subscriptionPlan.setMaxUsers(request.getMax_users());
        subscriptionPlan.setFeatures(request.getFeatures());
        subscriptionPlan.setIsActive(request.getIs_active());
        subscriptionPlan.setIsPopular(request.getIs_popular());
        subscriptionPlan.setTrialDays(request.getTrial_days());
        subscriptionPlan.setSetupFee(request.getSetup_fee());
    }

    private SubscriptionPlansResponse mapToResponse(SubscriptionPlans subscriptionPlan) {
        SubscriptionPlansResponse response = new SubscriptionPlansResponse();
        response.setId(subscriptionPlan.getId());
        response.setPlan_id(subscriptionPlan.getPlanId());
        response.setName(subscriptionPlan.getName());
        response.setDisplay_name(subscriptionPlan.getDisplayName());
        response.setDescription(subscriptionPlan.getDescription());
        response.setPrice(subscriptionPlan.getPrice());
        response.setCurrency(subscriptionPlan.getCurrency());
        response.setBilling_cycle(subscriptionPlan.getBillingCycle());
        response.setMax_restaurants(subscriptionPlan.getMaxRestaurants());
        response.setMax_users(subscriptionPlan.getMaxUsers());
        response.setFeatures(subscriptionPlan.getFeatures());
        response.setIs_active(subscriptionPlan.getIsActive());
        response.setIs_popular(subscriptionPlan.getIsPopular());
        response.setSubscriber_count(subscriptionPlan.getSubscriberCount());
        response.setRevenue(subscriptionPlan.getRevenue());
        response.setTrial_days(subscriptionPlan.getTrialDays());
        response.setSetup_fee(subscriptionPlan.getSetupFee());
        response.setCreated_by(subscriptionPlan.getCreatedBy() != null ? subscriptionPlan.getCreatedBy().getId() : null);
        response.setUpdated_by(subscriptionPlan.getUpdatedBy() != null ? subscriptionPlan.getUpdatedBy().getId() : null);
        response.setCreated_at(subscriptionPlan.getCreatedAt());
        response.setUpdated_at(subscriptionPlan.getUpdatedAt());
        return response;
    }
}