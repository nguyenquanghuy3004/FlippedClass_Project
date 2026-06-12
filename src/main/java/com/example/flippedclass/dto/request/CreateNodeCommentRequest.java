package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNodeCommentRequest {
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
