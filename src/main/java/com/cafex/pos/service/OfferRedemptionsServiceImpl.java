package com.cafex.pos.service;

import com.cafex.pos.dto.OfferRedemptionPageResponse;
import com.cafex.pos.dto.OfferRedemptionRequest;
import com.cafex.pos.dto.OfferRedemptionResponse;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.OfferRedemptions;
import com.cafex.pos.entity.Offers;
import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.OfferRedemptionsRepository;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.OffersRepository;
import com.cafex.pos.repository.OrderRepository;
import com.cafex.pos.repository.RestaurantRepository;
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
public class OfferRedemptionsServiceImpl implements OfferRedemptionsService {

    private final OfferRedemptionsRepository offerRedemptionsRepository;
    private final CustomerRepository customerRepository;
    private final OffersRepository offersRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public OfferRedemptionResponse createRedemption(OfferRedemptionRequest request) {
        String redemptionId = request.getRedemptionId();
        if (redemptionId == null || redemptionId.trim().isEmpty()) {
            String prefix = "REDEEM";
            List<String> existingIds = offerRedemptionsRepository.findAll().stream()
                    .map(OfferRedemptions::getRedemptionId)
                    .filter(id -> id != null && id.startsWith(prefix))
                    .collect(Collectors.toList());
            int maxNum = existingIds.stream()
                    .map(id -> id.substring(prefix.length()))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);
            redemptionId = prefix + String.format("%03d", maxNum + 1);
        }

