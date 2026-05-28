package com.example.flippedclass.controller;

import java.util.List;

import com.example.flippedclass.dto.CourseDocumentRequest;
import com.example.flippedclass.dto.CourseDocumentResponse;
import com.example.flippedclass.service.CourseDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseDocumentController {

    private final CourseDocumentService documentService;

    public CourseDocumentController(CourseDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/api/learning-paths/{learningPathId}/documents")
    public List<CourseDocumentResponse> findByLearningPath(@PathVariable Long learningPathId) {
        return documentService.findByLearningPath(learningPathId);
    }

    @PostMapping("/api/learning-paths/{learningPathId}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDocumentResponse create(
            @PathVariable Long learningPathId,
            @RequestBody CourseDocumentRequest request
    ) {
        return documentService.create(learningPathId, request);
    }

    @PutMapping("/api/course-documents/{documentId}")
    public CourseDocumentResponse update(
            @PathVariable Long documentId,
            @RequestBody CourseDocumentRequest request
    ) {
        return documentService.update(documentId, request);
    }

    @DeleteMapping("/api/course-documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long documentId) {
        documentService.delete(documentId);
    }
}
