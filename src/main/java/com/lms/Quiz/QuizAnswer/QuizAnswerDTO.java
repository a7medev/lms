package com.lms.Quiz.QuizAnswer;

import lombok.Data;


@Data
public class QuizAnswerDTO {
    int questionNumber;
    int chosenOption;
    String answer;
}
