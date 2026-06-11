package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_bank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String options;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String correctAnswer;

    @Column
    @Builder.Default
    private Integer points = 10;

    @Column(name = "question_type", length = 50)
    @Builder.Default
    private String questionType = "SINGLE_CHOICE";
    
    @Column(name = "explanation", columnDefinition = "NVARCHAR(MAX)")
    private String explanation;
}
