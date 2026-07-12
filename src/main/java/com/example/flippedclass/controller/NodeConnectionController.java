package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.response.NodeConnectionResponse;
import com.example.flippedclass.service.NodeConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public NodeConnectionResponse create( @PathVariable Long pathId,@Valid @RequestBody NodeConnectionRequest request) {
        return connectionService.create(pathId, request);
    }

    @PutMapping("/{connectionId}")
    public NodeConnectionResponse update(@PathVariable Long pathId,@PathVariable Long connectionId,@Valid @RequestBody NodeConnectionRequest request) {
        return connectionService.update(pathId, connectionId, request);
    }

    @DeleteMapping("/{connectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long pathId, @PathVariable Long connectionId) {
        connectionService.delete(pathId, connectionId);
    }
}
