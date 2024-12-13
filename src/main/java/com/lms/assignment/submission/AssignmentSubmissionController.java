package com.lms.assignment.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/courses/{courseId}/assignments/{assignmentId}/submissions")
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService assignmentSubmissionService;

    @Autowired
    public AssignmentSubmissionController(AssignmentSubmissionService assignmentSubmissionService) {
        this.assignmentSubmissionService = assignmentSubmissionService;
    }

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
    public AssignmentSubmission submitAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId, @RequestParam MultipartFile file) throws IOException {
        Long studentId = 123L;
        return assignmentSubmissionService.createSubmission(assignmentId, studentId, file);
    }

}
