package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.response.NodeConnectionResponse;
import java.util.List;

public interface NodeConnectionService {

    List<NodeConnectionResponse> findByLearningPath(Long pathId);

    NodeConnectionResponse create(Long pathId, NodeConnectionRequest request);

    NodeConnectionResponse update(Long pathId, Long connectionId, NodeConnectionRequest request);

    void delete(Long pathId, Long connectionId);
}
