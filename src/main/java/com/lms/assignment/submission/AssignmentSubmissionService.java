package com.lms.assignment.submission;

import com.lms.assignment.Assignment;
import com.lms.assignment.AssignmentService;
import com.lms.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentSubmissionService {
    final AssignmentSubmissionRepository assignmentSubmissionRepository;
    final AssignmentService assignmentService;

    public static final String SUBMISSION_LOCATION_FORMAT = "uploads" + File.separator + "assignments" + File.separator + "%d" + File.separator + "submissions" + File.separator;

    @Autowired
    public AssignmentSubmissionService(AssignmentSubmissionRepository assignmentSubmissionRepository, AssignmentService assignmentService) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentService = assignmentService;
    }

    private Path submissionPath(long assignmentId, String extension) {
        UUID fileId = UUID.randomUUID();

        String parent = String.format(SUBMISSION_LOCATION_FORMAT, assignmentId);

        Path parentPath = Paths.get(parent);

        return parentPath.resolve(fileId + "." + extension);
    }

    public List<AssignmentSubmission> getSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.findAllByAssignmentId(assignmentId);
    }

    public Optional<AssignmentSubmission> getSubmission(Long submissionId) {
        return assignmentSubmissionRepository.findById(submissionId);
    }

    public AssignmentSubmission createSubmission(Long assignmentId, User student, MultipartFile file) throws IOException {
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
                .student(student)
                .contentType(file.getContentType())
                .fileLocation(submissionPath.getFileName().toString())
                .build();

        return assignmentSubmissionRepository.save(submission);
    }

    public Pair<InputStream, String> getSubmissionFile(Long assignmentId, Long submissionId) throws FileNotFoundException {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
        String filePath = String.format(SUBMISSION_LOCATION_FORMAT, assignmentId) + submission.getFileLocation();

        return Pair.of(new FileInputStream(filePath), submission.getContentType());
    }

    public AssignmentSubmission gradeSubmission(Long submissionId, int grade) {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        submission.setScore(grade);

        return assignmentSubmissionRepository.save(submission);
    }

}
