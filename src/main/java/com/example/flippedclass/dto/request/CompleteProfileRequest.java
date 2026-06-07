package com.example.flippedclass.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteProfileRequest {
    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;
}
