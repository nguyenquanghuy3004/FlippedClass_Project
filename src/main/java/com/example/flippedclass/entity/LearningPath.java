package com.example.flippedclass.entity;

import com.example.flippedclass.enums.LearningPathStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_paths")
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_space_id", nullable = false)
    private LearningSpace learningSpace;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningPathStatus status = LearningPathStatus.DRAFT;

    @Column(name = "position")
    private Integer position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "learningPath")
    private List<LearningNode> nodes = new ArrayList<>();

    @OneToMany(mappedBy = "learningPath")
    private List<NodeConnection> connections = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getLearningSpaceId() {
        return learningSpace == null ? null : learningSpace.getId();
    }

    public LearningSpace getLearningSpace() {
        return learningSpace;
    }

    public void setLearningSpace(LearningSpace learningSpace) {
        this.learningSpace = learningSpace;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LearningPathStatus getStatus() {
        return status;
    }

    public void setStatus(LearningPathStatus status) {
        this.status = status;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<LearningNode> getNodes() {
        return nodes;
    }

    public List<NodeConnection> getConnections() {
        return connections;
    }
}
