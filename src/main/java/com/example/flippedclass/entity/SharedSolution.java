package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_solutions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedSolution {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "learning_node_id", nullable = false)
        private LearningNode learningNode;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "student_id", nullable = false)
        private User student;

        @Column(name = "title" , nullable = false, length = 255)
        private String title;


        @Column(name = "code_content", columnDefinition = "NVARCHAR(MAX)", nullable = false)
        private String codeContent;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

    @Column(name = "upvotes", nullable = false)
    @Builder.Default
    private int upvotes = 0;
}
