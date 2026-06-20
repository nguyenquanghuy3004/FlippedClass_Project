package com.example.flippedclass.repository;

import com.example.flippedclass.entity.ClassroomActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomActivityRepository extends JpaRepository<ClassroomActivity, Long> {
    List<ClassroomActivity> findByClassroom_Id(Long classroomId);
}
