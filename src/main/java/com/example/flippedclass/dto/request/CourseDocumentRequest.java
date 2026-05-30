package com.example.flippedclass.dto.request;

import com.example.flippedclass.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseDocumentRequest {
    @NotBlank(message = "Document title is required")
    @Size(max = 255, message = "Document title must not exceed 255 characters")
    private String title;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document URL is required")
    @Size(max = 1000, message = "Document URL must not exceed 1000 characters")
    private String url;

    private String description;
}
