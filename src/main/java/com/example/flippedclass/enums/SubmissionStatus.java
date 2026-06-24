package com.example.flippedclass.enums;

public enum SubmissionStatus {
    NOT_SUBMITTED,
    PENDING, // Chờ submit
    SUBMITTED, // Đã submit
//    LATE_SUBMITTED,// Submit muộn z - nếu muốn cho sinh viên nộp muộn
    // bây giờ đang để không submit quá deadline
    GRADED // Đã chấm điểm
}
