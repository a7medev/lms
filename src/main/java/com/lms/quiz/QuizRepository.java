package com.lms.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Collection<Quiz> findAllByCourseCourseId(Long courseId);
    Collection<Quiz> findAllByCourseCourseIdAndStartDateAfter(Long courseId, LocalDateTime startDate);
    Optional<Quiz> findByQuizIdAndCourseCourseId(Long quizId, Long courseId);

    Optional<Quiz> findByQuizId(long quizId);
}
