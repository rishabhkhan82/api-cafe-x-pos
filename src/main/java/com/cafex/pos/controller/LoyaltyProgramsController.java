package com.cafex.pos.controller;

import com.cafex.pos.dto.LoyaltyProgramPageResponse;
import com.cafex.pos.dto.LoyaltyProgramRequest;
import com.cafex.pos.dto.LoyaltyProgramResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.service.LoyaltyProgramsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty-programs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class LoyaltyProgramsController {

    private final LoyaltyProgramsService loyaltyProgramsService;

    @PostMapping
    public ResponseEntity<OperationResponse> createProgram(@Valid @RequestBody LoyaltyProgramRequest request) {
        log.info("Create loyalty program request received for programId: {}", request.getProgramId());
        try {
            LoyaltyProgramResponse response = loyaltyProgramsService.createProgram(request);
            log.info("Loyalty program created successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_PROGRAM_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to create loyalty program: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_PROGRAM_CREATE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateProgram(@PathVariable Long id, @Valid @RequestBody LoyaltyProgramRequest request) {
        log.info("Update loyalty program request received for ID: {}", id);
        try {
            LoyaltyProgramResponse response = loyaltyProgramsService.updateProgram(id, request);
            log.info("Loyalty program updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_PROGRAM_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update loyalty program: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_PROGRAM_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<LoyaltyProgramPageResponse> getPrograms(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get loyalty programs request received with filters - customerId: {}, isActive: {}, page: {}, size: {}",
                customerId, isActive, page, size);
        try {
            LoyaltyProgramPageResponse response = loyaltyProgramsService.getProgramsWithFilters(customerId, isActive, page, size);
            log.info("Retrieved {} programs (page {} of {})",
                    response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty programs: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyProgramResponse> getProgramById(@PathVariable Long id) {
        log.info("Get loyalty program by ID request received for ID: {}", id);
        try {
            LoyaltyProgramResponse response = loyaltyProgramsService.getProgramById(id)
                    .orElseThrow(() -> new RuntimeException("Loyalty program not found"));
            log.info("Loyalty program retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty program: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteProgram(@PathVariable Long id) {
        log.info("Delete loyalty program request received for ID: {}", id);
        try {
            loyaltyProgramsService.deleteProgram(id);
            log.info("Loyalty program deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_PROGRAM_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete loyalty program: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_PROGRAM_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<LoyaltyProgramResponse> getProgramByCustomer(@PathVariable Long customerId) {
        log.info("Get loyalty program by customer request received for customerId: {}", customerId);
        try {
            LoyaltyProgramPageResponse response = loyaltyProgramsService.getProgramsWithFilters(
                    String.valueOf(customerId), null, 1, 1);
            if (response.getData() != null && !response.getData().isEmpty()) {
                return ResponseEntity.ok(response.getData().get(0));
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            log.error("Failed to get loyalty program by customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
