package entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "evaluation_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private EvaluationSession session;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(nullable = false)
    private Integer sortOrder;
}
