package com.cafex.pos.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRecipientsRequest {

    private String restaurantId;

    private List<String> roles;
}
