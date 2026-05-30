package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Hàm tìm profile theo userId (bạn vừa thêm lúc nãy)
    Optional<StudentProfile> findByUserId(Long userId);

    // Hàm kiểm tra trùng lặp mã sinh viên (thêm mới dòng này để fix lỗi AuthServiceImpl)
    boolean existsByStudentCode(String studentCode);

}