package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningNodeItem;
import com.example.flippedclass.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentDocumentResponse {
    private Long nodeId;
    private Long spaceId;
    private String title;
    private String url;
    private String learningPathTitle;
    private String learningSpaceName;
    private LocalDateTime uploadedAt;
    private String documentType;

    public static RecentDocumentResponse from(LearningNodeItem item) {
        if (item == null) return null;
        
        Long nId = null;
        Long sId = null;
        String pathTitle = null;
        String spaceName = null;
        LocalDateTime createdAt = null;

        if (item.getLearningNode() != null) {
            nId = item.getLearningNode().getId();
            createdAt = item.getLearningNode().getCreatedAt();
            if (item.getLearningNode().getLearningPath() != null) {
                pathTitle = item.getLearningNode().getLearningPath().getTitle();
                if (item.getLearningNode().getLearningPath().getLearningSpace() != null) {
                    sId = item.getLearningNode().getLearningPath().getLearningSpace().getId();
                    spaceName = item.getLearningNode().getLearningPath().getLearningSpace().getName();
                }
            }
        }

        return RecentDocumentResponse.builder()
                .nodeId(nId)
                .spaceId(sId)
                .title(item.getTitle())
                .url(item.getUrl())
                .learningPathTitle(pathTitle)
                .learningSpaceName(spaceName)
                .uploadedAt(createdAt)
                .documentType(item.getItemType() != null ? item.getItemType().name() : "OTHER")
                .build();
    }
}
