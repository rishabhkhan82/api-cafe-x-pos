package com.cafex.pos.dto;

import java.util.List;

public class SubscriptionPlansPageResponse {

    private List<SubscriptionPlansResponse> data;

    private int currentPage;

    private int pageCount;

    private long totalItems;

    public SubscriptionPlansPageResponse() {
    }

    public SubscriptionPlansPageResponse(List<SubscriptionPlansResponse> data, int currentPage, int pageCount, long totalItems) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageCount = pageCount;
        this.totalItems = totalItems;
    }

    // Getters and Setters
    public List<SubscriptionPlansResponse> getData() {
        return data;
    }

    public void setData(List<SubscriptionPlansResponse> data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}