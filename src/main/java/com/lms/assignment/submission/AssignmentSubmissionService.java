package com.lms.assignment.submission;

import com.lms.assignment.Assignment;
import com.lms.assignment.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentSubmissionService {
    final AssignmentSubmissionRepository assignmentSubmissionRepository;
    final AssignmentService assignmentService;

    public static final String SUBMISSION_LOCATION_FORMAT = "uploads/assignments/%d/submissions/%s.%s";

    @Autowired
    public AssignmentSubmissionService(AssignmentSubmissionRepository assignmentSubmissionRepository, AssignmentService assignmentService) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentService = assignmentService;
    }

    private Path submissionPath(long assignmentId, String extension) {
        UUID fileId = UUID.randomUUID();

        String location = String.format(SUBMISSION_LOCATION_FORMAT, assignmentId, fileId, extension);

        return Path.of(location);
    }

    public List<AssignmentSubmission> getSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.findAllByAssignmentId(assignmentId);
    }

    public Optional<AssignmentSubmission> getSubmission(Long submissionId) {
        return assignmentSubmissionRepository.findById(submissionId);
    }

    public AssignmentSubmission createSubmission(Long assignmentId, Long studentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentService.getAssignment(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        Path submissionPath = submissionPath(assignment.getId(), extension);
        Files.createDirectories(submissionPath.getParent());
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, submissionPath, StandardCopyOption.REPLACE_EXISTING);
        }

        AssignmentSubmission submission = new AssignmentSubmission.AssignmentSubmissionBuilder()
                .assignment(assignment)
                .studentId(studentId)
                .fileLocation(submissionPath.toString())
                .build();

        return assignmentSubmissionRepository.save(submission);
    }

}
