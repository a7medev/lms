package com.lms.quiz.quizsubmission;

import com.lms.quiz.QuizService;
import com.lms.quiz.quizanswer.QuizAnswerDTO;
import com.lms.user.User;
import com.lms.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/submit")
@AllArgsConstructor
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;
    private final QuizService quizService;
    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> submitAnswers(@RequestBody List<QuizAnswerDTO> studentAnswers, @PathVariable long quizId, @PathVariable long courseId, Principal principal)
    {
        User currentStudent = userService.getUser(principal);

        if(checkIfQuizBelongsToCourse(quizId,courseId))
            return new ResponseEntity<>("No such quiz exists.",HttpStatus.NOT_FOUND);

        Optional<QuizSubmission> submission =quizSubmissionService.getSubmission(currentStudent.getId(), quizId);
        if(submission.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(submission.get().getSubmissionState() == SubmissionState.SUBMITTED)
            return new ResponseEntity<>(Pair.of("Quiz has been already attempted",submission.get()),HttpStatus.CONFLICT);

        return ResponseEntity.ok(this.quizSubmissionService.submitQuiz(quizId,currentStudent,studentAnswers));
    }
    @GetMapping("/{submissionId}")
    public ResponseEntity<?> getSubmission(@PathVariable long submissionId,@PathVariable long quizId,@PathVariable long courseId)
    {
        if(checkIfQuizBelongsToCourse(quizId,courseId))
            return new ResponseEntity<>("No such quiz exists.",HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(this.quizSubmissionService.getQuizSubmission(submissionId,quizId));
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping
    public ResponseEntity<List<QuizSubmission>> getAllSubmissionsOfAQuiz(@PathVariable long quizId, @PathVariable long courseId)
    {
        if(checkIfQuizBelongsToCourse(quizId,courseId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No such quiz exists.");

        return ResponseEntity.ok(this.quizSubmissionService.getAllQuizSubmissions(quizId));
    }
    private boolean checkIfQuizBelongsToCourse(long quizId, long courseId) {
        return this.quizService.getQuiz(quizId,courseId).isEmpty();
    }
}
