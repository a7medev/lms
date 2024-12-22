package com.lms.QuestionBank.Question;

import jakarta.persistence.DiscriminatorValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Collection<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(long id) {
        return questionRepository.findById(id);
    }
    public Optional<Question> getQuestionInQuestionBank(long questionId, long questionBank) {
        return this.questionRepository.findQuestionByQuestionIdAndQuestionBankQuestionBankId(questionId, questionBank);
    }
    public void addQuestion(Question question) {
        questionRepository.save(question);
    }

    public Collection<Question> getQuestionsByQuestionBankId(long questionBankId) {
        return this.questionRepository.findAllByQuestionBankQuestionBankId(questionBankId);
    }
    public List<Question> getNQuestionsByQuestionBankId(int numberOfQuestions, long questionBankId) {
        return this.questionRepository.getRandomQuestions(numberOfQuestions,questionBankId);
    }
    public void deleteQuestion(long questionId,long questionBankId) {
        this.questionRepository.deleteByQuestionIdAndQuestionBankQuestionBankId(questionId,questionBankId);
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
