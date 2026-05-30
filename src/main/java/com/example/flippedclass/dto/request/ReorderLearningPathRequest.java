package com.example.flippedclass.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReorderLearningPathRequest {
    private List<Long> orderedIds;
}
