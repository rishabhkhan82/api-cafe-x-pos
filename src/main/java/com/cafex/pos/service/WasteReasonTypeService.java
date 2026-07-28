package com.cafex.pos.service;

import com.cafex.pos.dto.WasteReasonTypePageResponse;
import com.cafex.pos.dto.WasteReasonTypeRequest;
import com.cafex.pos.dto.WasteReasonTypeResponse;
import java.util.List;
import java.util.Optional;

public interface WasteReasonTypeService {
    WasteReasonTypePageResponse getWasteReasonTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<WasteReasonTypeResponse> getAllWasteReasonTypes();
    Optional<WasteReasonTypeResponse> getWasteReasonTypeById(Long id);
    WasteReasonTypeResponse createWasteReasonType(WasteReasonTypeRequest request);
    WasteReasonTypeResponse updateWasteReasonType(Long id, WasteReasonTypeRequest request);
    void deleteWasteReasonType(Long id);
}
