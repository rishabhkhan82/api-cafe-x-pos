package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlanFeaturesPageResponse {

    @JsonProperty("data")
    private List<PlanFeaturesResponse> data;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("page_count")
    private int pageCount;

    @JsonProperty("total_count")
    private long totalCount;

    public PlanFeaturesPageResponse(List<PlanFeaturesResponse> data, int currentPage, int pageCount, long totalCount) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageCount = pageCount;
        this.totalCount = totalCount;
    }
}