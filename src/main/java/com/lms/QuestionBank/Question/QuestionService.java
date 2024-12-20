package com.lms.QuestionBank.Question;

import jakarta.persistence.DiscriminatorValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    public Collection<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    public Optional<Question> getQuestionById(long id) {
        return questionRepository.findById(id);
    }
    public void addQuestion(Question question) {
        questionRepository.save(question);
    }
    public Collection<Question> getQuestionsByQuestionBankId(long questionBankId) {
        return this.questionRepository.findAllByQuestionBank_QuestionBankId(questionBankId);
    }
    public void deleteQuestion(long id) {
        questionRepository.deleteById(id);
    }
    public void updateQuestion(Question question) {
        questionRepository.save(question);
    }
    public Collection<Question> getQuestionsByQuestionType(String type,long questionBankId) {
        return this.questionRepository.findAll().stream()
                .filter(q -> q.getClass().getAnnotation(DiscriminatorValue.class).value().equals(type) && q.getQuestionBank().getQuestionBankId()==questionBankId)
                .collect(Collectors.toList());
    }
}
