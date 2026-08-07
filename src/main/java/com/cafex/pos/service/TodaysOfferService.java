package com.cafex.pos.service;

import com.cafex.pos.dto.TodaysOfferRequest;
import com.cafex.pos.dto.TodaysOfferResponse;
import com.cafex.pos.dto.TodaysOfferPageResponse;

import java.util.Optional;

public interface TodaysOfferService {
    TodaysOfferResponse saveTodaysOffer(TodaysOfferRequest todaysOfferRequest);
    TodaysOfferResponse updateTodaysOffer(Long id, TodaysOfferRequest todaysOfferRequest);
    TodaysOfferPageResponse getTodaysOffersWithFilters(Long restaurantId, Boolean isActive, int page, int size);
    Optional<TodaysOfferResponse> getTodaysOfferById(Long id);
    void deleteTodaysOffer(Long id);
}
