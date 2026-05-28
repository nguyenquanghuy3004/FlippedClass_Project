package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.NodeConnectionResponse;
import com.example.flippedclass.service.NodeConnectionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeConnectionController {

    private final NodeConnectionService connectionService;

    public NodeConnectionController(NodeConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping("/api/learning-paths/{pathId}/connections")
    public List<NodeConnectionResponse> findByLearningPath(@PathVariable Long pathId) {
        return connectionService.findByLearningPath(pathId);
    }

    @PostMapping("/api/learning-paths/{pathId}/connections")
    @ResponseStatus(HttpStatus.CREATED)
    public NodeConnectionResponse create(
            @PathVariable Long pathId,
            @RequestBody NodeConnectionRequest request
    ) {
        return connectionService.create(pathId, request);
    }

    @DeleteMapping("/api/node-connections/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        connectionService.delete(id);
    }
}
