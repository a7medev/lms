package com.lms.quiz.quizsubmission;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.quiz.Quiz;
import com.lms.quiz.quizanswer.QuizAnswer;
import com.lms.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "quiz_submission")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long quizSubmissionId;

    @ManyToOne
    @JoinColumn(name = "quizId",nullable = false)
    private Quiz quiz;

    @CreatedBy
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "studentId",nullable = false)
    private User student;

    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> studentAnswers;

    private int marks;
}
