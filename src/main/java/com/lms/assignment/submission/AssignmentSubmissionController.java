package com.lms.assignment.submission;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    @GetMapping("{submissionId}/file")
    public void getSubmissionFile(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable Long submissionId, HttpServletResponse response) throws IOException {
        Pair<InputStream, String> result = assignmentSubmissionService.getSubmissionFile(assignmentId, submissionId);
        response.setContentType(result.getSecond());
        StreamUtils.copy(result.getFirst(), response.getOutputStream());
    }

}
