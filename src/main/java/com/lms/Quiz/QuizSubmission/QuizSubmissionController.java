package com.lms.Quiz.QuizSubmission;

import com.lms.Quiz.QuizAnswer.QuizAnswerDTO;
import com.lms.user.User;
import com.lms.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/submissions")
@AllArgsConstructor
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;
    @PostMapping("")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> submitAnswers(@RequestBody List<QuizAnswerDTO> studentAnswers, @PathVariable long quizId, @PathVariable long courseId, Principal principal)
    {
        User currentStudent = userService.getUser(principal);
        Optional<QuizSubmission> check =quizSubmissionService.checkIfAttemptedBefore(currentStudent.getId(), quizId);

        if(check.isPresent())
            return new ResponseEntity<>(Pair.of("Quiz has been already attempted",check.get()),HttpStatus.CONFLICT);

        return ResponseEntity.ok(this.quizSubmissionService.submitQuiz(quizId,courseId,currentStudent,studentAnswers));
    }
    @GetMapping("/{submissionId}")
    public ResponseEntity<?> getSubmission(long submissionId, long quizId)
    {
        return ResponseEntity.ok(this.quizSubmissionService.getQuizSubmission(submissionId,quizId));
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    public ResponseEntity<List<QuizSubmission>> getAllSubmissionsOfAQuiz(@PathVariable long quizId)
    {
        return ResponseEntity.ok(this.quizSubmissionService.getAllQuizSubmissions(quizId));
    }
}
