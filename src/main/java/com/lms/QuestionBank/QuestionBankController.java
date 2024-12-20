package com.lms.QuestionBank;


import com.lms.QuestionBank.Question.MCQ.MCQ;
import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.Question.ShortAnswerQuestion.ShortAnswerQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/Questions")
public class QuestionBankController {
    private final QuestionBankService questionBankService;

    @Autowired
    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @GetMapping("/{questionBankId}/Question/{id}")
    public Question getQuestion(@PathVariable long questionBankId, @PathVariable long id)
    {
        return questionBankService.getQuestion(id,questionBankId).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND,"Question not found"));
    }
    @GetMapping("/{questionBankId}")
    public Collection<Question> getAllQuestions(@PathVariable long questionBankId)
    {
        return questionBankService.getAllQuestions(questionBankId);
    }
    @GetMapping("{questionBankId}/{Discriminator}")
    public Collection<Question> getAllQuestionsOfType(@PathVariable long questionBankId, @PathVariable String Discriminator)
    {
        return this.questionBankService.getAllQuestionsOfType(Discriminator,questionBankId);
    }
    @PostMapping("")
    public void addQuestionBank(@RequestBody QuestionBank questionBank)
    {
        this.questionBankService.newQuestionBank(questionBank);
    }
    @PostMapping("/{questionBankId}")
    public void addQuestion(@RequestBody com.fasterxml.jackson.databind.node.ObjectNode newQuestion, @PathVariable long questionBankId)
    {
        Question question;
        if(newQuestion.get("question_type").textValue().equals("mcq"))
            question = new MCQ(newQuestion.get("option1").textValue(),newQuestion.get("option2").textValue(),newQuestion.get("option3").textValue(),newQuestion.get("option4").textValue(),newQuestion.get("CorrectOption").intValue());
        else
            question = new ShortAnswerQuestion(newQuestion.get("answer").textValue());

        question.setQuestionTitle(newQuestion.get("questionTitle").textValue());
        this.questionBankService.addQuestion(question,questionBankId);
    }
}
