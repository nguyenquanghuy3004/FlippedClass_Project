package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionBankRequest {
    @NotBlank(message = "Question content is required")
    private String content;
    
    @NotBlank(message = "Options are required")
    private String options;
    
    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;
    
    private Integer points;
    
    private String questionType;
    
    private String explanation;
}
