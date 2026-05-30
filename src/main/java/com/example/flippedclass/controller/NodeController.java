package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.service.NodeService;
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
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/api/learning-paths/{pathId}/nodes")
    public List<NodeResponse> findByLearningPath(@PathVariable Long pathId) {
        return nodeService.findByLearningPath(pathId);
    }

    @PostMapping("/api/learning-paths/{pathId}/nodes")
    @ResponseStatus(HttpStatus.CREATED)
    public NodeResponse create(@PathVariable Long pathId, @RequestBody NodeRequest request) {
        return nodeService.create(pathId, request);
    }

    @GetMapping("/api/nodes/{id}")
    public NodeResponse findById(@PathVariable Long id) {
        return nodeService.findById(id);
    }

    @PutMapping("/api/nodes/{id}")
    public NodeResponse update(@PathVariable Long id, @RequestBody NodeRequest request) {
        return nodeService.update(id, request);
    }

    @DeleteMapping("/api/nodes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        nodeService.delete(id);
    }
}
