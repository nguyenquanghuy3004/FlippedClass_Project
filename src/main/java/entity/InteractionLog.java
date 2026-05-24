package entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interaction_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "learning_path_id")
    private Long learningPathId;

    @Column(name = "interaction_type", length = 50)
    private String interactionType;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String summary;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
