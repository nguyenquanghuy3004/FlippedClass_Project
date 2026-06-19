package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSummaryRequest {
    @NotNull(message = "Learning node ID is required")
    private Long learningNodeId;
    
    // We get studentId from JWT directly in the controller to ensure security,
    // but keeping it here if the existing service expects it.
    private Long studentId;
    
    @NotBlank(message = "Summary content is required")
    private String summaryContent;
}
