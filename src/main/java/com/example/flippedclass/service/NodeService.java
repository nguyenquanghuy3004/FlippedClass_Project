package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.entity.LearningNode;
import java.util.List;

// Xử lý nghiệp vụ của node học tập
public interface NodeService {

    public List<NodeResponse> findByLearningPath(Long pathId);

    public NodeResponse findById(Long pathId, Long id);

    public NodeResponse create(Long pathId, NodeRequest request);

    public NodeResponse update(Long pathId, Long id, NodeRequest request);

    public void delete(Long pathId, Long id);

    public LearningNode getNode(Long id);
}
