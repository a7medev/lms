package com.lms.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, AttendanceKey> {

    List<AttendanceRecord> findByIdCourseIdAndIdLessonId(Long courseId, Long lessonId);
    boolean existsById(AttendanceKey id); 
}
