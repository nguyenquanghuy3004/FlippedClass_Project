package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.entity.LearningNode;
import java.util.List;

public interface NodeService {

    public List<NodeResponse> findByLearningPath(Long pathId);

    public NodeResponse findById(Long id);

    public NodeResponse create(Long pathId, NodeRequest request);

    public NodeResponse update(Long id, NodeRequest request);

    public void delete(Long id);

    public LearningNode getNode(Long id);
}
