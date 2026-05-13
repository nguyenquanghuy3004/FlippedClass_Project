package com.example.flippedclass.dto;

import com.example.flippedclass.enums.LearningPathStatus;

public class LearningPathRequest {
    private String title;
    private String description;
    private LearningPathStatus status;

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

    public LearningPathStatus getStatus() {
        return status;
    }

    public void setStatus(LearningPathStatus status) {
        this.status = status;
    }
}
