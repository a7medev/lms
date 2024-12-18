package com.lms.assignment.submission;

import com.lms.user.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/courses/{courseId}/assignments/{assignmentId}/submissions")
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService assignmentSubmissionService;

    @GetMapping
    public List<AssignmentSubmission> getSubmissions(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        return assignmentSubmissionService.getSubmissions(assignmentId);
    }

    @GetMapping("{submissionId}")
    public AssignmentSubmission getSubmission(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable Long submissionId) {
        return assignmentSubmissionService.getSubmission(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public AssignmentSubmission submitAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId, @RequestParam MultipartFile file, Principal principal) throws IOException {
        User student = principalToUser(principal);

        return assignmentSubmissionService.createSubmission(assignmentId, student, file);
    }

    @GetMapping("{submissionId}/file")
    public void getSubmissionFile(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable Long submissionId, HttpServletResponse response) throws IOException {
        Pair<InputStream, String> result = assignmentSubmissionService.getSubmissionFile(assignmentId, submissionId);
        response.setContentType(result.getSecond());
        StreamUtils.copy(result.getFirst(), response.getOutputStream());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("{submissionId}")
    public AssignmentSubmission gradeSubmission(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable Long submissionId, @RequestBody GradeRequest gradeRequest) {
        return assignmentSubmissionService.gradeSubmission(submissionId, gradeRequest.getGrade());
    }

}
