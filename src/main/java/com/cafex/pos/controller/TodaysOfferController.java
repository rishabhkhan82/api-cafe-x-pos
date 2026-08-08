package com.cafex.pos.controller;

import com.cafex.pos.dto.TodaysOfferRequest;
import com.cafex.pos.dto.TodaysOfferResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.TodaysOfferPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.TodaysOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todays-offers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TodaysOfferController {

    private final TodaysOfferService todaysOfferService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveTodaysOffer(@Valid @RequestBody TodaysOfferRequest todaysOfferRequest) {
        log.info("Save today's offer request received for restaurantId: {}", todaysOfferRequest.getRestaurantId());
        TodaysOfferResponse response = todaysOfferService.saveTodaysOffer(todaysOfferRequest);
        log.info("Today's offer saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "TODAYS_OFFER_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateTodaysOffer(@PathVariable Long id, @Valid @RequestBody TodaysOfferRequest todaysOfferRequest) {
        log.info("Update today's offer request received for ID: {}", id);
        TodaysOfferResponse response = todaysOfferService.updateTodaysOffer(id, todaysOfferRequest);
        log.info("Today's offer updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "TODAYS_OFFER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<TodaysOfferPageResponse> getTodaysOffers(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get today's offers request received with filters - restaurantId: {}, isActive: {}, page: {}, size: {}",
                restaurantId, isActive, page, size);
        TodaysOfferPageResponse response = todaysOfferService.getTodaysOffersWithFilters(restaurantId, isActive, page, size);
        log.info("Retrieved {} today's offers (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodaysOfferResponse> getTodaysOfferById(@PathVariable Long id) {
        log.info("Get today's offer by ID request received for ID: {}", id);
        TodaysOfferResponse response = todaysOfferService.getTodaysOfferById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Today's offer not found"));
        log.info("Today's offer retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteTodaysOffer(@PathVariable Long id) {
        log.info("Delete today's offer request received for ID: {}", id);
        todaysOfferService.deleteTodaysOffer(id);
        log.info("Today's offer deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "TODAYS_OFFER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
