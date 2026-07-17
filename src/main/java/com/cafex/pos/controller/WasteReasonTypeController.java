package com.cafex.pos.controller;

import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.WasteReasonTypePageResponse;
import com.cafex.pos.dto.WasteReasonTypeRequest;
import com.cafex.pos.dto.WasteReasonTypeResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.WasteReasonTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/waste-reason-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WasteReasonTypeController {

    private final WasteReasonTypeService wasteReasonTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createWasteReasonType(@Valid @RequestBody WasteReasonTypeRequest request) {
        log.info("Create waste reason type request received for key: {}", request.getKey());
        WasteReasonTypeResponse response = wasteReasonTypeService.createWasteReasonType(request);
        log.info("Waste reason type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_REASON_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateWasteReasonType(@PathVariable Long id, @Valid @RequestBody WasteReasonTypeRequest request) {
        log.info("Update waste reason type request received for ID: {}", id);
        WasteReasonTypeResponse response = wasteReasonTypeService.updateWasteReasonType(id, request);
        log.info("Waste reason type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_REASON_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<WasteReasonTypePageResponse> getWasteReasonTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get waste reason types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        WasteReasonTypePageResponse response = wasteReasonTypeService.getWasteReasonTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} waste reason types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteReasonTypeResponse> getWasteReasonTypeById(@PathVariable Long id) {
        log.info("Get waste reason type by ID request received for ID: {}", id);
        WasteReasonTypeResponse response = wasteReasonTypeService.getWasteReasonTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste reason type not found"));
        log.info("Waste reason type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteWasteReasonType(@PathVariable Long id) {
        log.info("Delete waste reason type request received for ID: {}", id);
        wasteReasonTypeService.deleteWasteReasonType(id);
        log.info("Waste reason type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "WASTE_REASON_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
