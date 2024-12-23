package com.lms.quiz.quizanswer;

import lombok.Data;


@Data
public class QuizAnswerDTO {
    private int questionNumber;
    private int chosenOption;
    private String answer;
}
