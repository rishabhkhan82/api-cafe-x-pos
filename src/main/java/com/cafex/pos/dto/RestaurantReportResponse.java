package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantReportResponse {
    @JsonProperty("report_meta")
    private ReportMeta reportMeta;
    private Map<String, Object> statistics;
    private List<Map<String, Object>> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportMeta {
        @JsonProperty("restaurant_name")
        private String restaurantName;
        @JsonProperty("restaurant_address")
        private String restaurantAddress;
        @JsonProperty("restaurant_phone")
        private String restaurantPhone;
        @JsonProperty("restaurant_email")
        private String restaurantEmail;
        @JsonProperty("gst_number")
        private String gstNumber;
        @JsonProperty("report_type")
        private String reportType;
        @JsonProperty("report_type_label")
        private String reportTypeLabel;
        @JsonProperty("start_date")
        private String startDate;
        @JsonProperty("end_date")
        private String endDate;
        @JsonProperty("generated_at")
        private String generatedAt;
        @JsonProperty("period")
        private String period;
    }
}
