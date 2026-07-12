package com.cafex.pos.service;

import com.cafex.pos.dto.WasteManagementRequest;
import com.cafex.pos.dto.WasteManagementResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WasteManagementService {
    List<WasteManagementResponse> createWasteBatch(List<WasteManagementRequest> requests);
    Map<String, Object> getWasteByRestaurant(Long restaurantId, Pageable pageable, String search, String wasteType, String reason);
    Optional<WasteManagementResponse> getWasteById(Long id);
    void deleteWaste(Long id);
}
