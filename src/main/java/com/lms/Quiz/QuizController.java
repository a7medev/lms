package com.lms.Quiz;


import com.lms.QuestionBank.QuestionBank;
import com.lms.QuestionBank.QuestionBankService;
import com.lms.Quiz.QuizSubmission.QuizSubmission;
import com.lms.Quiz.QuizSubmission.QuizSubmissionService;
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
    private QuizService quizService;
    private QuestionBankService questionBankService;
    private QuizSubmissionService quizSubmissionService;
    private UserService userService;
    @GetMapping()
    public Collection<Quiz> getAll(@PathVariable("courseId") long courseId, @RequestParam(defaultValue = "false") boolean upcoming){
        return this.quizService.getAllQuizzes(courseId, upcoming);
    }
    @GetMapping("/{quizId}")
    public Collection<QuizQuestionDTO> startQuiz(@PathVariable("courseId") long courseId, @PathVariable("quizId") long quizId){
        return this.quizService.startQuiz(quizId, courseId);
    }
    @GetMapping("/{quizId}/grades")
    public ResponseEntity<?> startQuiz(@PathVariable("courseId") long courseId, @PathVariable("quizId") long quizId, Principal principal){
        User currentUser = this.userService.getUser(principal);
        Optional<QuizSubmission> submission = this.quizSubmissionService.checkIfAttemptedBefore(currentUser.getId(),quizId);
        if(submission.isPresent())
            return new ResponseEntity<>("Quiz: " + submission.get().getQuiz().getQuizId() + "\nTotal Marks: " + submission.get().getMarks() + "/" + submission.get().getQuiz().getNumberOfQuestions(), HttpStatus.OK);
        return new ResponseEntity<>("Quiz doesnt exist or has not been attempted yet", HttpStatus.NOT_FOUND);

    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
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
