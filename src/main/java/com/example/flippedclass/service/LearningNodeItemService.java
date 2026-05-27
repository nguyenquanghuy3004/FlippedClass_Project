package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.CreateLearningNodeItemRequest;
import com.example.flippedclass.entity.LearningNodeItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LearningNodeItemService {
    LearningNodeItem createItem(CreateLearningNodeItemRequest request);

    List<LearningNodeItem> getItemByNodeId(Long nodeId);

    void delete(Long id);

    void reorderItem(List<Long> itemIdsInNewOrder);
}
