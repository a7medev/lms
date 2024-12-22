package com.lms.QuestionBank.Question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.QuestionBank.QuestionBank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;


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

    @NotNull
    private String questionTitle;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "questionBankId",nullable = false)
    private QuestionBank questionBank;
}
