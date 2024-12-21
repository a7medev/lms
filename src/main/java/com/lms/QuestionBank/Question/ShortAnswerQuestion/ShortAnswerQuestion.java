package com.lms.QuestionBank.Question.ShortAnswerQuestion;

import com.lms.QuestionBank.Question.Question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "short_answer_question")
public class ShortAnswerQuestion extends Question {
    @NotNull
    private String answer;

}
