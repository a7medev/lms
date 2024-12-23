package com.lms.questionbank.question.shortanswerquestion;

import com.lms.questionbank.question.Question;
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
