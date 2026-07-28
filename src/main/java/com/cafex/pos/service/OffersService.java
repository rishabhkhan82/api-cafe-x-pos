package com.cafex.pos.service;

import com.cafex.pos.dto.OfferRequest;
import com.cafex.pos.dto.OfferResponse;
import com.cafex.pos.dto.OfferPageResponse;

import java.util.Optional;

public interface OffersService {
    OfferResponse saveOffer(OfferRequest offerRequest);
    OfferResponse updateOffer(Long id, OfferRequest offerRequest);
    OfferPageResponse getOffersWithFilters(String name, String type, String restaurant_id, String isActive, String autoApply, int page, int size);
    Optional<OfferResponse> getOfferById(Long id);
    void deleteOffer(Long id);
}