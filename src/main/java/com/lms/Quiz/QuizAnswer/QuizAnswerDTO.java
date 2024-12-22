package com.lms.Quiz.QuizAnswer;

import lombok.Data;


@Data
public class QuizAnswerDTO {
    private int questionNumber;
    private int chosenOption;
    private String answer;
}
