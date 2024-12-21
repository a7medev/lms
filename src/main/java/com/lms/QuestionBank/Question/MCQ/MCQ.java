package com.lms.QuestionBank.Question.MCQ;

import com.lms.QuestionBank.Question.Question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue(value = "mcq")
@AllArgsConstructor
@NoArgsConstructor
public class MCQ extends Question {
    @NotNull
    String option1,option2,option3,option4;

    @NotNull
    int correctOption;
}
