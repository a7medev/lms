package com.lms.Quiz.QuizSubmission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    Optional<QuizSubmission> findByQuizSubmissionIdAndQuiz_QuizId(long quizSubmissionId, long quizId);
    Optional<QuizSubmission> findByQuiz_QuizIdAndStudent_Id(long quizId, long studentId);
    List<QuizSubmission> findAllByQuiz_QuizId(long quizId);
}
