package com.cafex.pos.service;

import com.cafex.pos.dto.LoyaltyProgramPageResponse;
import com.cafex.pos.dto.LoyaltyProgramRequest;
import com.cafex.pos.dto.LoyaltyProgramResponse;

import java.util.Optional;

public interface LoyaltyProgramsService {
    LoyaltyProgramResponse createProgram(LoyaltyProgramRequest request);
    LoyaltyProgramResponse createLoyaltyProgram(LoyaltyProgramRequest request);
    LoyaltyProgramResponse updateProgram(Long id, LoyaltyProgramRequest request);
    LoyaltyProgramPageResponse getProgramsWithFilters(String customerId, String isActive, int page, int size);
    Optional<LoyaltyProgramResponse> getProgramById(Long id);
    void deleteProgram(Long id);
}
