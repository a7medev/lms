package com.lms.QuestionBank.Question;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
/*@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)*/
public interface QuestionRepository extends JpaRepository<Question,Long>{
    Collection<Question> findAllByQuestionBank_QuestionBankId(long questionBankQuestionBankId);
}
