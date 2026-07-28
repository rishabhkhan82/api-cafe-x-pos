package com.cafex.pos.controller;

import com.cafex.pos.dto.StatePageResponse;
import com.cafex.pos.dto.StateRequest;
import com.cafex.pos.dto.StateResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.StateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/states")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class StateController {

    private final StateService stateService;

    @PostMapping
    public ResponseEntity<OperationResponse> createState(@Valid @RequestBody StateRequest request) {
        log.info("Create state request received for key: {}", request.getKey());
        StateResponse response = stateService.createState(request);
        log.info("State created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "STATE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateState(@PathVariable Long id, @Valid @RequestBody StateRequest request) {
        log.info("Update state request received for ID: {}", id);
        StateResponse response = stateService.updateState(id, request);
        log.info("State updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "STATE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<StatePageResponse> getStates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get states request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        StatePageResponse response = stateService.getStatesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} states", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateResponse> getStateById(@PathVariable Long id) {
        log.info("Get state by ID request received for ID: {}", id);
        StateResponse response = stateService.getStateById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        log.info("State retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteState(@PathVariable Long id) {
        log.info("Delete state request received for ID: {}", id);
        stateService.deleteState(id);
        log.info("State deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "STATE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
