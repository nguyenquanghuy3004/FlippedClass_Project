package com.example.flippedclass.entity;

import com.example.flippedclass.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitySubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    @JsonIgnore
    private ActivityGroup group;

    @Pattern(regexp = "^https://github\\.com/.+$", message = "Invalid Github URL format")
    @Column(name = "github_url", nullable = false, length = 500)
    private String githubUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    @JsonIgnore
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.PENDING;
}
