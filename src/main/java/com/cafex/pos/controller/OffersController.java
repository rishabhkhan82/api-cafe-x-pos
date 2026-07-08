package com.cafex.pos.controller;

import com.cafex.pos.dto.OfferRequest;
import com.cafex.pos.dto.OfferResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.OfferPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
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
        OfferResponse response = offersService.saveOffer(offerRequest);
        log.info("Offer saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest offerRequest) {
        log.info("Update offer request received for ID: {}", id);
        OfferResponse response = offersService.updateOffer(id, offerRequest);
        log.info("Offer updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<OfferPageResponse> getOffers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(value = "restaurant_id", required = false) String restaurant_id,
            @RequestParam(required = false) String isActive,
            @RequestParam(required = false) String autoApply,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get offers request received with filters - name: {}, type: {}, restaurant_id: {}, isActive: {}, autoApply: {}, page: {}, size: {}",
                name, type, restaurant_id, isActive, autoApply, page, size);
        OfferPageResponse response = offersService.getOffersWithFilters(name, type, restaurant_id, isActive, autoApply, page, size);
        log.info("Retrieved {} offers (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferResponse> getOfferById(@PathVariable Long id) {
        log.info("Get offer by ID request received for ID: {}", id);
        OfferResponse response = offersService.getOfferById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
        log.info("Offer retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOffer(@PathVariable Long id) {
        log.info("Delete offer request received for ID: {}", id);
        offersService.deleteOffer(id);
        log.info("Offer deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "OFFER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}