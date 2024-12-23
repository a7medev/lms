package com.lms.questionbank.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.questionbank.QuestionBank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Objects;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
@RequiredArgsConstructor
@Table(name="question")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question question)) return false;
        return getQuestionId() == question.getQuestionId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getQuestionId());
    }

    @NotNull
    private String questionTitle;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "questionBankId",nullable = false)
    private QuestionBank questionBank;
}
