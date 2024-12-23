package com.lms.quiz;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class QuizDTO {
    private int numberOfQuestions;
    private long questionBankId;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;
}
