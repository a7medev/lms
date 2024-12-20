package com.lms.QuestionBank;

import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.Question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;

@Service
public class QuestionBankService {
    private final QuestionBankRepository questionBankRepository;
    private final QuestionService questionService;

    @Autowired
    public QuestionBankService(QuestionBankRepository questionBankRepository, QuestionService questionService) {
        this.questionBankRepository = questionBankRepository;
        this.questionService = questionService;
    }
    public Collection<Question> getAllQuestions(long questionBankId){
        return questionService.getQuestionsByQuestionBankId(questionBankId);
    }
    public Optional<Question> getQuestion(long questionId, long questionBankId){
        Optional<Question> question = questionService.getQuestionById(questionId);

        if(question.isPresent() && question.get().getQuestionBank().getQuestionBankId() == questionBankId)
            return question;

        return Optional.empty();
    }
    public void addQuestion(Question question, long questionBankId){
        Optional<QuestionBank> targetedQuestionBank = questionBankRepository.findById(questionBankId);
        if(targetedQuestionBank.isPresent()) {
            question.setQuestionBank(targetedQuestionBank.get());
            this.questionService.addQuestion(question);
            return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question bank not found");
    }
    public void deleteQuestion(long questionId, long questionBankId){
        Optional<Question> question = questionService.getQuestionById(questionId);

        if(question.isPresent() && question.get().getQuestionBank().getQuestionBankId() == questionBankId)
            this.questionService.deleteQuestion(questionId);
    }
    public void newQuestionBank(QuestionBank questionBank){
        this.questionBankRepository.saveAndFlush(questionBank);
    }
    public Collection<Question> getAllQuestionsOfType(String questionType,long questionBankId){
        return this.questionService.getQuestionsByQuestionType(questionType,questionBankId);
    }
}
