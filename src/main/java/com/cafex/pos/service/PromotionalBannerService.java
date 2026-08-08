package com.cafex.pos.service;

import com.cafex.pos.dto.PromotionalBannerRequest;
import com.cafex.pos.dto.PromotionalBannerResponse;
import com.cafex.pos.dto.PromotionalBannerPageResponse;

import java.util.Optional;

public interface PromotionalBannerService {
    PromotionalBannerResponse savePromotionalBanner(PromotionalBannerRequest promotionalBannerRequest);
    PromotionalBannerResponse updatePromotionalBanner(Long id, PromotionalBannerRequest promotionalBannerRequest);
    PromotionalBannerPageResponse getPromotionalBannersWithFilters(Long restaurantId, String title, Boolean isActive, int page, int size);
    Optional<PromotionalBannerResponse> getPromotionalBannerById(Long id);
    void deletePromotionalBanner(Long id);
}
