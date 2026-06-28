package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPageResponse {
    @JsonProperty("data")
    private List<NotificationResponse> data;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("pageCount")
    private int pageCount;

    @JsonProperty("totalRowCount")
    private long totalRowCount;
}
