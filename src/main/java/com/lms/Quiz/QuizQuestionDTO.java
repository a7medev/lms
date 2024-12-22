package com.lms.Quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizQuestionDTO {
    int questionNumber;
    private String questionTitle;
}
