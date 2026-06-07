package com.example.flippedclass.dto.request;

import com.example.flippedclass.enums.NodeStatus;
import com.example.flippedclass.enums.NodeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NodeRequest {
    @NotBlank(message = "Node title is required")
    private String title;

    private String description;
    private String content;
    private NodeType nodeType;
    private NodeStatus status;

    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be greater than 0")
    private Integer displayOrder;

    @Min(value = 0, message = "Estimated minutes cannot be negative")
    private Integer estimatedMinutes;
}
