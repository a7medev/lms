package com.lms.QuestionBank;

import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.Question.QuestionService;
import com.lms.course.Course;
import com.lms.course.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuestionBankService {
    private final QuestionBankRepository questionBankRepository;
    private final QuestionService questionService;
    private final CourseService courseService;

    public Collection<Question> getAllQuestions(long questionBankId){
        return questionService.getQuestionsByQuestionBankId(questionBankId);
    }
    public List<Question> getNRandomQuestions(int n, long questionBankId){
        return this.questionService.getNQuestionsByQuestionBankId(n,questionBankId);
    }
    public Optional<QuestionBank> getQuestionBank(long questionBankId,long  courseId)
    {
        return this.questionBankRepository.findByQuestionBankIdAndCourseCourseId(questionBankId,courseId);
    }
    public Optional<Question> getQuestion(long questionId, long questionBankId){
        return this.questionService.getQuestionInQuestionBank(questionId,questionBankId);
    }
    public void addQuestion(Question question, long questionBankId){
        QuestionBank targetedQuestionBank = this.questionBankRepository.findById(questionBankId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Question bank not found"));
        question.setQuestionBank(targetedQuestionBank);
        this.questionService.addQuestion(question);
    }
    public void deleteQuestion(long questionId, long questionBankId){
            this.questionService.deleteQuestion(questionId, questionBankId);
    }
    public void newQuestionBank(QuestionBank questionBank,long courseId){
        Course targetedCourse = this.courseService.getCourseById(courseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        questionBank.setCourse(targetedCourse);
        this.questionBankRepository.saveAndFlush(questionBank);
    }
    public Collection<Question> getAllQuestionsOfType(String questionType,long questionBankId){
        return this.questionService.getQuestionsByQuestionType(questionType,questionBankId);
    }
}
