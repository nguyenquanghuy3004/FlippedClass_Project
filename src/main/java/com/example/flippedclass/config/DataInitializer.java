package com.example.flippedclass.config;

import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;

import com.example.flippedclass.enums.AuthProvider;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.InviteCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.flippedclass.entity.EvaluationSession;
import com.example.flippedclass.entity.EvaluationCriterion;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.InteractionLog;
import com.example.flippedclass.entity.GradeEntry;
import com.example.flippedclass.repository.EvaluationSessionRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.repository.InteractionLogRepository;
import com.example.flippedclass.repository.GradeEntryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.enums.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LearningSpaceRepository learningSpaceRepository;

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private EvaluationSessionRepository evaluationSessionRepository;

    @Autowired
    private InteractionLogRepository interactionLogRepository;

    @Autowired
    private GradeEntryRepository gradeEntryRepository;

    @Autowired
    private LearningSpaceMemberRepository learningSpaceMemberRepository;

    @Autowired
    private LearningNodeRepository learningNodeRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;

    @Value("${flippedclass.app.initialPassword}")
    private String initialPassword;

    @Override
    public void run(String... args) throws Exception {

        // Khởi tạo các Role nếu chưa tồn tại
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }


        // Tự động sửa lỗi mật khẩu chưa được mã hoá (do chạy script SQL raw)
        List<User> allUsers = userRepository.findAll();
        boolean hasFixedPasswords = false;
        for (User u : allUsers) {
            if (u.getPassword() != null && !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("123456"));
                userRepository.save(u);
                hasFixedPasswords = true;
            }
        }
        if (hasFixedPasswords) {
            System.out.println(">>> Đã tự động Fix lỗi mã hoá BCrypt cho các User! Mật khẩu hiện tại là: 123456");
        }

        // Tài khoản ADMIN hệ thống (chỉ có role ADMIN)
        if (userRepository.countByRolesName(RoleName.ADMIN) == 0) {
            seedAdmin("admin@system.vn", "admin", "System Admin", initialPassword);
        }

        // Tài khoản MENTOR mẫu (chỉ có role MENTOR)
        if (userRepository.countByRolesName(RoleName.MENTOR) == 0) {
            seedMentor("giangvien@fpt.edu.vn", "giangvien", "Thay Nguyen Van A", initialPassword);
        }

        // Tài khoản STUDENT mẫu (chỉ có role STUDENT)
        if (userRepository.countByRolesName(RoleName.STUDENT) == 0) {
            seedStudent("sinhvien@fpt.edu.vn", "SE123456", "Nguyen Van Sinh Vien", initialPassword);
        }

        // Seed Sample Evaluation Data
        seedEvaluationData();
    }


    // Tạo tài khoản Admin hệ thống (chỉ có role ADMIN)
    private void seedAdmin(String email, String username, String fullName, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = buildBaseUser(email, username, fullName, password);

            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));
            user.setRoles(new HashSet<>(Collections.singleton(adminRole)));

            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan ADMIN: " + email);
        }
    }

    // Tạo tài khoản Mentor mẫu (chỉ có role MENTOR)
    private void seedMentor(String email, String username, String fullName, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = buildBaseUser(email, username, fullName, password);

            Role mentorRole = roleRepository.findByName(RoleName.MENTOR)
                    .orElseThrow(() -> new RuntimeException("Role MENTOR not found."));
            user.setRoles(new HashSet<>(Collections.singleton(mentorRole)));

            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan MENTOR: " + email);
        }
    }

    // Tạo tài khoản Student mẫu (chỉ có role STUDENT)
    private void seedStudent(String email, String username, String fullName, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = buildBaseUser(email, username, fullName, password);

            Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Role STUDENT not found."));
            user.setRoles(new HashSet<>(Collections.singleton(studentRole)));

            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan STUDENT: " + email);
        }
    }

    // Helper: tạo User cơ bản (chưa có role)
    private User buildBaseUser(String email, String username, String fullName, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider(AuthProvider.LOCAL);
        user.setAvatarUrl("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+") + "&background=random");
        return user;
    }

    private void seedEvaluationData() {
        if (evaluationSessionRepository.count() > 0) return;

        User mentor = userRepository.findByEmail("giangvien@fpt.edu.vn").orElse(null);
        if (mentor == null) return;

        // Create dummy Learning Space
        LearningSpace space = LearningSpace.builder()
                .name("PRJ301 - Java Web Development")
                .description("Sample Space for PRJ301")
                .owner(mentor)
                .inviteCode(inviteCodeGenerator.generateUniqueInviteCode())
                .build();
        space = learningSpaceRepository.save(space);

        // Create dummy Learning Path
        LearningPath path = LearningPath.builder()
                .title("Module 1: Servlets and JSP")
                .description("Introduction to Java Web")
                .learningSpace(space)
                .lecturer(mentor)
                .build();
        path = learningPathRepository.save(path);

        // Create Evaluation Session 1
        EvaluationSession session1 = EvaluationSession.builder()
                .title("Midterm Assessment")
                .learningPath(path)
                .lecturer(mentor)
                .gradingStartAt(LocalDateTime.now().minusDays(1))
                .gradingDeadlineAt(LocalDateTime.now().plusDays(7))
                .build();

        EvaluationCriterion c1 = EvaluationCriterion.builder()
                .name("Class Participation")
                .description("Active involvement during Q&A")
                .maxScore(new BigDecimal("10.0"))
                .sortOrder(1)
                .session(session1)
                .build();

        EvaluationCriterion c2 = EvaluationCriterion.builder()
                .name("Homework Assignment 1")
                .description("Completion of pre-class exercises")
                .maxScore(new BigDecimal("10.0"))
                .sortOrder(2)
                .session(session1)
                .build();

        session1.getCriteria().addAll(Arrays.asList(c1, c2));
        evaluationSessionRepository.save(session1);

        // Create Evaluation Session 2
        EvaluationSession session2 = EvaluationSession.builder()
                .title("Final Project Review")
                .learningPath(path)
                .lecturer(mentor)
                .gradingStartAt(LocalDateTime.now().plusDays(5))
                .gradingDeadlineAt(LocalDateTime.now().plusDays(14))
                .build();

        EvaluationCriterion c3 = EvaluationCriterion.builder()
                .name("Code Quality")
                .description("Clean code principles applied")
                .maxScore(new BigDecimal("40.0"))
                .sortOrder(1)
                .session(session2)
                .build();

        EvaluationCriterion c4 = EvaluationCriterion.builder()
                .name("Presentation")
                .description("Clarity and demonstration")
                .maxScore(new BigDecimal("60.0"))
                .sortOrder(2)
                .session(session2)
                .build();

        session2.getCriteria().addAll(Arrays.asList(c3, c4));
        session2 = evaluationSessionRepository.save(session2);

        // Seed Logs and Grades for Student
        User student = userRepository.findByUsername("SE123456").orElse(null);
        if (student != null) {
            // Logs
            InteractionLog log1 = InteractionLog.builder()
                    .student(student)
                    .learningPath(path)
                    .interactionType("HOME_STUDY")
                    .summary("Watched Chapter 1 Video completely")
                    .occurredAt(LocalDateTime.now().minusDays(3))
                    .build();

            InteractionLog log2 = InteractionLog.builder()
                    .student(student)
                    .learningPath(path)
                    .interactionType("CLASS_PARTICIPATION")
                    .summary("Answered the instructor's question perfectly")
                    .occurredAt(LocalDateTime.now().minusDays(1))
                    .build();
            interactionLogRepository.saveAll(Arrays.asList(log1, log2));

            // Grade
            GradeEntry grade = GradeEntry.builder()
                    .session(session1)
                    .criterion(session1.getCriteria().get(0))
                    .student(student)
                    .lecturer(mentor)
                    .score(new BigDecimal("9.5"))
                    .comment("Excellent participation in the midterm phase.")
                    .build();
            gradeEntryRepository.save(grade);

            // 1. Enroll Student into Learning Space
            if (learningSpaceMemberRepository.count() == 0) {
                LearningSpaceMember member = LearningSpaceMember.builder()
                        .learningSpace(space)
                        .user(student)
                        .role(MemberRole.MEMBER)
                        .status(MemberStatus.ACTIVE)
                        .joinedAt(LocalDateTime.now())
                        .build();
                learningSpaceMemberRepository.save(member);
            }

            // 2. Create a Learning Node in the Path
            if (learningNodeRepository.count() == 0) {
                LearningNode node = LearningNode.builder()
                        .learningPath(path)
                        .title("Chapter 1: Servlets Basics")
                        .description("Understanding the lifecycle of Servlets")
                        .nodeType("LESSON")
                        .displayOrder(1)
                        .build();
                node = learningNodeRepository.save(node);

                // 3. Create a Quiz attached to the Node
                Quiz quiz = Quiz.builder()
                        .learningNode(node)
                        .lecturer(mentor)
                        .title("Midterm Assessment - Servlets")
                        .description("Test your knowledge of Java Servlets")
                        .durationMinutes(30)
                        .passScore(50)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build();
                quiz = quizRepository.save(quiz);

                // 4. Create Quiz Questions
                QuizQuestion q1 = QuizQuestion.builder()
                        .quiz(quiz)
                        .content("Which of the following is NOT a phase of the Servlet lifecycle?")
                        .questionType("SINGLE_CHOICE")
                        .options("[\"Initialization (init)\", \"Execution (service)\", \"Suspension (suspend)\", \"Destruction (destroy)\"]")
                        .correctAnswer("Suspension (suspend)")
                        .points(10)
                        .sortOrder(1)
                        .build();

                QuizQuestion q2 = QuizQuestion.builder()
                        .quiz(quiz)
                        .content("What is the default HTTP method handled by a basic HTML form?")
                        .questionType("SINGLE_CHOICE")
                        .options("[\"POST\", \"GET\", \"PUT\", \"DELETE\"]")
                        .correctAnswer("GET")
                        .points(10)
                        .sortOrder(2)
                        .build();

                QuizQuestion q3 = QuizQuestion.builder()
                        .quiz(quiz)
                        .content("The HttpServletResponse interface provides methods to handle which of the following?")
                        .questionType("SINGLE_CHOICE")
                        .options("[\"Sending HTTP headers\", \"Writing data to the response body\", \"Managing cookies\", \"All of the above\"]")
                        .correctAnswer("All of the above")
                        .points(10)
                        .sortOrder(3)
                        .build();

                quizQuestionRepository.saveAll(Arrays.asList(q1, q2, q3));
            }
        }

        System.out.println(">>> Da khoi tao du lieu mau cho Evaluation Sessions, Quizzes & Logs.");
    }
}
