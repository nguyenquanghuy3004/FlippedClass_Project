package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.UpdateProfileRequest;
import com.example.flippedclass.dto.response.StudentDetailResponse;
import com.example.flippedclass.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDetailResponse> getStudentDetail(@PathVariable Long id) {
        StudentDetailResponse response = studentProfileService.getStudentDetail(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDetailResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {

        StudentDetailResponse response = studentProfileService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }
}