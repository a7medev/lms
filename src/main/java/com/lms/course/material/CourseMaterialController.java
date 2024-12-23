package com.lms.course.material;

import com.lms.course.Course;
import com.lms.course.post.CoursePost;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
@EnableMethodSecurity
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "courses/{courseId}/posts/{postId}/material")
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseMaterial>> getAllMaterials(@PathVariable Long courseId, @PathVariable Long postId, Principal principal) {
        List<CourseMaterial> materials = courseMaterialService.getMaterialsForUser(courseId, postId, principal);
        return ResponseEntity.ok(materials);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{materialId}")
    public CourseMaterial getMaterialById(@PathVariable Long courseId, @PathVariable Long postId, @PathVariable Long materialId, Principal principal) {
        return courseMaterialService.getMaterialByIdForUser(courseId, postId, materialId, principal);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CourseMaterial> uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException{
        CourseMaterial uploadedMaterial = courseMaterialService.uploadMaterial(courseId, postId, file,principal);
        return new ResponseEntity<>(uploadedMaterial, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{materialId}/file")
    public void getMaterialFile(@PathVariable Long courseId, @PathVariable Long materialId, HttpServletResponse response,Principal principal) throws IOException {
        Pair<InputStream, String> result = courseMaterialService.getMaterialFile(courseId,materialId,principal);
        response.setContentType(result.getSecond());
        StreamUtils.copy(result.getFirst(), response.getOutputStream());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @DeleteMapping("/{materialId}")
    public void deletePost(@PathVariable Long courseId, @PathVariable Long postId, @PathVariable Long materialId,Principal principal) {
        courseMaterialService.deleteMaterial(courseId,postId,materialId,principal);
    }
}
