package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.entity.LearningNode;

import java.util.List;

public interface NodeService {

    List<NodeResponse> findByLearningPath(Long pathId);

    NodeResponse findById(Long pathId, Long nodeId);

    NodeResponse create(Long pathId, NodeRequest request);

    NodeResponse update(Long pathId, Long nodeId, NodeRequest request);

    void delete(Long pathId, Long nodeId);

    LearningNode getNode(Long id);

}
