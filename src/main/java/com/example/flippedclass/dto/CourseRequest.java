package com.example.flippedclass.dto;

import com.example.flippedclass.enums.CourseStatus;

public class CourseRequest {
    private String title;
    private String description;
    private CourseStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
