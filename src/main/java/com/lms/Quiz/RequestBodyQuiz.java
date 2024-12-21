package com.lms.Quiz;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class RequestBodyQuiz {
    private int numberOfQuestions;
    private long questionBankId;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;
}
