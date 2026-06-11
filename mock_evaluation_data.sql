-- Thêm Roles (Nếu chưa có)
SET IDENTITY_INSERT roles ON;
IF NOT EXISTS (SELECT 1 FROM roles WHERE id = 1) INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
IF NOT EXISTS (SELECT 1 FROM roles WHERE id = 2) INSERT INTO roles (id, name) VALUES (2, 'MENTOR'); -- Lecturer
IF NOT EXISTS (SELECT 1 FROM roles WHERE id = 3) INSERT INTO roles (id, name) VALUES (3, 'STUDENT');
SET IDENTITY_INSERT roles OFF;

-- Thêm Users (Giảng viên và Sinh viên)
SET IDENTITY_INSERT users ON;
-- Giảng viên (ID: 1)
IF NOT EXISTS (SELECT 1 FROM users WHERE id = 1)
    INSERT INTO users (id, username, email, full_name, provider, created_at, updated_at) 
    VALUES (1, 'lecturer1', 'lecturer1@example.com', 'Nguyen Van Giang Vien', 'LOCAL', GETDATE(), GETDATE());

-- Sinh viên (ID: 2)
IF NOT EXISTS (SELECT 1 FROM users WHERE id = 2)
    INSERT INTO users (id, username, email, full_name, provider, created_at, updated_at) 
    VALUES (2, 'student1', 'student1@example.com', 'Tran Thi Sinh Vien', 'LOCAL', GETDATE(), GETDATE());
SET IDENTITY_INSERT users OFF;

-- Thêm User Roles
IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = 1 AND role_id = 2)
    INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = 2 AND role_id = 3)
    INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);

-- Thêm Learning Space
SET IDENTITY_INSERT learning_spaces ON;
IF NOT EXISTS (SELECT 1 FROM learning_spaces WHERE id = 1)
    INSERT INTO learning_spaces (id, owner_id, name, invite_code, visibility, status, created_at, updated_at)
    VALUES (1, 1, 'Web Programming Fall 2026', 'INVITE_WEB2026', 'PUBLIC', 'ACTIVE', GETDATE(), GETDATE());
SET IDENTITY_INSERT learning_spaces OFF;

-- Thêm Learning Space Member
IF NOT EXISTS (SELECT 1 FROM learning_space_members WHERE user_id = 2 AND learning_space_id = 1)
    INSERT INTO learning_space_members (learning_space_id, user_id, role, status, joined_at)
    VALUES (1, 2, 'MEMBER', 'ACTIVE', GETDATE());

-- Thêm Learning Path (Môn học)
SET IDENTITY_INSERT learning_paths ON;
IF NOT EXISTS (SELECT 1 FROM learning_paths WHERE id = 1)
    INSERT INTO learning_paths (id, learning_space_id, lecturer_id, title, description, status, visibility, position, estimated_duration_hours, created_at, updated_at)
    VALUES (1, 1, 1, 'Lập trình Web Frontend', 'HTML, CSS, JS cơ bản và nâng cao', 'ACTIVE', 'PUBLIC', 1, 40, GETDATE(), GETDATE());
SET IDENTITY_INSERT learning_paths OFF;

-- Thêm Evaluation Session (Đợt đánh giá)
SET IDENTITY_INSERT evaluation_sessions ON;
IF NOT EXISTS (SELECT 1 FROM evaluation_sessions WHERE id = 1)
    INSERT INTO evaluation_sessions (id, learning_path_id, lecturer_id, title, grading_start_at, grading_deadline_at, created_at)
    VALUES (1, 1, 1, N'Đánh giá giữa kỳ - Tuần 8', GETDATE(), DATEADD(day, 7, GETDATE()), GETDATE());
SET IDENTITY_INSERT evaluation_sessions OFF;

-- Thêm Evaluation Criteria (Các tiêu chí)
SET IDENTITY_INSERT evaluation_criteria ON;
IF NOT EXISTS (SELECT 1 FROM evaluation_criteria WHERE id = 1)
    INSERT INTO evaluation_criteria (id, session_id, name, description, max_score, sort_order)
    VALUES (1, 1, N'Tham gia phát biểu trong lớp', N'Tích cực trả lời câu hỏi và thảo luận', 3.00, 1);

IF NOT EXISTS (SELECT 1 FROM evaluation_criteria WHERE id = 2)
    INSERT INTO evaluation_criteria (id, session_id, name, description, max_score, sort_order)
    VALUES (2, 1, N'Hoàn thành bài tập về nhà', N'Làm đủ và đúng các bài tập trên hệ thống', 4.00, 2);

IF NOT EXISTS (SELECT 1 FROM evaluation_criteria WHERE id = 3)
    INSERT INTO evaluation_criteria (id, session_id, name, description, max_score, sort_order)
    VALUES (3, 1, N'Thái độ học tập', N'Đi học đầy đủ, không vi phạm nội quy', 3.00, 3);
SET IDENTITY_INSERT evaluation_criteria OFF;

-- Thêm Interaction Logs (Lịch sử tương tác của sinh viên)
SET IDENTITY_INSERT interaction_logs ON;
INSERT INTO interaction_logs (id, student_id, learning_path_id, interaction_type, summary, occurred_at)
VALUES 
    (1, 2, 1, 'HOME_STUDY', N'Đã xem video bài 5: DOM Manipulation', DATEADD(day, -2, GETDATE())),
    (2, 2, 1, 'HOME_STUDY', N'Đọc tài liệu chương 3', DATEADD(day, -2, GETDATE())),
    (3, 2, 1, 'CLASS_PARTICIPATION', N'Phát biểu 2 lần trong giờ học', DATEADD(day, -1, GETDATE())),
    (4, 2, 1, 'CLASS_PARTICIPATION', N'Trả lời đúng câu hỏi tình huống về Event Listener', DATEADD(day, -1, GETDATE()));
SET IDENTITY_INSERT interaction_logs OFF;

-- Thêm Grade Entries (Điểm đã chấm thử)
SET IDENTITY_INSERT grade_entries ON;
INSERT INTO grade_entries (id, session_id, criterion_id, student_id, lecturer_id, score, comment, graded_at)
VALUES 
    (1, 1, 1, 2, 1, 2.50, N'Tích cực phát biểu nhưng đôi khi chưa chính xác hoàn toàn', GETDATE());
SET IDENTITY_INSERT grade_entries OFF;
