package com.lms.quiz.quizanswer.shortanswer;

import com.lms.quiz.quizanswer.QuizAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value ="short_answer")
@Builder
public class ShortAnswer extends QuizAnswer {
    //@NotNull  Likewise here
    private String shortAnswer;
}
