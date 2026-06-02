-- T-SQL Script to insert mock data for FlippedClass project
-- Chạy đoạn script này trên SQL Server để bơm dữ liệu giả lập

DECLARE @lecturer_id BIGINT;
DECLARE @student1_id BIGINT;
DECLARE @student2_id BIGINT;
DECLARE @student3_id BIGINT;

DECLARE @space1_id BIGINT;
DECLARE @space2_id BIGINT;

DECLARE @path1_id BIGINT;
DECLARE @path2_id BIGINT;

DECLARE @node1_id BIGINT;
DECLARE @node2_id BIGINT;
DECLARE @node3_id BIGINT;

DECLARE @quiz1_id BIGINT;
DECLARE @quiz2_id BIGINT;
DECLARE @quiz3_id BIGINT;

-- ==========================================
-- 1. INSERT LECTURER & STUDENTS
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'lecturer_mock@test.com')
BEGIN
    INSERT INTO users (email, full_name, password, created_at, updated_at)
    VALUES ('lecturer_mock@test.com', 'John Doe Lecturer', 'password123', GETDATE(), GETDATE());
    SET @lecturer_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @lecturer_id = id FROM users WHERE email = 'lecturer_mock@test.com';
END

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'student1@test.com')
BEGIN
    INSERT INTO users (email, full_name, password, created_at, updated_at)
    VALUES ('student1@test.com', 'Alice Student', '123', GETDATE(), GETDATE());
    SET @student1_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @student1_id = id FROM users WHERE email = 'student1@test.com';
END

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'student2@test.com')
BEGIN
    INSERT INTO users (email, full_name, password, created_at, updated_at)
    VALUES ('student2@test.com', 'Bob Student', '123', GETDATE(), GETDATE());
    SET @student2_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @student2_id = id FROM users WHERE email = 'student2@test.com';
END

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'student3@test.com')
BEGIN
    INSERT INTO users (email, full_name, password, created_at, updated_at)
    VALUES ('student3@test.com', 'Charlie Student', '123', GETDATE(), GETDATE());
    SET @student3_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @student3_id = id FROM users WHERE email = 'student3@test.com';
END

-- ==========================================
-- 2. INSERT LEARNING SPACES & MEMBERS
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM learning_spaces WHERE invite_code = 'BACK-MOCK')
BEGIN
    INSERT INTO learning_spaces (name, description, owner_id, invite_code, status, visibility, created_at, updated_at)
    VALUES ('Backend Development Mastery', 'Learn all about Spring Boot and Java.', @lecturer_id, 'BACK-MOCK', 'ACTIVE', 'PRIVATE', GETDATE(), GETDATE());
    SET @space1_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @space1_id = id FROM learning_spaces WHERE invite_code = 'BACK-MOCK';
END

IF NOT EXISTS (SELECT 1 FROM learning_spaces WHERE invite_code = 'FRONT-MOCK')
BEGIN
    INSERT INTO learning_spaces (name, description, owner_id, invite_code, status, visibility, created_at, updated_at)
    VALUES ('Frontend React Bootcamp', 'Master modern frontend web development.', @lecturer_id, 'FRONT-MOCK', 'ACTIVE', 'PRIVATE', GETDATE(), GETDATE());
    SET @space2_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @space2_id = id FROM learning_spaces WHERE invite_code = 'FRONT-MOCK';
END

-- Add students to spaces (IGNORE IF EXISTS)
BEGIN TRY
    INSERT INTO learning_space_members (learning_space_id, user_id, role, status, joined_at) VALUES 
    (@space1_id, @student1_id, 'MEMBER', 'ACTIVE', GETDATE()),
    (@space1_id, @student2_id, 'MEMBER', 'ACTIVE', GETDATE()),
    (@space2_id, @student2_id, 'MEMBER', 'ACTIVE', GETDATE()),
    (@space2_id, @student3_id, 'MEMBER', 'ACTIVE', GETDATE());
END TRY
BEGIN CATCH
    -- Ignore duplicate key errors for members
END CATCH

-- ==========================================
-- 3. INSERT LEARNING PATHS
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM learning_paths WHERE title = 'Spring Boot Core' AND learning_space_id = @space1_id)
BEGIN
    INSERT INTO learning_paths (learning_space_id, lecturer_id, title, description, status, position, estimated_duration_hours, visibility, created_at, updated_at)
    VALUES (@space1_id, @lecturer_id, 'Spring Boot Core', 'Core concepts of Spring Boot', 'ACTIVE', 1, 10, 'PRIVATE', GETDATE(), GETDATE());
    SET @path1_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @path1_id = id FROM learning_paths WHERE title = 'Spring Boot Core' AND learning_space_id = @space1_id;
END

IF NOT EXISTS (SELECT 1 FROM learning_paths WHERE title = 'React Hooks' AND learning_space_id = @space2_id)
BEGIN
    INSERT INTO learning_paths (learning_space_id, lecturer_id, title, description, status, position, estimated_duration_hours, visibility, created_at, updated_at)
    VALUES (@space2_id, @lecturer_id, 'React Hooks', 'Deep dive into React Hooks', 'ACTIVE', 1, 15, 'PRIVATE', GETDATE(), GETDATE());
    SET @path2_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @path2_id = id FROM learning_paths WHERE title = 'React Hooks' AND learning_space_id = @space2_id;
END

