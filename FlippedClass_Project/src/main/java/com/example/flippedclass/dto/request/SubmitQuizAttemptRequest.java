package com.example.flippedclass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Nop bai quiz: key = questionId, value = dap an")
public class SubmitQuizAttemptRequest {

    @NotNull(message = "studentId is required")
    @Positive(message = "studentId must be a positive number")
    private Long studentId;

    @NotEmpty(message = "answers must not be empty")
    @Schema(example = "{\"1\": \"A\", \"2\": \"B\"}")
    private Map<Long, String> answers;
}
