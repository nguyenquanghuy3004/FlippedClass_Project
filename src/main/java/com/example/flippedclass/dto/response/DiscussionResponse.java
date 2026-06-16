package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.DiscussionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionResponse {
    private Long id;
    private String content;
    private String authorName;
    private String authorAvatar;
    private String authorRole; // STUDENT, LECTURER, SUPPORTER
    private Long authorId;
    
    private DiscussionStatus status;
    private boolean isPinned;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<DiscussionResponse> replies;
    private int replyCount;
}
