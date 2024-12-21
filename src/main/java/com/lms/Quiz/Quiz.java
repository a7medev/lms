package com.lms.Quiz;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.QuestionBank.Question.Question;
import com.lms.QuestionBank.QuestionBank;
import com.lms.Quiz.QuizSubmission.QuizSubmission;
import com.lms.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "quiz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long quizId;

    @NotNull
    private int numberOfQuestions;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "questionBankId",nullable = false)
    private QuestionBank questionBank;

    @JsonIgnore
    @Transient
    private List<Question> questions;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "courseId",nullable = false)
    private Course course;

    @JsonIgnore
    @OneToMany(mappedBy = "quizSubmissionId", cascade = CascadeType.ALL, orphanRemoval = true)
    Collection<QuizSubmission> quizSubmissions;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime creationDate;
}
