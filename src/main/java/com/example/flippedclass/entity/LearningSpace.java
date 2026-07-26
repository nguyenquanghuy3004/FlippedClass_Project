package com.example.flippedclass.entity;

import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.VisibilityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "NVARCHAR(255)", nullable = false)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "invite_code", length = 255, nullable = false, unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private LearningSpaceStatus status = LearningSpaceStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private VisibilityType visibility = VisibilityType.PRIVATE;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();







    @OneToMany(mappedBy = "learningSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningPath> paths = new ArrayList<>();

    @OneToMany(mappedBy = "learningSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningSpaceMember> members = new ArrayList<>();
}
