package com.cafex.pos.controller;

import com.cafex.pos.dto.NotificationPageResponse;
import com.cafex.pos.dto.NotificationRequest;
import com.cafex.pos.dto.NotificationResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.service.NotificationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class NotificationsController {

    private final NotificationsService notificationsService;

    @PostMapping
    public ResponseEntity<OperationResponse> createNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        log.info("Create notification request received for recipient: {}", notificationRequest.getRecipientId());
        try {
            NotificationResponse response = notificationsService.createNotification(notificationRequest);
            log.info("Notification created successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "NOTIFICATION_CREATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to create notification: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "NOTIFICATION_CREATE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<NotificationPageResponse> getNotifications(
            @RequestParam(name = "recipient_id", required = false) String recipientId,
            @RequestParam(name = "recipient_role", required = false) String recipientRole,
            @RequestParam(name = "restaurant_id", required = false) String restaurantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get notifications request received with filters - recipientId: {}, recipientRole: {}, restaurantId: {}, status: {}, type: {}, page: {}, size: {}",
                recipientId, recipientRole, restaurantId, status, type, page, size);
        try {
            NotificationPageResponse response = notificationsService.getNotificationsWithFilters(recipientId, recipientRole, restaurantId, status, type, page, size);
            log.info("Retrieved {} notifications (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        log.info("Get notification by ID request received for ID: {}", id);
        try {
            NotificationResponse response = notificationsService.getNotificationById(id)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            log.info("Notification retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get notification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OperationResponse> updateNotificationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "read");
        String readAt = body.getOrDefault("readAt", body.get("read_at"));
        log.info("Update notification status request received for ID: {} to {}", id, status);
        try {
            NotificationResponse response = notificationsService.updateNotificationStatus(id, status, readAt);
            log.info("Notification status updated for ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "NOTIFICATION_STATUS_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update notification status: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "NOTIFICATION_STATUS_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<OperationResponse> markAllAsRead(
            @RequestBody Map<String, String> body) {
        String recipientId = body.getOrDefault("recipientId", body.get("recipient_id"));
        String status = body.getOrDefault("status", "read");
        String readAt = body.getOrDefault("readAt", body.get("read_at"));
        log.info("Mark all notifications as read request received for recipient: {}", recipientId);
        try {
            notificationsService.markAllAsRead(recipientId);
            log.info("All notifications marked as read for recipient: {}", recipientId);
            OperationResponse operationResponse = new OperationResponse("success", "ALL_NOTIFICATIONS_MARKED_READ", null, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to mark all notifications as read: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MARK_ALL_READ_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteNotification(@PathVariable Long id) {
        log.info("Delete notification request received for ID: {}", id);
        try {
            notificationsService.deleteNotification(id);
            log.info("Notification deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "NOTIFICATION_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete notification: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "NOTIFICATION_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}