-- ==========================================
-- 4. INSERT LEARNING NODES
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM learning_nodes WHERE title = 'Dependency Injection basics' AND learning_path_id = @path1_id)
BEGIN
    INSERT INTO learning_nodes (learning_path_id, title, description, content, node_type, status, display_order, estimated_minutes, created_at, updated_at)
    VALUES (@path1_id, 'Dependency Injection basics', 'DI fundamentals', '', 'LESSON', 'ACTIVE', 1, 30, GETDATE(), GETDATE());
    SET @node1_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @node1_id = id FROM learning_nodes WHERE title = 'Dependency Injection basics' AND learning_path_id = @path1_id;
END

IF NOT EXISTS (SELECT 1 FROM learning_nodes WHERE title = 'JPA and Hibernate' AND learning_path_id = @path1_id)
BEGIN
    INSERT INTO learning_nodes (learning_path_id, title, description, content, node_type, status, display_order, estimated_minutes, created_at, updated_at)
    VALUES (@path1_id, 'JPA and Hibernate', 'ORM mapping', '', 'LESSON', 'ACTIVE', 2, 45, GETDATE(), GETDATE());
    SET @node2_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @node2_id = id FROM learning_nodes WHERE title = 'JPA and Hibernate' AND learning_path_id = @path1_id;
END

IF NOT EXISTS (SELECT 1 FROM learning_nodes WHERE title = 'UseState & UseEffect' AND learning_path_id = @path2_id)
BEGIN
    INSERT INTO learning_nodes (learning_path_id, title, description, content, node_type, status, display_order, estimated_minutes, created_at, updated_at)
    VALUES (@path2_id, 'UseState & UseEffect', 'React states', '', 'LESSON', 'ACTIVE', 1, 60, GETDATE(), GETDATE());
    SET @node3_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @node3_id = id FROM learning_nodes WHERE title = 'UseState & UseEffect' AND learning_path_id = @path2_id;
END

-- ==========================================
-- 5. INSERT QUIZZES
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM quizzes WHERE title = 'Spring Core Assessment' AND learning_node_id = @node1_id)
BEGIN
    INSERT INTO quizzes (learning_node_id, lecturer_id, title, description, duration_minutes, active, pass_score, difficulty, created_at)
    VALUES (@node1_id, @lecturer_id, 'Spring Core Assessment', 'Test your knowledge on Spring Beans and Dependency Injection.', 30, 1, 50, 'EASY', GETDATE());
    SET @quiz1_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @quiz1_id = id FROM quizzes WHERE title = 'Spring Core Assessment' AND learning_node_id = @node1_id;
END

IF NOT EXISTS (SELECT 1 FROM quizzes WHERE title = 'Advanced JPA Quiz' AND learning_node_id = @node2_id)
BEGIN
    INSERT INTO quizzes (learning_node_id, lecturer_id, title, description, duration_minutes, active, pass_score, difficulty, created_at)
    VALUES (@node2_id, @lecturer_id, 'Advanced JPA Quiz', 'Complex joins and relationships in Hibernate.', 45, 0, 60, 'HARD', GETDATE());
    SET @quiz2_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @quiz2_id = id FROM quizzes WHERE title = 'Advanced JPA Quiz' AND learning_node_id = @node2_id;
END

IF NOT EXISTS (SELECT 1 FROM quizzes WHERE title = 'React Fundamentals Quiz' AND learning_node_id = @node3_id)
BEGIN
    INSERT INTO quizzes (learning_node_id, lecturer_id, title, description, duration_minutes, active, pass_score, difficulty, created_at)
    VALUES (@node3_id, @lecturer_id, 'React Fundamentals Quiz', 'Hooks, state, and lifecycle basics.', 20, 1, 70, 'MEDIUM', GETDATE());
    SET @quiz3_id = SCOPE_IDENTITY();
END
ELSE
BEGIN
    SELECT @quiz3_id = id FROM quizzes WHERE title = 'React Fundamentals Quiz' AND learning_node_id = @node3_id;
END

-- ==========================================
-- 6. INSERT QUIZ ATTEMPTS (SCORES)
-- ==========================================
BEGIN TRY
    -- Quiz 1 (Spring Core) - Pass score 50
    INSERT INTO quiz_attempts (quiz_id, student_id, score, total_questions, correct_answers, started_at, submitted_at) VALUES 
    (@quiz1_id, @student1_id, 85.50, 10, 8, GETDATE(), GETDATE()), -- Alice (Pass, Good)
    (@quiz1_id, @student2_id, 45.00, 10, 4, GETDATE(), GETDATE()); -- Bob (Fail)

    -- Quiz 2 (Advanced JPA) - Pass score 60
    INSERT INTO quiz_attempts (quiz_id, student_id, score, total_questions, correct_answers, started_at, submitted_at) VALUES 
    (@quiz2_id, @student1_id, 95.00, 20, 19, GETDATE(), GETDATE()); -- Alice (Pass, Excellent)

    -- Quiz 3 (React Fundamentals) - Pass score 70
    INSERT INTO quiz_attempts (quiz_id, student_id, score, total_questions, correct_answers, started_at, submitted_at) VALUES 
    (@quiz3_id, @student2_id, 75.00, 15, 11, GETDATE(), GETDATE()), -- Bob (Pass, Fair)
    (@quiz3_id, @student3_id, 30.00, 15, 4, GETDATE(), GETDATE());  -- Charlie (Fail, Needs attention)
END TRY
BEGIN CATCH
    -- Ignore duplicate attempts if run multiple times
END CATCH

PRINT 'Mock data processing complete! No foreign key conflicts should occur now.';
