package com.lms.course.lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByCourseCourseId(Long courseId);
    Optional<Lesson> findByCourseCourseIdAndId(Long courseId, Long lessonId);
}

