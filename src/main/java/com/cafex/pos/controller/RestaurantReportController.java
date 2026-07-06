package com.cafex.pos.controller;

import com.cafex.pos.dto.RestaurantReportResponse;
import com.cafex.pos.service.RestaurantReportPdfService;
import com.cafex.pos.service.RestaurantReportService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant-report")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
public class RestaurantReportController {

    private final RestaurantReportService restaurantReportService;
    private final RestaurantReportPdfService restaurantReportPdfService;

    @GetMapping
    public ResponseEntity<RestaurantReportResponse> getRestaurantReport(
            @RequestParam @NotNull String reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam @NotNull Long restaurantId) {
        log.info("Restaurant report request received - reportType: {}, startDate: {}, endDate: {}, restaurantId: {}", reportType, startDate, endDate, restaurantId);
        try {
            RestaurantReportResponse response = restaurantReportService.getRestaurantReport(reportType, startDate, endDate, restaurantId);
            log.info("Restaurant report generated successfully - type: {}", reportType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to generate restaurant report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<ByteArrayResource> getRestaurantReportPdf(
            @RequestParam @NotNull String reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam @NotNull Long restaurantId) {
        log.info("Restaurant report PDF request received - reportType: {}, startDate: {}, endDate: {}, restaurantId: {}", reportType, startDate, endDate, restaurantId);
        try {
            RestaurantReportResponse response = restaurantReportService.getRestaurantReport(reportType, startDate, endDate, restaurantId);
            ByteArrayResource pdfResource = restaurantReportPdfService.generatePdf(response);

            String filename = "report-" + reportType.toLowerCase() + "-" + System.currentTimeMillis() + ".pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfResource.contentLength())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfResource);
        } catch (Exception e) {
            log.error("Failed to generate restaurant report PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