        log.info("Creating offer redemption: {}", redemptionId);

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));
        Offers offer = offersRepository.findById(request.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + request.getOfferId()));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + request.getRestaurantId()));

        OfferRedemptions redemption = new OfferRedemptions();
        redemption.setRedemptionId(redemptionId);
        redemption.setOffer(offer);
        redemption.setOrder(order);
        redemption.setCustomer(customer);
        redemption.setRestaurant(restaurant);
        redemption.setRedemptionCode(request.getRedemptionCode());
        redemption.setDiscountAmount(request.getDiscountAmount());
        redemption.setOriginalAmount(request.getOriginalAmount());
        redemption.setFinalAmount(request.getFinalAmount());
        redemption.setRedemptionMethod(request.getRedemptionMethod());
        redemption.setAppliedBy(request.getAppliedBy());
        redemption.setAppliedAt(request.getAppliedAt() != null ? request.getAppliedAt() : LocalDateTime.now());
        redemption.setOrderItems(request.getOrderItems());
        redemption.setConditionsMet(request.getConditionsMet());
        redemption.setUsageCount(0);
        redemption.setDeviceType(request.getDeviceType());
        redemption.setPlatform(request.getPlatform());
        redemption.setLocation(request.getLocation());
        redemption.setNotes(request.getNotes());
        redemption.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());

        OfferRedemptions savedRedemption = offerRedemptionsRepository.save(redemption);
        log.info("Offer redemption created successfully with ID: {}", savedRedemption.getId());

        return convertToResponse(savedRedemption);
    }

    @Override
    public OfferRedemptionResponse updateRedemption(Long id, OfferRedemptionRequest request) {
        log.info("Updating offer redemption with ID: {}", id);

        OfferRedemptions existingRedemption = offerRedemptionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer redemption not found with ID: " + id));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));
        Offers offer = offersRepository.findById(request.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + request.getOfferId()));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + request.getRestaurantId()));

        existingRedemption.setOffer(offer);
        existingRedemption.setOrder(order);
        existingRedemption.setCustomer(customer);
        existingRedemption.setRestaurant(restaurant);
        existingRedemption.setRedemptionCode(request.getRedemptionCode());
        existingRedemption.setDiscountAmount(request.getDiscountAmount());
        existingRedemption.setOriginalAmount(request.getOriginalAmount());
        existingRedemption.setFinalAmount(request.getFinalAmount());
        existingRedemption.setRedemptionMethod(request.getRedemptionMethod());
        existingRedemption.setAppliedBy(request.getAppliedBy());
        existingRedemption.setAppliedAt(request.getAppliedAt());
        existingRedemption.setOrderItems(request.getOrderItems());
        existingRedemption.setConditionsMet(request.getConditionsMet());
        existingRedemption.setDeviceType(request.getDeviceType());
        existingRedemption.setPlatform(request.getPlatform());
        existingRedemption.setLocation(request.getLocation());
        existingRedemption.setNotes(request.getNotes());

        OfferRedemptions updatedRedemption = offerRedemptionsRepository.save(existingRedemption);
        log.info("Offer redemption updated successfully with ID: {}", updatedRedemption.getId());

        return convertToResponse(updatedRedemption);
    }

    @Override
    public OfferRedemptionPageResponse getRedemptionsWithFilters(String customerId, String offerId, String restaurantId, String redemptionMethod, int page, int size) {
        log.info("Fetching redemptions with filters - customerId: {}, offerId: {}, restaurantId: {}, redemptionMethod: {}, page: {}, size: {}",
                customerId, offerId, restaurantId, redemptionMethod, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<OfferRedemptions> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (customerId != null && !customerId.trim().isEmpty()) {
                try {
                    Long cid = Long.parseLong(customerId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customer").get("id"), cid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (offerId != null && !offerId.trim().isEmpty()) {
                try {
                    Long oid = Long.parseLong(offerId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("offer").get("id"), oid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long rid = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), rid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (redemptionMethod != null && !redemptionMethod.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("redemptionMethod"), redemptionMethod));
            }

            return predicate;
        };

        Page<OfferRedemptions> redemptionPage = offerRedemptionsRepository.findAll(spec, pageable);

        List<OfferRedemptionResponse> content = redemptionPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new OfferRedemptionPageResponse(
                content,
                redemptionPage.getNumber() + 1,
                redemptionPage.getTotalPages(),
                redemptionPage.getTotalElements()
        );
    }

    @Override
    public Optional<OfferRedemptionResponse> getRedemptionById(Long id) {
        log.info("Fetching redemption by ID: {}", id);
        return offerRedemptionsRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteRedemption(Long id) {
        log.info("Deleting offer redemption with ID: {}", id);

        OfferRedemptions redemption = offerRedemptionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer redemption not found with ID: " + id));

        offerRedemptionsRepository.deleteById(id);
        log.info("Offer redemption deleted successfully with ID: {}", id);
    }

    private OfferRedemptionResponse convertToResponse(OfferRedemptions redemption) {
        OfferRedemptionResponse response = new OfferRedemptionResponse();
        response.setId(redemption.getId());
        response.setRedemptionId(redemption.getRedemptionId());
        response.setOfferId(redemption.getOffer() != null ? redemption.getOffer().getId() : null);
        response.setOrderId(redemption.getOrder() != null ? redemption.getOrder().getId() : null);
        response.setCustomerId(redemption.getCustomer() != null ? redemption.getCustomer().getId() : null);
        response.setRestaurantId(redemption.getRestaurant() != null ? redemption.getRestaurant().getId() : null);
        response.setRedemptionCode(redemption.getRedemptionCode());
        response.setDiscountAmount(redemption.getDiscountAmount());
        response.setOriginalAmount(redemption.getOriginalAmount());
        response.setFinalAmount(redemption.getFinalAmount());
        response.setRedemptionMethod(redemption.getRedemptionMethod());
        response.setAppliedBy(redemption.getAppliedBy());
        response.setAppliedAt(redemption.getAppliedAt());
        response.setOrderItems(redemption.getOrderItems());
        response.setConditionsMet(redemption.getConditionsMet());
        response.setUsageCount(redemption.getUsageCount());
        response.setDeviceType(redemption.getDeviceType());
        response.setPlatform(redemption.getPlatform());
        response.setLocation(redemption.getLocation());
        response.setNotes(redemption.getNotes());
        response.setCreatedAt(redemption.getCreatedAt());
        return response;
    }
}
