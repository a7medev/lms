package com.lms.quiz.quizanswer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    Collection<QuizAnswer>  findAllByQuizSubmission_QuizSubmissionId(long quizSubmissionId);
}
