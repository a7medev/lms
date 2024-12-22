package com.lms.Quiz.QuizAnswer.MCQAnswer;

import com.lms.Quiz.QuizAnswer.QuizAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = "mcq_answer")
@Builder
public class MCQAnswer extends QuizAnswer {
    @NotNull
    private int chosenOption;
}
