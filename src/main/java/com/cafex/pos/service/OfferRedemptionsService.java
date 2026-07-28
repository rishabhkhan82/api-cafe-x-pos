package com.cafex.pos.service;

import com.cafex.pos.dto.OfferRedemptionPageResponse;
import com.cafex.pos.dto.OfferRedemptionRequest;
import com.cafex.pos.dto.OfferRedemptionResponse;

import java.util.Optional;

public interface OfferRedemptionsService {
    OfferRedemptionResponse createRedemption(OfferRedemptionRequest request);
    OfferRedemptionResponse updateRedemption(Long id, OfferRedemptionRequest request);
    OfferRedemptionPageResponse getRedemptionsWithFilters(String customerId, String offerId, String restaurantId, String redemptionMethod, int page, int size);
    Optional<OfferRedemptionResponse> getRedemptionById(Long id);
    void deleteRedemption(Long id);
}
