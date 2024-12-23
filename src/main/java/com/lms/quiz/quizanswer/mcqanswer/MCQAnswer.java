package com.lms.quiz.quizanswer.mcqanswer;

import com.lms.quiz.quizanswer.QuizAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = "mcq_answer")
@Builder
public class MCQAnswer extends QuizAnswer {
    //@NotNull  Since the answer will be determined later
    private int chosenOption;
}
