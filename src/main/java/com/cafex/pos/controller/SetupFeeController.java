package com.cafex.pos.controller;

import com.cafex.pos.dto.SetupFeePageResponse;
import com.cafex.pos.dto.SetupFeeRequest;
import com.cafex.pos.dto.SetupFeeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.SetupFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup-fees")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SetupFeeController {

    private final SetupFeeService setupFeeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createSetupFee(@Valid @RequestBody SetupFeeRequest request) {
        log.info("Create setup fee request received for key: {}", request.getKey());
        SetupFeeResponse response = setupFeeService.createSetupFee(request);
        log.info("Setup fee created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "SETUP_FEE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateSetupFee(@PathVariable Long id, @Valid @RequestBody SetupFeeRequest request) {
        log.info("Update setup fee request received for ID: {}", id);
        SetupFeeResponse response = setupFeeService.updateSetupFee(id, request);
        log.info("Setup fee updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "SETUP_FEE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<SetupFeePageResponse> getSetupFees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get setup fees request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        SetupFeePageResponse response = setupFeeService.getSetupFeesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} setup fees", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetupFeeResponse> getSetupFeeById(@PathVariable Long id) {
        log.info("Get setup fee by ID request received for ID: {}", id);
        SetupFeeResponse response = setupFeeService.getSetupFeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setup fee not found"));
        log.info("Setup fee retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteSetupFee(@PathVariable Long id) {
        log.info("Delete setup fee request received for ID: {}", id);
        setupFeeService.deleteSetupFee(id);
        log.info("Setup fee deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "SETUP_FEE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
