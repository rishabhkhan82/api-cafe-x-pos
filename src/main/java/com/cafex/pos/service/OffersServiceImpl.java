package com.cafex.pos.service;

import com.cafex.pos.dto.OfferRequest;
import com.cafex.pos.dto.OfferResponse;
import com.cafex.pos.dto.OfferPageResponse;
import com.cafex.pos.entity.Offers;
import com.cafex.pos.repository.OffersRepository;
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
public class OffersServiceImpl implements OffersService {

    private final OffersRepository offersRepository;

    @Override
    public OfferResponse saveOffer(OfferRequest offerRequest) {
        String offerId = offerRequest.getOfferId();
        if (offerId == null || offerId.trim().isEmpty()) {
            // Auto-generate offer ID
            String prefix = "OFFER";
            List<String> existingIds = offersRepository.findOfferIdsStartingWith(prefix);
            int maxNum = existingIds.stream()
                .map(id -> id.substring(prefix.length()))
                .filter(s -> s.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
            offerId = prefix + String.format("%03d", maxNum + 1);
        }
        log.info("Saving new offer: {}", offerId);

        // Check if offerId already exists
        if (offersRepository.existsByOfferId(offerId)) {
            throw new RuntimeException("Offer ID already exists: " + offerId);
        }

        Offers offer = new Offers();
        offer.setOfferId(offerId);
        offer.setName(offerRequest.getName());
        offer.setTitle(offerRequest.getTitle());
        offer.setDescription(offerRequest.getDescription());
        offer.setType(offerRequest.getType());
        offer.setValue(offerRequest.getValue());
        offer.setDiscountValue(offerRequest.getDiscountValue());
        offer.setMinOrderValue(offerRequest.getMinOrderValue());
        offer.setStartDate(offerRequest.getStartDate());
        offer.setEndDate(offerRequest.getEndDate());
        offer.setUsageLimit(offerRequest.getUsageLimit() != null ? offerRequest.getUsageLimit() : 0);
        offer.setUsageCount(offerRequest.getUsageCount() != null ? offerRequest.getUsageCount() : 0);
        offer.setMaxUsagePerCustomer(offerRequest.getMaxUsagePerCustomer() != null ? offerRequest.getMaxUsagePerCustomer() : 1);
        offer.setIsActive(offerRequest.getIsActive() != null ? offerRequest.getIsActive() : true);
        offer.setAutoApply(offerRequest.getAutoApply() != null ? offerRequest.getAutoApply() : false);
        offer.setCode(offerRequest.getCode());
        offer.setTerms(offerRequest.getTerms());
        offer.setRestaurantId(offerRequest.getRestaurantId());
        offer.setCreatedAt(offerRequest.getCreatedAt() != null ? offerRequest.getCreatedAt() : LocalDateTime.now());
        offer.setUpdatedAt(offerRequest.getUpdatedAt() != null ? offerRequest.getUpdatedAt() : LocalDateTime.now());
        offer.setCreatedBy(offerRequest.getCreatedBy());

        Offers savedOffer = offersRepository.save(offer);
        log.info("Offer saved successfully with ID: {}", savedOffer.getId());

        return convertToResponse(savedOffer);
    }

    @Override
    public OfferResponse updateOffer(Long id, OfferRequest offerRequest) {
        log.info("Updating offer with ID: {}", id);

        Offers existingOffer = offersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + id));

        String offerId = offerRequest.getOfferId();
        if (offerId == null || offerId.trim().isEmpty()) {
            throw new RuntimeException("Offer ID is required for update");
        }

        // Check offerId uniqueness if changed
        if (!existingOffer.getOfferId().equals(offerId) &&
            offersRepository.existsByOfferId(offerId)) {
            throw new RuntimeException("Offer ID already exists: " + offerId);
        }

