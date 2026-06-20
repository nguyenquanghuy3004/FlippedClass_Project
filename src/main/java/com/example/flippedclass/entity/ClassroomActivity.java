package com.example.flippedclass.entity;

import com.example.flippedclass.enums.ActivityStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classroom_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomActivity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    @JsonIgnore
    private LearningSpace classroom;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "min_members_per_group")
    private Integer minMembersPerGroup = 1;

    @Column(name = "max_members_per_group")
    private Integer maxMembersPerGroup;

    @Column(name = "allow_late_submission")
    private boolean allowLateSubmission = false;

    @Column(name = "auto_group_enabled")
    private boolean autoGroupEnabled = false;

    @Future(message = "Deadline must be in the future")
    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.DRAFT;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ActivityGroup> groups = new ArrayList<>();
}
