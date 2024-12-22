package com.lms.quiz.quizquestiondto;


import lombok.*;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizMCQDTO extends QuizQuestionDTO {
    private String option1, option2, option3, option4;
}
