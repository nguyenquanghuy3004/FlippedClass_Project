package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.service.NodeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learning-paths/{pathId}/nodes")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    @GetMapping
    public List<NodeResponse> findByLearningPath(@PathVariable Long pathId) {
        return nodeService.findByLearningPath(pathId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodeResponse create(@PathVariable Long pathId, @Valid @RequestBody NodeRequest request) {
        return nodeService.create(pathId, request);
    }

    @GetMapping("/{nodeId}")
    public NodeResponse findById(@PathVariable Long pathId, @PathVariable Long nodeId) {
        return nodeService.findById(pathId, nodeId);
    }

    @PutMapping("/{nodeId}")
    public NodeResponse update(
            @PathVariable Long pathId,
            @PathVariable Long nodeId,
            @Valid @RequestBody NodeRequest request
    ) {
        return nodeService.update(pathId, nodeId, request);
    }

    @DeleteMapping("/{nodeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long pathId, @PathVariable Long nodeId) {
        nodeService.delete(pathId, nodeId);
    }
}
