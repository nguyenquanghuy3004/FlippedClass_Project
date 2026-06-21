package com.example.flippedclass.event;

import org.springframework.context.ApplicationEvent;

public class SubmissionCreatedEvent extends ApplicationEvent {
    private final Long submissionId;
    private final Long groupId;

    public SubmissionCreatedEvent(Object source, Long submissionId, Long groupId) {
        super(source);
        this.submissionId = submissionId;
        this.groupId = groupId;
    }

    public Long getSubmissionId() { return submissionId; }
    public Long getGroupId() { return groupId; }
}
