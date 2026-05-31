package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_spaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "invite_code", unique = true, length = 50)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private com.example.flippedclass.enums.LearningSpaceStatus status = com.example.flippedclass.enums.LearningSpaceStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private com.example.flippedclass.enums.VisibilityType visibility = com.example.flippedclass.enums.VisibilityType.PRIVATE;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
