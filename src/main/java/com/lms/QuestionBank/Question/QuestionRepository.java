package com.lms.QuestionBank.Question;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>{
    Collection<Question> findAllByQuestionBank_QuestionBankId(long questionBankQuestionBankId);
}
