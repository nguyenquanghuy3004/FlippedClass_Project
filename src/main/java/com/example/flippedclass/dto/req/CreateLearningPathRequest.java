package com.example.flippedclass.dto.req;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearningPathRequest {
    private String title;
    private String description;
}
