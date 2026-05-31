package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import com.example.flippedclass.service.CourseDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths/{learningPathId}/documents")
@RequiredArgsConstructor
public class CourseDocumentController {

    private final CourseDocumentService documentService;

    @GetMapping
    public List<CourseDocumentResponse> findByLearningPath(@PathVariable Long learningPathId) {
        return documentService.findByLearningPath(learningPathId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDocumentResponse create(
            @PathVariable Long learningPathId,
            @Valid @RequestBody CourseDocumentRequest request
    ) {
        return documentService.create(learningPathId, request);
    }

    @PutMapping("/{documentId}")
    public CourseDocumentResponse update(
            @PathVariable Long learningPathId,
            @PathVariable Long documentId,
            @Valid @RequestBody CourseDocumentRequest request
    ) {
        return documentService.update(learningPathId, documentId, request);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long learningPathId, @PathVariable Long documentId) {
        documentService.delete(learningPathId, documentId);
    }
}
