package com.example.flippedclass.entity;

import com.example.flippedclass.enums.PeerPairingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "peer_pairings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"learning_space_id", "mentor_id", "mentee_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerPairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_space_id", nullable = false)
    private LearningSpace learningSpace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PeerPairingStatus status = PeerPairingStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime pairedAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
