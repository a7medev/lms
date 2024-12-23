package com.lms.quiz.quizsubmission;



import com.lms.questionbank.question.mcq.MCQ;
import com.lms.questionbank.question.shortanswerquestion.ShortAnswerQuestion;
import com.lms.quiz.Quiz;
import com.lms.quiz.quizanswer.mcqanswer.MCQAnswer;
import com.lms.quiz.quizanswer.QuizAnswer;
import com.lms.quiz.quizanswer.QuizAnswerDTO;
import com.lms.quiz.quizanswer.QuizAnswerService;
import com.lms.quiz.quizanswer.shortanswer.ShortAnswer;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizAnswerService quizAnswerService;
    private final NotificationService notificationService;
    public QuizSubmission getQuizSubmission(long submissionId,long quizId){
        return this.quizSubmissionRepository.findByQuizSubmissionIdAndQuizQuizId(submissionId,quizId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public List<QuizSubmission> getAllQuizSubmissions(long quizId){
        return this.quizSubmissionRepository.findAllByQuizQuizId(quizId);
    }
    public Optional<QuizSubmission> getSubmission(long studentId, long quizId){
        return this.quizSubmissionRepository.findByQuizQuizIdAndStudentId(quizId,studentId);
    }
    public void updateQuizSubmission(QuizSubmission quizSubmission){
        this.quizSubmissionRepository.save(quizSubmission);
    }
    public QuizSubmission submitQuiz(long quizId, User student, List<QuizAnswerDTO> studAns) {
        QuizSubmission quizSubmission = this.quizSubmissionRepository.findByQuizQuizIdAndStudentId(quizId,student.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        HashSet<QuizAnswer> actualAnswers = new HashSet<>(quizAnswerService.getStudentAnswers(quizSubmission.getQuizSubmissionId()));
        studAns.forEach(answerDTO -> {
               Optional<QuizAnswer> answer =  actualAnswers.stream().filter(actualAnswer -> actualAnswer.getQuestion().getQuestionId() == answerDTO.getQuestionNumber()).findFirst();
               if (answer.isPresent()) {
                   if(answer.get() instanceof ShortAnswer)
                       ((ShortAnswer)answer.get()).setShortAnswer(answerDTO.getAnswer());
                   else
                       ((MCQAnswer)answer.get()).setChosenOption(answerDTO.getChosenOption());
                   this.quizAnswerService.addOrUpdateSubmittedAnswer(answer.get());
               }
               
        });
        quizSubmission.setStudentAnswers(actualAnswers);
        gradeQuiz(quizSubmission,actualAnswers);
        quizSubmission.setSubmissionState(SubmissionState.SUBMITTED);
        this.quizSubmissionRepository.save(quizSubmission);
        sendGradingNotification(quizSubmission);
        return quizSubmission;
    }
    private void gradeQuiz(QuizSubmission studentSubmission, Collection<QuizAnswer> studentAnswers) {
        int score = 0;

        for(QuizAnswer answer : studentAnswers) {
            if (answer instanceof MCQAnswer)
                score += ((MCQ)answer.getQuestion()).getCorrectOption() == ((MCQAnswer) answer).getChosenOption() ? 1 : 0;
            else
                score += ((ShortAnswerQuestion)answer.getQuestion()).getAnswer().equals(((ShortAnswer) answer).getShortAnswer()) ? 1 : 0;
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
