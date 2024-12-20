package com.lms.QuestionBank.Question.MCQ;

import com.lms.QuestionBank.Question.Question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue(value = "mcq")
@AllArgsConstructor
@NoArgsConstructor
public class MCQ extends Question {
    String option1,option2,option3,option4;
    int CorrectOption;
}
