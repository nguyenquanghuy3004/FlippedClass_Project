package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import java.util.List;

public interface CourseDocumentService {

    List<CourseDocumentResponse> findByLearningPath(Long learningPathId);

    CourseDocumentResponse create(Long learningPathId, CourseDocumentRequest request);

    CourseDocumentResponse update(Long learningPathId, Long documentId, CourseDocumentRequest request);

    void delete(Long learningPathId, Long documentId);
}
