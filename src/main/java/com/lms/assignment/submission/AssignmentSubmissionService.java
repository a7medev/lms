package com.lms.assignment.submission;

import com.lms.assignment.Assignment;
import com.lms.assignment.AssignmentService;
import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.Role;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final AssignmentService assignmentService;
    private final NotificationService notificationService;

    public static final String SUBMISSION_LOCATION_FORMAT = "uploads" + File.separator + "assignments" + File.separator + "%d" + File.separator + "submissions" + File.separator;
    private final CourseRepository courseRepository;

    private Path submissionPath(long assignmentId, String extension) {
        UUID fileId = UUID.randomUUID();

        String parent = String.format(SUBMISSION_LOCATION_FORMAT, assignmentId);

        Path parentPath = Paths.get(parent);

        return parentPath.resolve(fileId + "." + extension);
    }

    public List<AssignmentSubmission> getSubmissions(Long courseId, Long assignmentId, User user) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (user.getRole() != Role.ADMIN && course.getInstructor().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return assignmentSubmissionRepository.findAllByAssignmentId(assignmentId);
    }

    public AssignmentSubmission getSubmission(Long submissionId, User user) {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        Course course = submission.getAssignment().getCourse();

        if (user.getRole() != Role.ADMIN && course.getInstructor().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return submission;
    }

    public AssignmentSubmission createSubmission(Long assignmentId, User student, MultipartFile file) throws IOException {
        Assignment assignment = assignmentService.getAssignment(assignmentId, student);

        boolean hasSubmittedBefore = assignmentSubmissionRepository.existsByAssignmentIdAndStudentId(assignmentId, student.getId());

        if (hasSubmittedBefore) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment already exists");
        }

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

        AssignmentSubmission createdSubmission = assignmentSubmissionRepository.save(submission);

        sendSubmissionNotification(submission);

        return createdSubmission;
    }

    private void sendSubmissionNotification(AssignmentSubmission submission) {
        Assignment assignment = submission.getAssignment();
        Course course = assignment.getCourse();
        User student = submission.getStudent();
        User instructor = course.getInstructor();

        String subject = "LMS - New Assignment Submission";
        String message = String.format("%s submitted assignment \"%s\" for the \"%s\" course.", student.getName(), assignment.getTitle(), course.getTitle());
        Notification notification = Notification.builder()
                .message(message)
                .user(instructor)
                .build();

        notificationService.saveNotification(notification, subject);
    }

    public Pair<InputStream, String> getSubmissionFile(Long assignmentId, Long submissionId, User user) throws FileNotFoundException {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        boolean hasAccess = switch (user.getRole()) {
            case STUDENT -> submission.getStudent().getId() == user.getId();
            case INSTRUCTOR -> submission.getAssignment().getCourse().getInstructor().getId() == user.getId();
            case ADMIN -> true;
        };

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        String filePath = String.format(SUBMISSION_LOCATION_FORMAT, assignmentId) + submission.getFileLocation();

        return Pair.of(new FileInputStream(filePath), submission.getContentType());
    }

    public AssignmentSubmission gradeSubmission(Long submissionId, int grade, User user) {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (submission.getAssignment().getCourse().getInstructor().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        submission.setScore(grade);

        assignmentSubmissionRepository.save(submission);

        sendGradingNotification(submission);

        return submission;
    }

    private void sendGradingNotification(AssignmentSubmission submission) {
        User student = submission.getStudent();
        Assignment assignment = submission.getAssignment();
        Course course = assignment.getCourse();

        String subject = "LMS - Your Assignment Submission has been Graded";
        String message = String.format("You scored %d in the assignment \"%s\" for the \"%s\" course.", submission.getScore(), assignment.getTitle(), course.getTitle());
        Notification notification = Notification.builder()
                .message(message)
                .user(student)
                .build();

        notificationService.saveNotification(notification, subject);
    }

}
