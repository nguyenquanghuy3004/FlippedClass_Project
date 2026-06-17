package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompletedNodeResponse {
    private Long nodeId;
    private String nodeName;
    private LocalDateTime completedAt;
}
