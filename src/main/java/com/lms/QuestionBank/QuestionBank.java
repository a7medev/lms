package com.lms.QuestionBank;

import com.lms.QuestionBank.Question.Question;
import com.lms.Quiz.Quiz;
import com.lms.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question_bank")
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionBankId;

    @OneToMany(mappedBy = "questionBank", cascade = CascadeType.ALL, orphanRemoval = true)
    Collection<Question> questions;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "quizId")
    private Collection<Quiz> Quizzes;
}
