package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.LearningPathResponse;
import com.example.flippedclass.service.LearningPathService;
import java.util.List;
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
public class LearningPathController {

    private final LearningPathService learningPathService;

    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @GetMapping("/api/learning-spaces/{learningSpaceId}/learning-paths")
    public List<LearningPathResponse> findByLearningSpace(@PathVariable Long learningSpaceId) {
        return learningPathService.findByLearningSpace(learningSpaceId);
    }

    @PostMapping("/api/learning-spaces/{learningSpaceId}/learning-paths")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningPathResponse create(
            @PathVariable Long learningSpaceId,
            @RequestBody LearningPathRequest request
    ) {
        return learningPathService.create(learningSpaceId, request);
    }

    @GetMapping("/api/learning-paths/{id}")
    public LearningPathResponse findById(@PathVariable Long id) {
        return learningPathService.findById(id);
    }

    @PutMapping("/api/learning-paths/{id}")
    public LearningPathResponse update(@PathVariable Long id, @RequestBody LearningPathRequest request) {
        return learningPathService.update(id, request);
    }

    @DeleteMapping("/api/learning-paths/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        learningPathService.delete(id);
    }
}
