package com.cafex.pos.service;

import com.cafex.pos.dto.SetupFeePageResponse;
import com.cafex.pos.dto.SetupFeeRequest;
import com.cafex.pos.dto.SetupFeeResponse;
import java.util.List;
import java.util.Optional;

public interface SetupFeeService {
    SetupFeePageResponse getSetupFeesWithFilters(String name, Boolean isActive, int page, int size);
    List<SetupFeeResponse> getAllSetupFees();
    Optional<SetupFeeResponse> getSetupFeeById(Long id);
    SetupFeeResponse createSetupFee(SetupFeeRequest request);
    SetupFeeResponse updateSetupFee(Long id, SetupFeeRequest request);
    void deleteSetupFee(Long id);
}
