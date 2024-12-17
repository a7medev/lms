package com.lms.course.material;

import com.lms.course.Course;
import com.lms.course.post.CoursePost;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(path = "courses/{courseId}/posts/{postId}/material")
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @Autowired
    public CourseMaterialController(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @GetMapping
    public ResponseEntity<List<CourseMaterial>> getAllMaterials(@PathVariable String courseId, @PathVariable String postId) {
        List<CourseMaterial> materials = courseMaterialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{materialId}")
    public CourseMaterial getMaterialById(@PathVariable Long materialId, @PathVariable String courseId, @PathVariable String postId) {
        return courseMaterialService.getMaterialById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material for this post not found"));
    }

    @PostMapping
    public ResponseEntity<CourseMaterial> uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file) throws IOException {
        CourseMaterial uploadedMaterial = courseMaterialService.uploadMaterial(courseId, postId, file);
        return new ResponseEntity<>(uploadedMaterial, HttpStatus.CREATED);
    }

    @GetMapping("/{materialId}/file")
    public void getMaterialFile(@PathVariable Long courseId, @PathVariable Long postId, @PathVariable Long materialId, HttpServletResponse response) throws IOException {
        Pair<InputStream, String> result = courseMaterialService.getMaterialFile(materialId);
        response.setContentType(result.getSecond());
        StreamUtils.copy(result.getFirst(), response.getOutputStream());
    }

    @DeleteMapping("/{materialId}")
    public void deletePost(@PathVariable Long materialId, @PathVariable String courseId, @PathVariable String postId) {
        courseMaterialService.deleteMaterial(materialId);
    }
}
