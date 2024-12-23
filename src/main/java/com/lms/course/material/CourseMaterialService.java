package com.lms.course.material;

import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.course.Course;
import com.lms.course.post.CoursePost;
import com.lms.course.post.CoursePostRepository;
import com.lms.course.post.CoursePostService;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.Role;
import com.lms.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@Service
public class CourseMaterialService {
    public static final String MATERIAL_UPLOAD_DIR = "uploads" + File.separator + "courses" + File.separator + "%d" + File.separator + "posts" + File.separator + "%d" + File.separator + "material" + File.separator;
    private final CourseMaterialRepository courseMaterialRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    public List<CourseMaterial> getMaterialsForUser(Long courseId, Long postId, Principal principal) {
        User user = principalToUser(principal);
        switch (user.getRole()) {
            case ADMIN:
                return courseMaterialRepository.findAll();
            case INSTRUCTOR:
                return courseMaterialRepository.findAllByPost_Course_Instructor(user);
            case STUDENT:
                checkStudentEnrollment(user, courseId);
                return courseMaterialRepository.findAllByPost_CourseCourseIdAndPost_CourseUpdateId(courseId, postId);
            default:
                throw new IllegalStateException("Unauthorized role");
        }
    }

    public CourseMaterial getMaterialByIdForUser(Long courseId, Long postId, Long materialId, Principal principal) {
        User user = principalToUser(principal);
        switch (user.getRole()) {
            case ADMIN:
                return getMaterialById(materialId);
            case INSTRUCTOR:
                return getInstructorMaterial(courseId, postId, materialId, user);
            case STUDENT:
                checkStudentEnrollment(user, courseId);
                return getStudentMaterial(courseId, postId, materialId);
            default:
                throw new IllegalStateException("Unauthorized role");
        }
    }

    private void checkStudentEnrollment(User user, Long courseId) {
        boolean isEnrolled = enrollmentRepository.findAllByUser(user).stream()
                .anyMatch(enrollment -> enrollment.getCourse().getCourseId().equals(courseId));
        if (!isEnrolled) {
            throw new IllegalStateException("You are not enrolled in this course.");
        }
    }

    private CourseMaterial getInstructorMaterial(Long courseId, Long postId, Long materialId, User user) {
        CourseMaterial material = courseMaterialRepository.findByMaterialIdAndPost_Course_Instructor(materialId, user);
        if (material == null || !material.getPost().getCourse().getCourseId().equals(courseId)) {
            throw new IllegalStateException("You don't have access to this material");
        }
        return material;
    }

    private CourseMaterial getStudentMaterial(Long courseId, Long postId, Long materialId) {
        return courseMaterialRepository.findByMaterialIdAndPost_CourseCourseIdAndPost_CourseUpdateId(materialId, courseId, postId)
                .orElseThrow(() -> new IllegalStateException("Material not found"));
    }

    private CourseMaterial getMaterialById(Long materialId) {
        return courseMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }

    private Path uploadPath(long courseId, long postId, String extension) {
        UUID fileId = UUID.randomUUID();
        String parent = String.format(MATERIAL_UPLOAD_DIR, courseId, postId);
        Path parentPath = Paths.get(parent);
        return parentPath.resolve(fileId + "." + extension);
    }


    public CourseMaterial uploadMaterial(long courseId, long postId, MultipartFile file, Principal principal) throws IOException {
        User user = principalToUser(principal);

        if (!(user.getRole() == Role.ADMIN || user.getRole() == Role.INSTRUCTOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to upload materials");
        }

        if (user.getRole() == Role.INSTRUCTOR) {
            boolean isInstructorForCourse = courseMaterialRepository.existsByPost_Course_CourseIdAndPost_Course_Instructor(courseId, user);
            if (!isInstructorForCourse) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the instructor for this course");
            }
        }

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
        }
        CourseMaterial courseMaterial;
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            Path materialPath = uploadPath(courseId, postId, extension);
            Files.createDirectories(materialPath.getParent()); // Ensure the directory exists
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, materialPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // prepare entity
            Course course = new Course();
            course.setCourseId(courseId);

            CoursePost post = new CoursePost();
            post.setCourseUpdateId(postId);
            post.setCourse(course);

            courseMaterial = new CourseMaterial();
            courseMaterial.setPost(post);
            courseMaterial.setFileLocation(String.valueOf(materialPath));
            courseMaterial.setContentType(file.getContentType());

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File could not be uploaded");
        }

        CourseMaterial savedMaterial = courseMaterialRepository.save(courseMaterial);
        sendMaterialUploadNotification(courseId, savedMaterial);

        return savedMaterial;
    }

    public Pair<InputStream, String> getMaterialFile(Long materialId) throws FileNotFoundException {
        CourseMaterial material = courseMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
        Path filePath = Paths.get(material.getFileLocation());
        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found on server");
        }
        String contentType = material.getContentType();
        return Pair.of(new FileInputStream(filePath.toFile()), contentType);
    }

    public Optional<CourseMaterial> getMaterialById(Long postId, Long materialId) {
        return courseMaterialRepository.findByPostCourseUpdateIdAndMaterialId(postId, materialId);
    }

    public void deleteMaterial(Long courseId,Long postId,Long materialId,Principal principal) {
        User user = principalToUser(principal);

        CourseMaterial material = courseMaterialRepository.findByMaterialIdAndPost_CourseCourseIdAndPost_CourseUpdateId(
                        materialId, courseId, postId)
                .orElseThrow(() -> new IllegalStateException("Material not found"));

        if (user.getRole() == Role.INSTRUCTOR) {
            boolean isInstructorForCourse = material.getPost().getCourse().getInstructor().getId() == user.getId();
            if (!isInstructorForCourse) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this material");
            }
        }
        if (user.getRole() == Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this material");
        }
        // Delete the physical file
        try {
            Path filePath = Paths.get(material.getFileLocation());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete file: " + e.getMessage());
        }

        courseMaterialRepository.deleteById(materialId);
    }

    private void sendMaterialUploadNotification(Long courseId, CourseMaterial savedMaterial) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseCourseId(courseId);
        String notificationMessage = "New material uploaded for Course " + savedMaterial.getPost().getCourse().getTitle();

        for (Enrollment enrollment : enrollments) {
            User student = enrollment.getUser();
            Notification notification = Notification.builder()
                    .user(student)
                    .message(notificationMessage)
                    .build();
            notificationService.saveNotification(notification, "New Material Uploaded");
        }
    }
}
