package com.example.flippedclass.repository;

import com.example.flippedclass.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByQuizId(Long quizId);

    long countByQuizId(Long quizId);

    @Query("SELECT AVG(a.score) FROM QuizAttempt a WHERE a.quiz.id = :quizId")
    Double averageScoreByQuizId(@Param("quizId") Long quizId);

    List<QuizAttempt> findByStudent_Id(Long studentId);

    boolean existsByQuizIdAndStudent_Id(Long quizId, Long studentId);

    @Query("SELECT CASE WHEN COUNT(qa) > 0 THEN true ELSE false END FROM QuizAttempt qa " +
           "WHERE qa.student.id = :studentId " +
           "AND qa.quiz.learningNode.id = :nodeId " +
           "AND qa.score >= :minScore")
    boolean existsPassedAttemptForNode(@Param("studentId") Long studentId,
                                       @Param("nodeId") Long nodeId,
                                       @Param("minScore") BigDecimal minScore);

    @Query("SELECT COALESCE(AVG(qa.score), 0) FROM QuizAttempt qa " +
           "WHERE qa.student.id = :studentId " +
           "AND qa.quiz.learningNode.learningPath.learningSpace.id = :spaceId")
    BigDecimal findAverageScoreByStudentAndSpace(@Param("studentId") Long studentId, @Param("spaceId") Long spaceId);
}
