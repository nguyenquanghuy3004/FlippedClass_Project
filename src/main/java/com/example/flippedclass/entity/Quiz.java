package com.example.flippedclass.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Thông tin bài quiz gắn với node học tập
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_node_id")
    private LearningNode learningNode;

    @Column(name = "lecturer_id")
    private Long lecturerId;

    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public LearningNode getLearningNode() {
        return learningNode;
    }

    public Long getLearningNodeId() {
        return learningNode == null ? null : learningNode.getId();
    }
}
