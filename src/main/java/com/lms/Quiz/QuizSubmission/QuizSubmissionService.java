package com.lms.Quiz.QuizSubmission;


import com.lms.QuestionBank.Question.MCQ.MCQ;
import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.Question.ShortAnswerQuestion.ShortAnswerQuestion;
import com.lms.Quiz.Quiz;
import com.lms.Quiz.QuizAnswer.MCQAnswer.MCQAnswer;
import com.lms.Quiz.QuizAnswer.QuizAnswer;
import com.lms.Quiz.QuizAnswer.QuizAnswerDTO;
import com.lms.Quiz.QuizAnswer.QuizAnswerService;
import com.lms.Quiz.QuizAnswer.ShortAnswer.ShortAnswer;
import com.lms.Quiz.QuizService;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import jakarta.persistence.DiscriminatorValue;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizService quizService;
    private final QuizAnswerService quizAnswerService;
    private final NotificationService notificationService;
    public QuizSubmission getQuizSubmission(long submissionId,long quizId){
        return this.quizSubmissionRepository.findByQuizSubmissionIdAndQuizQuizId(submissionId,quizId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public List<QuizSubmission> getAllQuizSubmissions(long quizId){
        return this.quizSubmissionRepository.findAllByQuizQuizId(quizId);
    }
    public Optional<QuizSubmission> checkIfAttemptedBefore(long studentId, long quizId){
        return this.quizSubmissionRepository.findByQuiz_QuizIdAndStudentId(quizId,studentId);
    }
    public QuizSubmission submitQuiz(long quizId, long courseId, User student, List<QuizAnswerDTO> studAns) {
        Quiz quiz = this.quizService.getQuiz(quizId,courseId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found"));
        QuizSubmission quizSubmission =  QuizSubmission.builder()
                .quiz(quiz)
                .student(student)
                .createdAt(LocalDateTime.now())
                .build();
        List<QuizAnswer> studentAnswers = studAns.stream()
                .map(submittedAnswer -> {
                        QuizAnswer quizAnswer;
                        if(quiz.getQuestions().get(submittedAnswer.getQuestionNumber() - 1).getClass().getAnnotation(DiscriminatorValue.class).value().equals("mcq"))
                            quizAnswer = MCQAnswer.builder()
                                    .chosenOption(submittedAnswer.getChosenOption())
                                    .build();
                        else
                            quizAnswer = ShortAnswer.builder()
                                    .shortAnswer(submittedAnswer.getAnswer())
                                    .build();
                        quizAnswer.setQuestion(quiz.getQuestions().get(submittedAnswer.getQuestionNumber() - 1));
                        return quizAnswer;
                }).toList();
        quizSubmission.setStudentAnswers(studentAnswers);
        for(QuizAnswer studentAns: studentAnswers){
            studentAns.setQuizSubmission(quizSubmission);
        }
        gradeQuiz(quizSubmission,studentAnswers,quiz.getQuestions());
        this.quizSubmissionRepository.save(quizSubmission);
        for(QuizAnswer studentAns: studentAnswers){
            this.quizAnswerService.addSubmittedAnswer(studentAns);
        }
        sendGradingNotification(quizSubmission);
        return quizSubmission;
    }
    private void gradeQuiz(QuizSubmission studentSubmission, List<QuizAnswer> studentAnswers, List<Question> quizQuestions) {
        int score = 0;

        for(QuizAnswer answer : studentAnswers) {
            if (answer instanceof MCQAnswer)
                score += ((MCQ) quizQuestions.stream().filter(question -> question.getQuestionId() == answer.getQuestion().getQuestionId()).findFirst().get()).getCorrectOption() == ((MCQAnswer) answer).getChosenOption() ? 1 : 0;
            else
                score += ((ShortAnswerQuestion) quizQuestions.stream().filter(question -> question.getQuestionId() == answer.getQuestion().getQuestionId()).findFirst().get()).getAnswer().equals(((ShortAnswer) answer).getShortAnswer()) ? 1 : 0;
        }
        studentSubmission.setMarks(score);
    }
    private void sendGradingNotification(QuizSubmission submission) {
        User student = submission.getStudent();
        Quiz quiz = submission.getQuiz();

        String subject = "LMS - Your Quiz Submission has been Graded";
        String message = String.format("You scored %d out of %d in the \"%s\" course quiz.", submission.getMarks(),quiz.getNumberOfQuestions(), quiz.getCourse().getTitle());
        Notification notification = Notification.builder()
                .message(message)
                .user(student)
                .build();

        notificationService.saveNotification(notification, subject);
    }
}
