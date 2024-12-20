package com.lms.QuestionBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.QuestionBank.Question.Question;
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
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionBankId;

    @OneToMany(mappedBy = "questionBank", cascade = CascadeType.ALL)
    Collection<Question> questions;
}
