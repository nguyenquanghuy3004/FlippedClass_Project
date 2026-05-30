package com.example.flippedclass.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearningPathRequest {
    private String title;
    private String description;
}
