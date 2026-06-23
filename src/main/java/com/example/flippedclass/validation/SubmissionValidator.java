package com.example.flippedclass.validation;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.entity.ClassroomActivity;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.exception.DeadlinePassedException;
import com.example.flippedclass.exception.IllegalActivityStateException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubmissionValidator {

    public void validateForSubmission(SubmissionRequest dto, ActivityGroup group) {
        ClassroomActivity activity = group.getActivity();

        if (activity.getStatus() == ActivityStatus.LOCKED && !activity.isAllowLateSubmission()) {
            throw new IllegalActivityStateException("Activity is locked and late submissions are not allowed.");
        }

        if (activity.getStatus() == ActivityStatus.GRADED) {
            throw new IllegalActivityStateException("Activity has already been graded.");
        }

        if (LocalDateTime.now().isAfter(activity.getDeadline()) && !activity.isAllowLateSubmission()) {
            throw new DeadlinePassedException("Deadline has passed and late submissions are not allowed.");
        }
    }
}
