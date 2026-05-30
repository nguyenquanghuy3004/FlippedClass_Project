package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(length = 1000)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
