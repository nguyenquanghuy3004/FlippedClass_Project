package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReorderLearningPathRequest {
    @NotEmpty(message = "orderedIds must not be empty")
    private List<Long> orderedIds;
}
