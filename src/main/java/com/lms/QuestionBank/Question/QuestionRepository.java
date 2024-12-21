package com.lms.QuestionBank.Question;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>{

    Collection<Question> findAllByQuestionBank_QuestionBankId(long questionBankQuestionBankId);
    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT :limit WHERE question_bank_id = :questionBankId ;", nativeQuery = true)
    List<Question> getRandomQuestions(@Param("limit") int limit, @Param("questionBankId")long questionBankId);
    void deleteByQuestionIdAndQuestionBank_QuestionBankId(long questionId, long questionBankId);
    Optional<Question> findQuestionByQuestionIdAndQuestionBank_QuestionBankId(long questionId, long questionBankQuestionBankId);
}
