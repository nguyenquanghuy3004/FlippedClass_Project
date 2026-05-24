package repository;

import entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByQuizId(Long quizId);

    long countByQuizId(Long quizId);

    @Query("SELECT AVG(a.score) FROM QuizAttempt a WHERE a.quiz.id = :quizId")
    BigDecimal averageScoreByQuizId(@Param("quizId") Long quizId);
}
