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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Quản lý các API nối các node trong Learning Path
@RestController
@RequestMapping("/api/learning-paths/{pathId}/connections")
@RequiredArgsConstructor
public class NodeConnectionController {

    private final NodeConnectionService connectionService;

    // Lấy danh sách liên kết node theo Learning Path
    @GetMapping
    public List<NodeConnectionResponse> findByLearningPath(@PathVariable Long pathId) {
        return connectionService.findByLearningPath(pathId);
    }

    // Tạo liên kết giữa hai node học tập
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodeConnectionResponse create(
            @PathVariable Long pathId,
            @Valid @RequestBody NodeConnectionRequest request
    ) {
        return connectionService.create(pathId, request);
    }

    // Xóa liên kết giữa các node
    @DeleteMapping("/{connectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long pathId, @PathVariable Long connectionId) {
        connectionService.delete(pathId, connectionId);
    }
}
