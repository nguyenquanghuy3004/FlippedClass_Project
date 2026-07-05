package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_node_id", nullable = false)
    private LearningNode learningNode;

    @Column(name = "input_data", columnDefinition = "NVARCHAR(MAX)")
    private String inputData;

    @Column(name = "expected_output", columnDefinition = "NVARCHAR(MAX)")
    private String expectedOutput;

    @Column(name = "is_hidden")
    private Boolean isHidden;

    @Column(name = "points")
    private Integer points;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isHidden == null) isHidden = false;
        if (points == null) points = 10;
    }
}
