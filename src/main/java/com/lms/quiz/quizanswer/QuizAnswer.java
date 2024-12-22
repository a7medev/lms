package com.lms.quiz.quizanswer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.questionbank.question.Question;
import com.lms.quiz.quizsubmission.QuizSubmission;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "quiz_answer")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long quizAnswerId;

    @ManyToOne
    @JoinColumn(name = "questionId",nullable = false)
    private Question question;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "quizSubmissionId",nullable = false)
    private QuizSubmission quizSubmission;
}
