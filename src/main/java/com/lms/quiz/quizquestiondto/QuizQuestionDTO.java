package com.lms.quiz.quizquestiondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionDTO {
    int questionNumber;
    private String questionTitle;
}
