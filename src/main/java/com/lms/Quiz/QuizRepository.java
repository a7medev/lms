package com.lms.Quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Collection<Quiz> findAllByCourse_CourseId(Long courseId);
    Collection<Quiz> findAllByCourse_CourseIdAndStartDateAfter(Long courseId, LocalDateTime startDate);
    Optional<Quiz> findByQuizIdAndCourse_CourseId(Long quizId, Long courseId);
}