        // Update fields
        existingOffer.setOfferId(offerId);
        existingOffer.setName(offerRequest.getName());
        existingOffer.setTitle(offerRequest.getTitle());
        existingOffer.setDescription(offerRequest.getDescription());
        existingOffer.setType(offerRequest.getType());
        existingOffer.setValue(offerRequest.getValue());
        existingOffer.setDiscountValue(offerRequest.getDiscountValue());
        existingOffer.setMinOrderValue(offerRequest.getMinOrderValue());
        existingOffer.setStartDate(offerRequest.getStartDate());
        existingOffer.setEndDate(offerRequest.getEndDate());
        existingOffer.setUsageLimit(offerRequest.getUsageLimit());
        existingOffer.setUsageCount(offerRequest.getUsageCount());
        existingOffer.setMaxUsagePerCustomer(offerRequest.getMaxUsagePerCustomer());
        existingOffer.setIsActive(offerRequest.getIsActive());
        existingOffer.setAutoApply(offerRequest.getAutoApply());
        existingOffer.setCode(offerRequest.getCode());
        existingOffer.setTerms(offerRequest.getTerms());
        existingOffer.setRestaurantId(offerRequest.getRestaurantId());
        existingOffer.setUpdatedAt(offerRequest.getUpdatedAt() != null ? offerRequest.getUpdatedAt() : LocalDateTime.now());
        existingOffer.setCreatedBy(offerRequest.getCreatedBy()); // Note: This might not be updated, but per request

        Offers updatedOffer = offersRepository.save(existingOffer);
        log.info("Offer updated successfully with ID: {}", updatedOffer.getId());

        return convertToResponse(updatedOffer);
    }

    @Override
    public OfferPageResponse getOffersWithFilters(String name, String type, String restaurantId, String isActive, String autoApply, int page, int size) {
        log.info("Fetching offers with filters - name: {}, type: {}, restaurantId: {}, isActive: {}, autoApply: {}, page: {}, size: {}",
                name, type, restaurantId, isActive, autoApply, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<Offers> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            // Type filter
            if (type != null && !type.trim().isEmpty() && !"all".equals(type)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }

            // Restaurant filter
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), id));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            // Is Active filter
            if (isActive != null && !isActive.trim().isEmpty() && !"all".equals(isActive)) {
                Boolean active = "true".equals(isActive);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), active));
            }

            // Auto Apply filter
            if (autoApply != null && !autoApply.trim().isEmpty() && !"all".equals(autoApply)) {
                Boolean apply = "true".equals(autoApply);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("autoApply"), apply));
            }

            return predicate;
        };

        Page<Offers> offerPage = offersRepository.findAll(spec, pageable);

        List<OfferResponse> content = offerPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new OfferPageResponse(
            content,
            offerPage.getNumber() + 1, // currentPage (1-based)
            offerPage.getTotalPages(),
            offerPage.getTotalElements()
        );
    }

    @Override
    public Optional<OfferResponse> getOfferById(Long id) {
        log.info("Fetching offer by ID: {}", id);
        return offersRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteOffer(Long id) {
        log.info("Deleting offer with ID: {}", id);

        Offers offer = offersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + id));

        offersRepository.deleteById(id);
        log.info("Offer deleted successfully with ID: {}", id);
    }

    private OfferResponse convertToResponse(Offers offer) {
        OfferResponse response = new OfferResponse();
        response.setId(offer.getId());
        response.setOfferId(offer.getOfferId());
        response.setName(offer.getName());
        response.setTitle(offer.getTitle());
        response.setDescription(offer.getDescription());
        response.setType(offer.getType());
        response.setValue(offer.getValue());
        response.setDiscountValue(offer.getDiscountValue());
        response.setMinOrderValue(offer.getMinOrderValue());
        response.setStartDate(offer.getStartDate());
        response.setEndDate(offer.getEndDate());
        response.setUsageLimit(offer.getUsageLimit());
        response.setUsageCount(offer.getUsageCount());
        response.setMaxUsagePerCustomer(offer.getMaxUsagePerCustomer());
        response.setIsActive(offer.getIsActive());
        response.setAutoApply(offer.getAutoApply());
        response.setCode(offer.getCode());
        response.setTerms(offer.getTerms());
        response.setRestaurantId(offer.getRestaurantId());
        response.setCreatedAt(offer.getCreatedAt());
        response.setUpdatedAt(offer.getUpdatedAt());
        response.setCreatedBy(offer.getCreatedBy());
        response.setUpdatedBy(offer.getCreatedBy()); // Assuming same as createdBy for now
        return response;
    }
}