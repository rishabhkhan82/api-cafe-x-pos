package com.cafex.pos.controller;

import com.cafex.pos.dto.OfferRequest;
import com.cafex.pos.dto.OfferResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.OfferPageResponse;
import com.cafex.pos.service.OffersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OffersController {

    private final OffersService offersService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveOffer(@Valid @RequestBody OfferRequest offerRequest) {
        log.info("Save offer request received for offerId: {}", offerRequest.getOfferId());
        try {
            OfferResponse response = offersService.saveOffer(offerRequest);
            log.info("Offer saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "OFFER_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save offer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "OFFER_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest offerRequest) {
        log.info("Update offer request received for ID: {}", id);
        try {
            OfferResponse response = offersService.updateOffer(id, offerRequest);
            log.info("Offer updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "OFFER_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update offer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "OFFER_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<OfferPageResponse> getOffers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String isActive,
            @RequestParam(required = false) String autoApply,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get offers request received with filters - name: {}, type: {}, restaurantId: {}, isActive: {}, autoApply: {}, page: {}, size: {}",
                name, type, restaurantId, isActive, autoApply, page, size);
        try {
            OfferPageResponse response = offersService.getOffersWithFilters(name, type, restaurantId, isActive, autoApply, page, size);
            log.info("Retrieved {} offers (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get offers: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferResponse> getOfferById(@PathVariable Long id) {
        log.info("Get offer by ID request received for ID: {}", id);
        try {
            OfferResponse response = offersService.getOfferById(id)
                    .orElseThrow(() -> new RuntimeException("Offer not found"));
            log.info("Offer retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOffer(@PathVariable Long id) {
        log.info("Delete offer request received for ID: {}", id);
        try {
            offersService.deleteOffer(id);
            log.info("Offer deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "OFFER_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete offer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "OFFER_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}