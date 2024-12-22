package com.lms.Quiz;

import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.QuestionBankService;
import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseService courseService;
    private final QuestionBankService questionBankService;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    public Collection<Quiz> getAllQuizzes(Long courseId, boolean upcoming) {

        if(upcoming){
            LocalDateTime now = LocalDateTime.now();
            return this.quizRepository.findAllByCourseCourseIdAndStartDateAfter(courseId, now);
        }
        return this.quizRepository.findAllByCourseCourseId(courseId);
    }
    public Optional<Quiz> getQuiz(long quizId,long courseId) {
        return this.quizRepository.findByQuizIdAndCourseCourseId(quizId,courseId);
    }
    public List<Question> addQuiz(Quiz quiz, long courseId) {
        Course course = this.courseService.getCourseById(courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found"));
        quiz.setQuestions(this.questionBankService.getNRandomQuestions(quiz.getNumberOfQuestions(),quiz.getQuestionBank().getQuestionBankId()));
        quiz.setCourse(course);
        quiz.setCreationDate(LocalDateTime.now());
        this.sendNewQuizNotification(this.quizRepository.save(quiz));
        return quiz.getQuestions();
    }
    public Collection<QuizQuestionDTO> startQuiz(long quizId,long courseId) {
        Quiz targetedQuiz = this.quizRepository.findByQuizIdAndCourseCourseId(quizId,courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found"));
            if(targetedQuiz.getStartDate().isAfter(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz has not started yet");
        List<QuizQuestionDTO> quiz = targetedQuiz.getQuestions().stream().map(question -> new QuizQuestionDTO(0,question.getQuestionTitle())).toList();
        int i = 1;
        for(QuizQuestionDTO quizQuestionDTO : quiz){
            quizQuestionDTO.setQuestionNumber(i);
            i++;
        }
        return quiz;
    }
    private void sendNewQuizNotification(Quiz quiz) {
        List<Enrollment> activeEnrollments = enrollmentRepository.findAllByCourseCourseIdAndEnrollmentState(
                quiz.getCourse().getCourseId(),
                EnrollmentState.ACTIVE
        );

        String subject = quiz.getCourse().getTitle() + " - new quiz";
        List<Notification> notifications = activeEnrollments.stream()
                .map(enrollment -> {
                    User student = enrollment.getUser();
                    String message = String.format("New \"%s\" course Quiz.",quiz.getCourse().getTitle());
                    return Notification.builder()
                            .message(message)
                            .user(student)
                            .build();
                })
                .toList();

        notificationService.saveNotifications(notifications, subject);
    }
}
