package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerSubmissionResponse;
import com.example.flippedclass.service.LecturerSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lecturer/submissions")
@RequiredArgsConstructor
public class LecturerSubmissionController {

    private final LecturerSubmissionService lecturerSubmissionService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public LecturerSubmissionResponse getSubmissionDetails(@PathVariable Long id) {
        return lecturerSubmissionService.getSubmissionDetails(id);
    }

    @PutMapping("/{id}/grade")
    @PreAuthorize("hasAuthority('LECTURER')")
    public LecturerSubmissionResponse gradeSubmission( @PathVariable Long id,
            @Valid @RequestBody LecturerGradeRequest request) {
        return lecturerSubmissionService.gradeSubmission(id, request);
    }
}
