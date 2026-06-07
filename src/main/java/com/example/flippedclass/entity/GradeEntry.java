package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grade_entries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "student_id", "criterion_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private EvaluationSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterion_id", nullable = false)
    private EvaluationCriterion criterion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @Column
    @Builder.Default
    private LocalDateTime gradedAt = LocalDateTime.now();
}
