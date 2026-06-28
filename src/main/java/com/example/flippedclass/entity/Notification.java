package com.example.flippedclass.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId; // Chỉ cần lưu ID người nhận cho đơn giản
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;

    private String link;   // Link chuyển hướng khi click
    private boolean isRead;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
