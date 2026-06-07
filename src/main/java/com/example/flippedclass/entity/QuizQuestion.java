package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String options;

    @Column(length = 50)
    private String correctAnswer;

    @Column
    @Builder.Default
    private Integer points = 1;

    @Column(name = "question_type", length = 50)
    @Builder.Default
    private String questionType = "SINGLE_CHOICE";

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
