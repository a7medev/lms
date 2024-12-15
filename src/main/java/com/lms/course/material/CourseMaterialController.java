package com.lms.course.material;

import com.lms.course.Course;
import com.lms.course.post.CoursePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "courses/{courseId}/posts/{postId}/")
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @Autowired
    public CourseMaterialController(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @GetMapping("material")
    public ResponseEntity<List<CourseMaterial>> getAllMaterials(@PathVariable String courseId, @PathVariable String postId) {
        List<CourseMaterial> materials = courseMaterialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("material/{materialId}")
    public CourseMaterial getMaterialById(@PathVariable Long courseId, @PathVariable Long postId, @PathVariable Long materialId) {
        return courseMaterialService.getMaterialById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material for this post not found"));
    }

    @PostMapping("upload")
    public ResponseEntity<CourseMaterial> uploadMaterial(@PathVariable Long courseId, @PathVariable Long postId, @RequestBody CourseMaterial courseMaterial) {
        Course course = new Course();
        course.setCourseId(courseId);

        CoursePost post = new CoursePost();
        post.setCourseUpdateId(postId);
        post.setCourse(course);

        courseMaterial.setPost(post);
        CourseMaterial uploadedMaterial = courseMaterialService.uploadMaterial(courseMaterial);
        return new ResponseEntity<>(uploadedMaterial, HttpStatus.CREATED);
    }

    @DeleteMapping("material/{materialId}")
    public void deletePost(@PathVariable Long courseId, @PathVariable Long postId, @PathVariable Long materialId) {
        courseMaterialService.deleteMaterial(materialId);
    }
}
