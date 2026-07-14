package com.example.flippedclass.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableStudentResponse {
    private Long id;
    private String username;
    private String fullName;
}
