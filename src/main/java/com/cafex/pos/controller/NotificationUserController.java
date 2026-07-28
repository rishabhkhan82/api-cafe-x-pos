package com.cafex.pos.controller;

import com.cafex.pos.dto.NotificationRecipientsRequest;
import com.cafex.pos.dto.UserResponse;
import com.cafex.pos.service.NotificationUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification-users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class NotificationUserController {

    private final NotificationUserService notificationUserService;

    @PostMapping("/recipients")
    public ResponseEntity<List<UserResponse>> getNotificationRecipients(@RequestBody(required = false) NotificationRecipientsRequest request) {
        log.info("Notification recipients request received with restaurantId: {}, roles: {}",
                request != null ? request.getRestaurantId() : null,
                request != null ? request.getRoles() : null);
        String restaurantId = request != null ? request.getRestaurantId() : null;
        List<String> roles = request != null ? request.getRoles() : null;
        List<UserResponse> response = notificationUserService.getUsersForNotifications(restaurantId, roles);
        log.info("Retrieved {} notification recipients", response.size());
        return ResponseEntity.ok(response);
    }
}
