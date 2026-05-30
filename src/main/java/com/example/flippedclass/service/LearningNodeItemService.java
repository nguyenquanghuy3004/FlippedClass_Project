package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningNodeItemRequest;
import com.example.flippedclass.dto.response.LearningNodeItemResponse;

import java.util.List;

public interface LearningNodeItemService {

    LearningNodeItemResponse createItem(Long nodeId, CreateLearningNodeItemRequest request);

    List<LearningNodeItemResponse> getItemByNodeId(Long nodeId);

    void delete(Long id);

    void reorderItem(List<Long> itemIdsInNewOrder);
}
