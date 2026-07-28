package com.cafex.pos.service;

import com.cafex.pos.dto.BillingPeriodMonthsPageResponse;
import com.cafex.pos.dto.BillingPeriodMonthsRequest;
import com.cafex.pos.dto.BillingPeriodMonthsResponse;
import java.util.List;
import java.util.Optional;

public interface BillingPeriodMonthsService {
    BillingPeriodMonthsPageResponse getBillingPeriodMonthsWithFilters(String name, Boolean isActive, int page, int size);
    List<BillingPeriodMonthsResponse> getAllBillingPeriodMonths();
    Optional<BillingPeriodMonthsResponse> getBillingPeriodMonthsById(Long id);
    BillingPeriodMonthsResponse createBillingPeriodMonths(BillingPeriodMonthsRequest request);
    BillingPeriodMonthsResponse updateBillingPeriodMonths(Long id, BillingPeriodMonthsRequest request);
    void deleteBillingPeriodMonths(Long id);
}
