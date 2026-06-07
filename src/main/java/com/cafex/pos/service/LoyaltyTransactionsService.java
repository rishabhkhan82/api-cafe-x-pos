package com.cafex.pos.service;

import com.cafex.pos.dto.LoyaltyTransactionsPageResponse;
import com.cafex.pos.dto.LoyaltyTransactionsRequest;
import com.cafex.pos.dto.LoyaltyTransactionsResponse;

import java.util.Optional;

public interface LoyaltyTransactionsService {
    LoyaltyTransactionsResponse createTransaction(LoyaltyTransactionsRequest request);
    LoyaltyTransactionsResponse updateTransaction(Long id, LoyaltyTransactionsRequest request);
    LoyaltyTransactionsPageResponse getTransactionsWithFilters(String customerId, String restaurantId, String transactionType, int page, int size);
    Optional<LoyaltyTransactionsResponse> getTransactionById(Long id);
    void deleteTransaction(Long id);
}
