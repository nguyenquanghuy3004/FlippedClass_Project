package com.example.flippedclass.service;

import java.util.List;

import com.example.flippedclass.dto.CourseRequest;
import com.example.flippedclass.dto.CourseResponse;
import com.example.flippedclass.entity.Course;
import com.example.flippedclass.enums.CourseStatus;
import com.example.flippedclass.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse findById(Long id) {
        return CourseResponse.from(getCourse(id));
    }

    public CourseResponse create(CourseRequest request) {
        validateTitle(request.getTitle());
        if (courseRepository.existsByTitleIgnoreCase(request.getTitle().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course title already exists");
        }

        Course course = new Course();
        applyRequest(course, request);
        return CourseResponse.from(courseRepository.save(course));
    }

    public CourseResponse update(Long id, CourseRequest request) {
        validateTitle(request.getTitle());
        Course course = getCourse(id);
        applyRequest(course, request);
        return CourseResponse.from(courseRepository.save(course));
    }

    public void delete(Long id) {
        Course course = getCourse(id);
        courseRepository.delete(course);
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private void applyRequest(Course course, CourseRequest request) {
        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setStatus(request.getStatus() == null ? CourseStatus.DRAFT : request.getStatus());
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course title is required");
        }
    }
}
