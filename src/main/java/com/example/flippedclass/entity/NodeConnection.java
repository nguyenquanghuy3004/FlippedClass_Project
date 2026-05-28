package com.example.flippedclass.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "node_connections",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_node_connection_source_target",
                columnNames = {"learning_path_id", "source_node_id", "target_node_id"}
        )
)
public class NodeConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private LearningNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private LearningNode targetNode;

    @Column(name = "condition_type")
    private String conditionType;

    @Column(name = "condition_value")
    private String conditionValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public LearningNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(LearningNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public LearningNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(LearningNode targetNode) {
        this.targetNode = targetNode;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
