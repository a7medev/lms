package com.lms.Quiz.QuizSubmission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    Optional<QuizSubmission> findByQuizSubmissionIdAndQuizQuizId(long quizSubmissionId, long quizId);
    Optional<QuizSubmission> findByQuiz_QuizIdAndStudentId(long quizId, long studentId);
    List<QuizSubmission> findAllByQuizQuizId(long quizId);
}
