package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import java.util.List;

public interface CourseDocumentService {

    public List<CourseDocumentResponse> findByLearningPath(Long learningPathId);

    public CourseDocumentResponse create(Long learningPathId, CourseDocumentRequest request);

    public CourseDocumentResponse update(Long documentId, CourseDocumentRequest request);

    public void delete(Long documentId);
}
