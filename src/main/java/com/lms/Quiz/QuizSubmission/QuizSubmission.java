package com.lms.Quiz.QuizSubmission;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.Quiz.Quiz;
import com.lms.Quiz.QuizAnswer.QuizAnswer;
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
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long quizSubmissionId;

    @ManyToOne
    @JoinColumn(name = "quizId",nullable = false)
    private Quiz quiz;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "studentId",nullable = false)
    User student;

    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> studentAnswers;

    private int marks;
}
