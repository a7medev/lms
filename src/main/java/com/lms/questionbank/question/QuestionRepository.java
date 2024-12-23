package com.lms.questionbank.question;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>{

    Collection<Question> findAllByQuestionBankQuestionBankId(long questionBankQuestionBankId);
    @Query(value = "SELECT * FROM question WHERE question_bank_id = :questionBankId ORDER BY RAND() LIMIT :limit ;", nativeQuery = true)
    List<Question> getRandomQuestions(@Param("limit") int limit, @Param("questionBankId")long questionBankId);
    void deleteByQuestionIdAndQuestionBankQuestionBankId(long questionId, long questionBankId);
    Optional<Question> findQuestionByQuestionIdAndQuestionBankQuestionBankId(long questionId, long questionBankQuestionBankId);
}
