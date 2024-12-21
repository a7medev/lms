package com.lms.Quiz;

import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.QuestionBankService;
import com.lms.course.Course;
import com.lms.course.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseService courseService;
    private final QuestionBankService questionBankService;
    public Collection<Quiz> getAllQuizzes(Long courseId, boolean upcoming) {

        if(upcoming){
            LocalDateTime now = LocalDateTime.now();
            return this.quizRepository.findAllByCourse_CourseIdAndStartDateAfter(courseId, now);
        }
        return this.quizRepository.findAllByCourse_CourseId(courseId);
    }
    public Optional<Quiz> getQuiz(long quizId,long courseId) {
        return this.quizRepository.findByQuizIdAndCourse_CourseId(quizId,courseId);
    }
    public void addQuiz(Quiz quiz,long courseId) {
        Course course = this.courseService.getCourseById(courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found"));
        quiz.setQuestions(this.questionBankService.getNRandomQuestions(quiz.getNumberOfQuestions(),quiz.getQuestionBank().getQuestionBankId()));
        quiz.setCourse(course);
        quiz.setCreationDate(LocalDateTime.now());
        this.quizRepository.save(quiz);

    }
    public Collection<Question> startQuiz(long quizId,long courseId) {
        Quiz targetedQuiz = this.quizRepository.findByQuizIdAndCourse_CourseId(quizId,courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found"));
            if(targetedQuiz.getStartDate().isAfter(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz has not started yet");

        return targetedQuiz.getQuestions();
    }
}
