package com.lms.quiz;


import com.lms.questionbank.QuestionBank;
import com.lms.questionbank.QuestionBankService;
import com.lms.quiz.quizquestiondto.QuizQuestionDTO;
import com.lms.quiz.quizsubmission.QuizSubmission;
import com.lms.quiz.quizsubmission.QuizSubmissionService;
import com.lms.user.User;
import com.lms.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("courses/{courseId}/quizzes")
@AllArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final QuestionBankService questionBankService;
    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping()
    public Collection<Quiz> getAll(@PathVariable("courseId") long courseId, @RequestParam(defaultValue = "false") boolean upcoming){
        return this.quizService.getAllQuizzes(courseId, upcoming);
    }

    @GetMapping("/{quizId}")
    public Collection<QuizQuestionDTO> startQuiz(@PathVariable("courseId") long courseId, @PathVariable("quizId") long quizId){
        return this.quizService.startQuiz(quizId, courseId);
    }
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/{quizId}/grades")
    public ResponseEntity<?> getGrades(@PathVariable("courseId") long courseId, @PathVariable("quizId") long quizId, Principal principal){
        User currentUser = this.userService.getUser(principal);
        Optional<QuizSubmission> submission = this.quizSubmissionService.checkIfAttemptedBefore(currentUser.getId(),quizId);
        return submission.map(quizSubmission -> new ResponseEntity<>("Quiz: " + quizSubmission.getQuiz().getQuizId() + "\nTotal Marks: " + quizSubmission.getMarks() + "/" + quizSubmission.getQuiz().getNumberOfQuestions(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("Quiz doesnt exist or has not been attempted yet", HttpStatus.NOT_FOUND));

    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<?> createQuiz(@PathVariable("courseId") long courseId, @RequestBody QuizDTO newQuiz){
        QuestionBank questionBank = this.questionBankService.getQuestionBank(newQuiz.getQuestionBankId(),courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No question bank found"));

        if(newQuiz.getNumberOfQuestions() > questionBank.getQuestions().size())
            return new ResponseEntity<>("Not enough questions in the question bank", HttpStatus.BAD_REQUEST);

        Quiz quiz = Quiz.builder()
                .questionBank(questionBank)
                .creationDate(newQuiz.getCreationDate())
                .startDate(newQuiz.getStartDate())
                .numberOfQuestions(newQuiz.getNumberOfQuestions())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(this.quizService.addQuiz(quiz,courseId));
    }

}
