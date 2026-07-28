package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;

    @JsonProperty("notification_id")
    private String notificationId;

    @JsonProperty("recipient_id")
    private String recipientId;

    @JsonProperty("recipient_role")
    private String recipientRole;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("message")
    private String message;

    @JsonProperty("action_text")
    private String actionText;

    @JsonProperty("action_url")
    private String actionUrl;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("status")
    private String status;

    @JsonProperty("related_entity_type")
    private String relatedEntityType;

    @JsonProperty("related_entity_id")
    private String relatedEntityId;

    @JsonProperty("related_order_id")
    private String relatedOrderId;

    @JsonProperty("expires_at")
    private String expiresAt;

    @JsonProperty("sent_at")
    private String sentAt;

    @JsonProperty("read_at")
    private String readAt;

    @JsonProperty("created_at")
    private String createdAt;
}
