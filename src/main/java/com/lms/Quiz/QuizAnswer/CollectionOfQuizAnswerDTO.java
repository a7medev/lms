package com.lms.Quiz.QuizAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class CollectionOfQuizAnswerDTO {
    private List<QuizAnswerDTO> submittedAnswers;
}
