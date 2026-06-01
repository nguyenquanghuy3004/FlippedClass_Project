package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.response.NodeConnectionResponse;
import com.example.flippedclass.service.NodeConnectionService;
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
@RequestMapping("/api/learning-paths/{pathId}/connections")
@RequiredArgsConstructor
public class NodeConnectionController {

    private final NodeConnectionService connectionService;

    @GetMapping
    public List<NodeConnectionResponse> findByLearningPath(@PathVariable Long pathId) {
        return connectionService.findByLearningPath(pathId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodeConnectionResponse create(
            @PathVariable Long pathId,
            @Valid @RequestBody NodeConnectionRequest request
    ) {
        return connectionService.create(pathId, request);
    }

    @PutMapping("/{connectionId}")
    public NodeConnectionResponse update(
            @PathVariable Long pathId,
            @PathVariable Long connectionId,
            @Valid @RequestBody NodeConnectionRequest request
    ) {
        return connectionService.update(pathId, connectionId, request);
    }

    @DeleteMapping("/{connectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long pathId, @PathVariable Long connectionId) {
        connectionService.delete(pathId, connectionId);
    }
}
