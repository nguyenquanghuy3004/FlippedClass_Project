package com.example.flippedclass.entity;

import com.example.flippedclass.enums.DiscussionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "node_discussions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeDiscussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_node_id", nullable = false)
    private LearningNode learningNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NodeDiscussion parentDiscussion;

    @OneToMany(mappedBy = "parentDiscussion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NodeDiscussion> replies = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DiscussionStatus status = DiscussionStatus.OPEN;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
