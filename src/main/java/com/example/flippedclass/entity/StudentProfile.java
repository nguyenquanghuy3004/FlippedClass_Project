package com.example.flippedclass.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "class_name")
    private String className;

    private String major;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getClassName() {
        return className;
    }

    public String getMajor() {
        return major;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }
}
