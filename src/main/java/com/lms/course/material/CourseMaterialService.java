package com.lms.course.material;

import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.course.Course;
import com.lms.course.post.CoursePost;
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
public class CourseMaterialService {
    public static final String MATERIAL_UPLOAD_DIR = "uploads" + File.separator + "courses" + File.separator + "%d" + File.separator + "posts" + File.separator + "%d" + File.separator + "material" + File.separator;
    private final CourseMaterialRepository courseMaterialRepository;

    @Autowired
    public CourseMaterialService(CourseMaterialRepository courseMaterialRepository) {
        this.courseMaterialRepository = courseMaterialRepository;
    }

    public List<CourseMaterial> getAllMaterials() {
        return courseMaterialRepository.findAll();
    }

    private Path uploadPath(long courseId, long postId, String extension) {
        UUID fileId = UUID.randomUUID();
        String parent = String.format(MATERIAL_UPLOAD_DIR, courseId, postId);
        Path parentPath = Paths.get(parent);
        return parentPath.resolve(fileId + "." + extension);
    }


    public CourseMaterial uploadMaterial(long courseId, long postId, MultipartFile file) throws IOException {
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

        return courseMaterialRepository.save(courseMaterial);
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

    public Optional<CourseMaterial> getMaterialById(Long id) {
        return courseMaterialRepository.findById(id);
    }

    public void deleteMaterial(Long materialId) {
        courseMaterialRepository.deleteById(materialId);
    }
}
