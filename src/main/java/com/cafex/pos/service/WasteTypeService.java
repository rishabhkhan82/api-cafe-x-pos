package com.cafex.pos.service;

import com.cafex.pos.dto.WasteTypePageResponse;
import com.cafex.pos.dto.WasteTypeRequest;
import com.cafex.pos.dto.WasteTypeResponse;
import java.util.List;
import java.util.Optional;

public interface WasteTypeService {
    WasteTypePageResponse getWasteTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<WasteTypeResponse> getAllWasteTypes();
    Optional<WasteTypeResponse> getWasteTypeById(Long id);
    WasteTypeResponse createWasteType(WasteTypeRequest request);
    WasteTypeResponse updateWasteType(Long id, WasteTypeRequest request);
    void deleteWasteType(Long id);
}
