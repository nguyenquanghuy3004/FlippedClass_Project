package entity;

import entity.enums.InteractionType;
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

    @Column(nullable = false, length = 100)
    private String courseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InteractionType type;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false)
    private LocalDateTime occurredAt;
}
