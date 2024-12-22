package com.lms.course.material;

import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.course.Course;
import com.lms.course.post.CoursePost;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
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

import static com.lms.util.AuthUtils.principalToUser;
@RequiredArgsConstructor
@Service
public class CourseMaterialService {
    public static final String MATERIAL_UPLOAD_DIR = "uploads" + File.separator + "courses" + File.separator + "%d" + File.separator + "posts" + File.separator + "%d" + File.separator + "material" + File.separator;
    private final CourseMaterialRepository courseMaterialRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    public List<CourseMaterial> getAllMaterials(Long postId) {
        return courseMaterialRepository.findAllByPostCourseUpdateId(postId);
    }

    private Path uploadPath(long courseId, long postId, String extension) {
        UUID fileId = UUID.randomUUID();
        String parent = String.format(MATERIAL_UPLOAD_DIR, courseId, postId);
        Path parentPath = Paths.get(parent);
        return parentPath.resolve(fileId + "." + extension);
    }


    public CourseMaterial uploadMaterial(long courseId, long postId, MultipartFile file) throws IOException, MessagingException {
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

        return savedMaterial;    }

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
        return courseMaterialRepository.findByPostCourseUpdateIdAndMaterialId(postId,materialId);
    }

    public void deleteMaterial(Long materialId) {
        courseMaterialRepository.deleteById(materialId);
    }

    public List<CourseMaterial> getMaterialsForCurrentStudent(Long courseId, Principal currentUser) {
        User user = principalToUser(currentUser);

        boolean isEnrolled = enrollmentRepository.findAllByUser(user).stream()
                .anyMatch(enrollment -> enrollment.getCourse().getCourseId().equals(courseId));

        if (!isEnrolled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not enrolled in this course.");
        }
        return courseMaterialRepository.findAllByCourseCourseId(courseId);
    }

    private void sendMaterialUploadNotification(Long courseId, CourseMaterial savedMaterial) throws MessagingException {
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
