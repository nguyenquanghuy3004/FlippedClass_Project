package com.example.flippedclass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.ItemType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_node_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningNodeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50)
    private ItemType itemType;

    @Column(name = "url", columnDefinition = "NVARCHAR(MAX)")
    private String url;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(nullable = false)
    private Integer position;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_node_id", nullable = false)
    private LearningNode learningNode;

    @Column(name = "quiz_id")
    private Long quizId;
}
