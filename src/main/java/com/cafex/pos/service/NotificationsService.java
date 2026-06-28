package com.cafex.pos.service;

import com.cafex.pos.dto.NotificationPageResponse;
import com.cafex.pos.dto.NotificationRequest;
import com.cafex.pos.dto.NotificationResponse;
import com.cafex.pos.entity.Notifications;
import com.cafex.pos.repository.NotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPageResponse getNotificationsWithFilters(
            String recipientId,
            String recipientRole,
            String restaurantId,
            String status,
            String type,
            int page,
            int size) {
        log.info("Fetching notifications with filters - recipientId: {}, recipientRole: {}, restaurantId: {}, status: {}, type: {}, page: {}, size: {}",
                recipientId, recipientRole, restaurantId, status, type, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<Notifications> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (recipientId != null && !recipientId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("recipientId"), recipientId));
            }

            if (recipientRole != null && !recipientRole.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("recipientRole"), recipientRole));
            }

            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), Long.parseLong(restaurantId)));
            }

            if (status != null && !status.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }

            if (type != null && !type.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }

            return predicate;
        };

        Page<Notifications> notificationPage = notificationsRepository.findAll(spec, pageable);

        List<NotificationResponse> content = notificationPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new NotificationPageResponse(
                content,
                notificationPage.getNumber() + 1,
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    public Optional<NotificationResponse> getNotificationById(Long id) {
        log.info("Fetching notification by ID: {}", id);
        return notificationsRepository.findById(id)
                .map(this::convertToResponse);
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for recipient: {}", request.getRecipientId());

        Notifications notification = new Notifications();
        notification.setNotificationId(request.getNotificationId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : "medium");
        notification.setStatus(request.getStatus() != null ? request.getStatus() : "unread");
        notification.setRecipientId(request.getRecipientId());
        notification.setRecipientRole(request.getRecipientRole());
        notification.setIcon(request.getIcon());
        notification.setActionUrl(request.getActionUrl());
        notification.setActionText(request.getActionText());
        notification.setRelatedOrderId(request.getRelatedOrderId());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setSentAt(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());

        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            notification.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ISO_DATE_TIME));
        }

        Notifications saved = notificationsRepository.save(notification);
        log.info("Notification created successfully with ID: {}", saved.getId());
        NotificationResponse response = convertToResponse(saved);
        this.messagingTemplate.convertAndSend("/topic/users/" + response.getRecipientId() + "/notifications", response);
        return response;
    }

    public NotificationResponse updateNotificationStatus(Long id, String status, String readAt) {
        log.info("Updating notification status for ID: {} to {}", id, status);

        Notifications notification = notificationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));

        notification.setStatus(status);

        if (readAt != null && !readAt.isEmpty()) {
            notification.setReadAt(LocalDateTime.parse(readAt, DateTimeFormatter.ISO_DATE_TIME));
        }

        Notifications updated = notificationsRepository.save(notification);
        log.info("Notification status updated for ID: {}", id);
        NotificationResponse response = convertToResponse(updated);
        this.messagingTemplate.convertAndSend("/topic/users/" + response.getRecipientId() + "/notifications", response);
        return response;
    }

    public void deleteNotification(Long id) {
        log.info("Deleting notification with ID: {}", id);
        if (!notificationsRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with ID: " + id);
        }
        notificationsRepository.deleteById(id);
        log.info("Notification deleted successfully with ID: {}", id);
    }

    public NotificationResponse markAllAsRead(String recipientId) {
        log.info("Marking all notifications as read for recipient: {}", recipientId);
        List<Notifications> notifications = notificationsRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("recipientId"), recipientId));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), "unread"));
            return predicate;
        });

        LocalDateTime now = LocalDateTime.now();
        for (Notifications notification : notifications) {
            notification.setStatus("read");
            notification.setReadAt(now);
            notificationsRepository.save(notification);
        }

        log.info("Marked {} notifications as read for recipient: {}", notifications.size(), recipientId);
        return null;
    }

    private NotificationResponse convertToResponse(Notifications notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setNotificationId(notification.getNotificationId());
        response.setRecipientId(notification.getRecipientId());
        response.setRecipientRole(notification.getRecipientRole());
        response.setRestaurantId(notification.getRestaurant() != null ? String.valueOf(notification.getRestaurant().getId()) : null);
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setActionText(notification.getActionText());
        response.setActionUrl(notification.getActionUrl());
        response.setIcon(notification.getIcon());
        response.setPriority(notification.getPriority());
        response.setStatus(notification.getStatus());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setRelatedOrderId(notification.getRelatedOrderId());
        response.setExpiresAt(notification.getExpiresAt() != null ? notification.getExpiresAt().toString() : null);
        response.setSentAt(notification.getSentAt() != null ? notification.getSentAt().toString() : null);
        response.setReadAt(notification.getReadAt() != null ? notification.getReadAt().toString() : null);
        response.setCreatedAt(notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null);
        return response;
    }
}
