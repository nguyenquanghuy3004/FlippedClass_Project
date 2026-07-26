package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String studentCode;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String className;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String major;

    private Integer enrollmentYear;

    @Column(length = 20)
    private String phoneNumber;
}
