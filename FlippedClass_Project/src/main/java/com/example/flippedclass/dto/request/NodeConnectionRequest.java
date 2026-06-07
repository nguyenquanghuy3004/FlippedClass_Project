package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NodeConnectionRequest {
    @NotNull(message = "Source node id is required")
    private Long sourceNodeId;

    @NotNull(message = "Target node id is required")
    private Long targetNodeId;

    private String conditionType;
    private String conditionValue;
}
