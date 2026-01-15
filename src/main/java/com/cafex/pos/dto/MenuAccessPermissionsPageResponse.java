package com.cafex.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuAccessPermissionsPageResponse {
    private List<MenuAccessPermissionsResponse> data;
    private int currentPage;
    private int pageCount;
    private long totalRowCount;
}