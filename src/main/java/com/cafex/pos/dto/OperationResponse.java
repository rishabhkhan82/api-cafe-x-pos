package com.cafex.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse {
    private String status;
    private String code;
    private Long id;
    private Object data; // Optional data field for additional response data
}