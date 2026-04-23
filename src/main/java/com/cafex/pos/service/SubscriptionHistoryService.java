package com.cafex.pos.service;

import com.cafex.pos.dto.SubscriptionHistoryRequest;
import com.cafex.pos.dto.SubscriptionHistoryResponse;
import com.cafex.pos.dto.SubscriptionHistoryPageResponse;

import java.util.Optional;

public interface SubscriptionHistoryService {
    SubscriptionHistoryResponse saveSubscriptionHistory(SubscriptionHistoryRequest subscriptionHistoryRequest);
    SubscriptionHistoryResponse updateSubscriptionHistory(Long id, SubscriptionHistoryRequest subscriptionHistoryRequest);
    SubscriptionHistoryPageResponse getSubscriptionHistoriesWithFilters(String historyId, String restaurantId, String changeType, String initiatedBy, String paymentStatus, int page, int size);
    Optional<SubscriptionHistoryResponse> getSubscriptionHistoryById(Long id);
    void deleteSubscriptionHistory(Long id);
}