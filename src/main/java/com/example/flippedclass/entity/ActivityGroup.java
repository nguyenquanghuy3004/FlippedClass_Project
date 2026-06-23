package com.example.flippedclass.entity;

import com.example.flippedclass.enums.GroupStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    @JsonIgnore
    private ClassroomActivity activity;

    @NotBlank(message = "Group name cannot be empty")
    @Size(max = 100, message = "Group name max 100 characters")
    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "final_grade")
    private Double finalGrade;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String gradeComment;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status = GroupStatus.FORMING;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ActivityGroupMember> members = new ArrayList<>();

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private ActivitySubmission submission;
}
