package com.cafex.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockLogSummaryResponse {
    private long totalLogs;
    private long sales;
    private long production;
    private long waste;
    private long adjustments;
    private long purchases;
}
