package com.cafex.pos.dto;

import java.util.List;

public class InventoryStockLogPageResponse {
    private List<InventoryStockLogResponse> data;
    private int currentPage;
    private int pageCount;
    private long totalRowCount;

    public InventoryStockLogPageResponse() {}

    public InventoryStockLogPageResponse(List<InventoryStockLogResponse> data, int currentPage, int pageCount, long totalRowCount) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageCount = pageCount;
        this.totalRowCount = totalRowCount;
    }

    public List<InventoryStockLogResponse> getData() {
        return data;
    }

    public void setData(List<InventoryStockLogResponse> data) {
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

    public long getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(long totalRowCount) {
        this.totalRowCount = totalRowCount;
    }
}
