package com.lms.questionbank.question.mcq;

import com.lms.questionbank.question.Question;
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
    private String option1,option2,option3,option4;

    @NotNull
    private int correctOption;
}
