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


    /*while this sort of defies the ERD diagram we agreed upon,
      I wasnt able to come up with any solution to store the randomly queried questions for a given quiz
      which I will need in order to grade student's submission, because I have no instance of the questions that was queried from the database
      I was left no choice but to query it again which will then result in different questions compared to the ones shown
      another solution is for the same quiz each student will have different model i.e. each student trying to attempt the quiz will trigger
      a query to the database resulting in new questions tho, that ensures there will be no cheating (lol), this will be costly in terms of performance due to multiple
      database hits, and we didnt really talk about that during the documentation in regard to different model per student.
     */
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quizId"),
            inverseJoinColumns = @JoinColumn(name = "questionId"))
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
