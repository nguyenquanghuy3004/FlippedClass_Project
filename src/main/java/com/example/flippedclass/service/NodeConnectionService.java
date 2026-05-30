package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.response.NodeConnectionResponse;
import java.util.List;

// Xử lý nghiệp vụ liên kết giữa các node
public interface NodeConnectionService {

    public List<NodeConnectionResponse> findByLearningPath(Long pathId);

    public NodeConnectionResponse create(Long pathId, NodeConnectionRequest request);

    public void delete(Long pathId, Long id);
}
