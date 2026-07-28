package com.cafex.pos.controller;

import com.cafex.pos.dto.WasteManagementRequest;
import com.cafex.pos.dto.WasteManagementResponse;
import com.cafex.pos.service.WasteManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/waste-management")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WasteManagementController {

    private final WasteManagementService wasteManagementService;

    @PostMapping("/batch")
    public ResponseEntity<List<WasteManagementResponse>> createWasteBatch(@Valid @RequestBody List<WasteManagementRequest> requests) {
        log.info("Batch create waste management request received with {} entries", requests.size());
        List<WasteManagementResponse> response = wasteManagementService.createWasteBatch(requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWasteByRestaurant(
            @RequestParam("restaurant_id") Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String waste_type,
            @RequestParam(required = false) String reason) {
        log.info("Get waste management records for restaurant: {}", restaurantId);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Map<String, Object> response = wasteManagementService.getWasteByRestaurant(restaurantId, pageable, search, waste_type, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteManagementResponse> getWasteById(@PathVariable Long id) {
        log.info("Get waste management record by ID: {}", id);
        Optional<WasteManagementResponse> response = wasteManagementService.getWasteById(id);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaste(@PathVariable Long id) {
        log.info("Delete waste management record by ID: {}", id);
        wasteManagementService.deleteWaste(id);
        return ResponseEntity.ok().build();
    }
}
