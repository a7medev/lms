package com.lms.quiz.quizquestiondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionDTO {
    Long questionNumber;
    private String questionTitle;
}
