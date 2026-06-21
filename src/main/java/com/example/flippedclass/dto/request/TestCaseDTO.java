package com.example.flippedclass.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDTO {
    private Long id;
    private String inputData;
    private String expectedOutput;
    @JsonProperty("isHidden")
    private Boolean isHidden;
    private Integer points;
}
