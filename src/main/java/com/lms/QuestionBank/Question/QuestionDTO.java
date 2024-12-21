package com.lms.QuestionBank.Question;


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
    String option1,option2,option3,option4,answer;
    int correctOption;
}
