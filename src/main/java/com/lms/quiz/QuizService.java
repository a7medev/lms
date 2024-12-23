package com.lms.quiz;


import com.lms.questionbank.QuestionBankService;
import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.questionbank.question.mcq.MCQ;
import com.lms.quiz.quizanswer.QuizAnswer;
import com.lms.quiz.quizanswer.QuizAnswerService;
import com.lms.quiz.quizanswer.mcqanswer.MCQAnswer;
import com.lms.quiz.quizanswer.shortanswer.ShortAnswer;
import com.lms.quiz.quizquestiondto.QuizMCQDTO;
import com.lms.quiz.quizquestiondto.QuizQuestionDTO;
import com.lms.quiz.quizsubmission.QuizSubmission;
import com.lms.quiz.quizsubmission.QuizSubmissionService;
import com.lms.quiz.quizsubmission.SubmissionState;
import com.lms.user.User;
import jakarta.persistence.DiscriminatorValue;
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
    private final QuizSubmissionService quizSubmissionService;
    private final QuizAnswerService quizAnswerService;
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
    public Quiz addQuiz(Quiz quiz, long courseId) {
        Course course = this.courseService.getCourseById(courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found"));
        quiz.setCourse(course);
        quiz.setCreationDate(LocalDateTime.now());
        this.sendNewQuizNotification(quiz);
        return this.quizRepository.save(quiz);

    }
    public Collection<QuizQuestionDTO> startQuiz(long quizId, long courseId, User student) {
        Quiz targetedQuiz = this.quizRepository.findByQuizIdAndCourseCourseId(quizId,courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found"));
            if(targetedQuiz.getStartDate().isAfter(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz has not started yet");

        if(this.quizSubmissionService.getSubmission(student.getId(), quizId).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz has already started");

        QuizSubmission quizSubmission =  QuizSubmission.builder()
                .quiz(targetedQuiz)
                .student(student)
                .createdAt(LocalDateTime.now())
                .build();
        quizSubmission.setSubmissionState(SubmissionState.ATTEMPTING);
        this.quizSubmissionService.updateQuizSubmission(quizSubmission);
        return this.questionBankService.getNRandomQuestions(targetedQuiz.getNumberOfQuestions(),targetedQuiz.getQuestionBank().getQuestionBankId()).stream().map(question -> {
            QuizQuestionDTO quizQuestionDTO;
            QuizAnswer quizAnswer;
            if(question.getClass().getAnnotation(DiscriminatorValue.class).value().equals("mcq")) {
                quizQuestionDTO = QuizMCQDTO.builder()
                        .option1(((MCQ) question).getOption1())
                        .option2(((MCQ) question).getOption2())
                        .option3(((MCQ) question).getOption3())
                        .option4(((MCQ) question).getOption4())
                        .build();
                quizQuestionDTO.setQuestionTitle(question.getQuestionTitle());
                quizAnswer = MCQAnswer.builder()
                        .build();
            }
            else {
                quizAnswer = ShortAnswer.builder().build();
                quizQuestionDTO = new QuizQuestionDTO();
            }
            quizAnswer.setQuestion(question);
            quizAnswer.setQuizSubmission(quizSubmission);
            this.quizAnswerService.addOrUpdateSubmittedAnswer(quizAnswer);
            quizQuestionDTO.setQuestionTitle(question.getQuestionTitle());
            quizQuestionDTO.setQuestionNumber(question.getQuestionId());
            return quizQuestionDTO;

        }).toList();
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
