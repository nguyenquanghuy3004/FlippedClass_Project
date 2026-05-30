package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.CreateLearningNodeItemRequest;
import com.example.flippedclass.dto.response.LearningNodeItemResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningNodeItem;
import com.example.flippedclass.repository.LearningNodeItemRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.service.FileStorageService;
import com.example.flippedclass.service.LearningNodeItemService;
import com.example.flippedclass.util.VideoUrlNormalizer;
import enums.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LearningNodeItemServiceImpl implements LearningNodeItemService {

    @Autowired
    private LearningNodeItemRepository learningNodeItemRepository;

    @Autowired
    private LearningNodeRepository learningNodeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public LearningNodeItemResponse createItem(Long nodeId, CreateLearningNodeItemRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài học tương ứng"));

        String normalizedUrl = normalizeItemUrl(request.getItemType(), request.getUrl());

        List<LearningNodeItem> existingItems = learningNodeItemRepository.findByLearningNodeIdOrderByPosition(nodeId);
        int nextPosition = existingItems.isEmpty() ? 1 : existingItems.get(existingItems.size() - 1).getPosition() + 1;

        LearningNodeItem item = LearningNodeItem.builder()
                .title(request.getTitle())
                .itemType(request.getItemType())
                .url(normalizedUrl)
                .content(request.getContent())
                .position(nextPosition)
                .learningNode(node)
                .quizId(request.getQuizId())
                .build();

        return toResponse(learningNodeItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningNodeItemResponse> getItemByNodeId(Long nodeId) {
        return learningNodeItemRepository.findByLearningNodeIdOrderByPosition(nodeId)
                .stream()
                .map(this::toResponse)
                .toList();
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

    private String normalizeItemUrl(ItemType itemType, String url) {
        if (itemType == ItemType.VIDEO && url != null && !url.isBlank()) {
            return VideoUrlNormalizer.normalize(url);
        }
        return url;
    }

    private LearningNodeItemResponse toResponse(LearningNodeItem item) {
        String url = item.getItemType() == ItemType.VIDEO
                ? VideoUrlNormalizer.normalize(item.getUrl())
                : item.getUrl();

        return LearningNodeItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .itemType(item.getItemType())
                .url(url)
                .fullUrl(fileStorageService.toFullUrl(url))
                .content(item.getContent())
                .position(item.getPosition())
                .quizId(item.getQuizId())
                .build();
    }
}
