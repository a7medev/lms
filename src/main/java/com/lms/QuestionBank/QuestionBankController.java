package com.lms.QuestionBank;


import com.lms.QuestionBank.Question.MCQ.MCQ;
import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.Question.QuestionDTO;
import com.lms.QuestionBank.Question.ShortAnswerQuestion.ShortAnswerQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/courses/{courseId}/questions")
public class QuestionBankController {
    private final QuestionBankService questionBankService;

    @Autowired
    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @GetMapping("/{questionBankId}/question/{id}")
    public Question getQuestion(@PathVariable long questionBankId, @PathVariable long id)
    {
        return questionBankService.getQuestion(id,questionBankId).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND,"Question not found"));
    }
    @GetMapping("/{questionBankId}")
    public Collection<Question> getAllQuestions(@PathVariable long questionBankId)
    {
        return questionBankService.getAllQuestions(questionBankId);
    }
    @GetMapping("{questionBankId}/{discriminator}")
    public Collection<Question> getAllQuestionsOfType(@PathVariable long questionBankId, @PathVariable String discriminator)
    {
        return this.questionBankService.getAllQuestionsOfType(discriminator,questionBankId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<?> addQuestionBank(@RequestBody QuestionBank questionBank,@PathVariable long courseId)
    {
        this.questionBankService.newQuestionBank(questionBank,courseId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("/{questionBankId}")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionDTO newQuestion, @PathVariable long questionBankId)
    {
        Question question;
        if(newQuestion.getQuestionType().equals("mcq"))
            question = MCQ.builder()
                    .option1(newQuestion.getOption1())
                    .option2(newQuestion.getOption2())
                    .option3(newQuestion.getOption3())
                    .option4(newQuestion.getOption4())
                    .correctOption(newQuestion.getCorrectOption())
                    .build();
        else
            question = ShortAnswerQuestion.builder()
                    .answer(newQuestion.getAnswer())
                    .build();

        question.setQuestionTitle(newQuestion.getQuestionTitle());
        this.questionBankService.addQuestion(question,questionBankId);
        return ResponseEntity.ok().build();

    }
    @DeleteMapping("/{questionBankId}/question/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable long questionBankId, @PathVariable long id)
    {
        this.questionBankService.deleteQuestion(id,questionBankId);
        return ResponseEntity.ok().build();
    }
}
