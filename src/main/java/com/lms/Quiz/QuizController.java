package com.lms.Quiz;


import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.QuestionBank;
import com.lms.QuestionBank.QuestionBankService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("courses/{courseId}/quizzes")
@AllArgsConstructor
public class QuizController {
    private QuizService quizService;
    private QuestionBankService questionBankService;
    @GetMapping()
    public Collection<Quiz> getAll(@PathVariable("courseId") long courseId, @RequestParam(defaultValue = "false") boolean upcoming){
        return this.quizService.getAllQuizzes(courseId, upcoming);
    }
    @GetMapping("/{quizId}")
    public Collection<Question> startQuiz(@PathVariable("courseId") long courseId, @PathVariable("quizId") long quizId){
        return this.quizService.startQuiz(quizId, courseId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    public ResponseEntity<?> createQuiz(@PathVariable("courseId") long courseId, @RequestBody QuizDTO newQuiz){
        QuestionBank questionBank = this.questionBankService.getQuestionBank(newQuiz.getQuestionBankId(),courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No question bank found"));

        if(newQuiz.getNumberOfQuestions() > questionBank.getQuestions().size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough questions in the question bank");

        Quiz quiz = Quiz.builder()
                .questionBank(questionBank)
                .creationDate(newQuiz.getCreationDate())
                .startDate(newQuiz.getStartDate())
                .numberOfQuestions(newQuiz.getNumberOfQuestions())
                .build();
        this.quizService.addQuiz(quiz,courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz.getQuestions());
    }

}
