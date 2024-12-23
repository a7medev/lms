package com.lms.questionbank.question;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuestionDTO {
    private String questionTitle;
    private Long questionBankId;
    private String questionType;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private int correctOption;
}
