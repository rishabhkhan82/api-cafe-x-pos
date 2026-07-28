package com.cafex.pos.controller;

import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.WasteTypePageResponse;
import com.cafex.pos.dto.WasteTypeRequest;
import com.cafex.pos.dto.WasteTypeResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.WasteTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/waste-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WasteTypeController {

    private final WasteTypeService wasteTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createWasteType(@Valid @RequestBody WasteTypeRequest request) {
        log.info("Create waste type request received for key: {}", request.getKey());
        WasteTypeResponse response = wasteTypeService.createWasteType(request);
        log.info("Waste type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateWasteType(@PathVariable Long id, @Valid @RequestBody WasteTypeRequest request) {
        log.info("Update waste type request received for ID: {}", id);
        WasteTypeResponse response = wasteTypeService.updateWasteType(id, request);
        log.info("Waste type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<WasteTypePageResponse> getWasteTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get waste types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        WasteTypePageResponse response = wasteTypeService.getWasteTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} waste types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteTypeResponse> getWasteTypeById(@PathVariable Long id) {
        log.info("Get waste type by ID request received for ID: {}", id);
        WasteTypeResponse response = wasteTypeService.getWasteTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste type not found"));
        log.info("Waste type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteWasteType(@PathVariable Long id) {
        log.info("Delete waste type request received for ID: {}", id);
        wasteTypeService.deleteWasteType(id);
        log.info("Waste type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
