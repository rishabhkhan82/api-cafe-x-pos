package com.cafex.pos.controller;

import com.cafex.pos.dto.OfferRedemptionPageResponse;
import com.cafex.pos.dto.OfferRedemptionRequest;
import com.cafex.pos.dto.OfferRedemptionResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.OfferRedemptionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offer-redemptions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OfferRedemptionsController {

    private final OfferRedemptionsService offerRedemptionsService;

    @PostMapping
    public ResponseEntity<OperationResponse> createRedemption(@Valid @RequestBody OfferRedemptionRequest request) {
        log.info("Create offer redemption request received for offerId: {}, orderId: {}, customerId: {}",
                request.getOfferId(), request.getOrderId(), request.getCustomerId());
        OfferRedemptionResponse response = offerRedemptionsService.createRedemption(request);
        log.info("Offer redemption created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_REDEMPTION_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateRedemption(@PathVariable Long id, @Valid @RequestBody OfferRedemptionRequest request) {
        log.info("Update offer redemption request received for ID: {}", id);
        OfferRedemptionResponse response = offerRedemptionsService.updateRedemption(id, request);
        log.info("Offer redemption updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_REDEMPTION_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<OfferRedemptionPageResponse> getRedemptions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String offerId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String redemptionMethod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get redemptions request received with filters - customerId: {}, offerId: {}, restaurantId: {}, redemptionMethod: {}, page: {}, size: {}",
                customerId, offerId, restaurantId, redemptionMethod, page, size);
        OfferRedemptionPageResponse response = offerRedemptionsService.getRedemptionsWithFilters(
                customerId, offerId, restaurantId, redemptionMethod, page, size);
        log.info("Retrieved {} redemptions (page {} of {})",
                response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferRedemptionResponse> getRedemptionById(@PathVariable Long id) {
        log.info("Get redemption by ID request received for ID: {}", id);
        OfferRedemptionResponse response = offerRedemptionsService.getRedemptionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer redemption not found"));
        log.info("Offer redemption retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteRedemption(@PathVariable Long id) {
        log.info("Delete offer redemption request received for ID: {}", id);
        offerRedemptionsService.deleteRedemption(id);
        log.info("Offer redemption deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_REDEMPTION_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OfferRedemptionResponse>> getRedemptionsByCustomer(@PathVariable Long customerId) {
        log.info("Get redemptions by customer request received for customerId: {}", customerId);
        OfferRedemptionPageResponse response = offerRedemptionsService.getRedemptionsWithFilters(
                String.valueOf(customerId), null, null, null, 1, 100);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/offer/{offerId}")
    public ResponseEntity<List<OfferRedemptionResponse>> getRedemptionsByOffer(@PathVariable Long offerId) {
        log.info("Get redemptions by offer request received for offerId: {}", offerId);
        OfferRedemptionPageResponse response = offerRedemptionsService.getRedemptionsWithFilters(
                null, String.valueOf(offerId), null, null, 1, 100);
        return ResponseEntity.ok(response.getData());
    }
}
