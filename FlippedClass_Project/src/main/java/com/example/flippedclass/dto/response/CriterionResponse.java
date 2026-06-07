package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CriterionResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal maxScore;
    private Integer sortOrder;
}
