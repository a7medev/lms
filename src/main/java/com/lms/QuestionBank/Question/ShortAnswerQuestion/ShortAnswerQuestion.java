package com.lms.QuestionBank.Question.ShortAnswerQuestion;

import com.lms.QuestionBank.Question.Question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "short_answer_question")
public class ShortAnswerQuestion extends Question {
    private String answer;

}
