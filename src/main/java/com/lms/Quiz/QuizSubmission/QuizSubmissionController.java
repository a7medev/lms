package com.lms.Quiz.QuizSubmission;

import com.lms.Quiz.QuizAnswer.CollectionOfQuizAnswerDTO;
import com.lms.user.User;
import com.lms.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/submissions")
@AllArgsConstructor
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;
    @PostMapping("")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> submitAnswers(@RequestBody CollectionOfQuizAnswerDTO studentAnswers, @PathVariable long quizId, @PathVariable long courseId, Principal principal)
    {
        User currentStudent = userService.getUser(principal);
        if(quizSubmissionService.checkIfAttemptedBefore(currentStudent.getId(), quizId))
            return new ResponseEntity<>("Quiz has been already attempted",HttpStatus.CONFLICT);
        this.quizSubmissionService.submitQuiz(quizId,courseId,currentStudent,studentAnswers);
        return ResponseEntity.ok().build();
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
