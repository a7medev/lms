package com.lms.quiz.quizanswer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class QuizAnswerService {
    private QuizAnswerRepository quizAnswerRepository;

    public Collection<QuizAnswer> getStudentAnswers(long quizSubmissionId) {
        return this.quizAnswerRepository.findAllByQuizSubmission_QuizSubmissionId(quizSubmissionId);
    }
    public void addSubmittedAnswer(QuizAnswer quizAnswer) {
        this.quizAnswerRepository.save(quizAnswer);
    }
}
