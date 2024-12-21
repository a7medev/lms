package com.lms.Quiz.QuizAnswer.ShortAnswer;

import com.lms.Quiz.QuizAnswer.QuizAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value ="short_answer")
@Builder
public class ShortAnswer extends QuizAnswer {
    @NotNull
    private String shortAnswer;
}
