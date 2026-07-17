package com.cafex.pos.service;

import com.cafex.pos.dto.BillingCyclePageResponse;
import com.cafex.pos.dto.BillingCycleRequest;
import com.cafex.pos.dto.BillingCycleResponse;
import java.util.List;
import java.util.Optional;

public interface BillingCycleService {
    BillingCyclePageResponse getBillingCyclesWithFilters(String name, Boolean isActive, int page, int size);
    List<BillingCycleResponse> getAllBillingCycles();
    Optional<BillingCycleResponse> getBillingCycleById(Long id);
    BillingCycleResponse createBillingCycle(BillingCycleRequest request);
    BillingCycleResponse updateBillingCycle(Long id, BillingCycleRequest request);
    void deleteBillingCycle(Long id);
}
