package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.CreateLearningNodeItemRequest;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningNodeItem;
import com.example.flippedclass.repository.LearningNodeItemRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.service.LearningNodeItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningNodeItemServiceImpl implements LearningNodeItemService {

    private final LearningNodeItemRepository learningNodeItemRepository;
    private final LearningNodeRepository learningNodeRepository;

    @Override
    @Transactional
    public LearningNodeItem createItem(CreateLearningNodeItemRequest request) {
        LearningNode node = learningNodeRepository.findById(request.getLearningNodeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học tương ứng"));

        List<LearningNodeItem> existingItems = learningNodeItemRepository.findByLearningNodeIdOrderByPosition(request.getLearningNodeId());
        int nextPosition = existingItems.isEmpty() ? 1 : existingItems.get(existingItems.size() - 1).getPosition() + 1;

        LearningNodeItem item = LearningNodeItem.builder()
                .title(request.getTitle())
                .itemType(request.getItemType())
                .Url(request.getUrl())
                .content(request.getContent())
                .position(nextPosition)
                .learningNode(node)
                .quizId(request.getQuizId())
                .build();

        return learningNodeItemRepository.save(item);
    }

    @Override
    public List<LearningNodeItem> getItemByNodeId(Long nodeId) {
        return learningNodeItemRepository.findByLearningNodeIdOrderByPosition(nodeId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        learningNodeItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorderItem(List<Long> itemIdsInNewOrder) {
        for (int i = 0; i < itemIdsInNewOrder.size(); i++) {
            Long itemId = itemIdsInNewOrder.get(i);
            int newPosition = i + 1;
            learningNodeItemRepository.findById(itemId).ifPresent(item -> {
                item.setPosition(newPosition);
                learningNodeItemRepository.save(item);
            });
        }
    }
}
