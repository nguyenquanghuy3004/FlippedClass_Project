package com.example.flippedclass.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBankResponse {
    private Long id;
    private String content;
    private String options;
    private String correctAnswer;
    private Integer points;
    private String questionType;
    private String explanation;
    private String category;
}
